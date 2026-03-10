package com.steve1.igortweakseaaddon.misc.IgorTransparentNode;

import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.Eln;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.Node;
import mods.eln.node.NodeBase;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.sim.ElectricalConnection;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalConnection;
import mods.eln.sim.ThermalLoad;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;

public class IgorTransparentNode extends TransparentNode {

    Field isAdded;

    public IgorTransparentNode() {
        try {
            isAdded=NodeBase.class.getDeclaredField("isAdded");
            isAdded.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void connectJob() {
        connectJob_mirror();
        element.connectJob();
    }

    public void connectJob_mirror() {
        // EXTERNAL OTHERS SIXNODE
        try {
            for (Direction dir : Direction.values()) {
                NodeBase otherNode = getNeighbor(dir);
                if (otherNode != null && ((Boolean)isAdded.get(otherNode))) {
                    for (LRDU lrdu : LRDU.values()) {
                        tryConnectTwoIgorNode(this, dir, lrdu, otherNode, dir.getInverse(), lrdu.inverseIfLR());
                    }
                }

            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public static void tryConnectTwoIgorNode(NodeBase nodeA, Direction directionA, LRDU lrduA, NodeBase nodeB, Direction directionB, LRDU lrduB) {
        int mskA = nodeA.getSideConnectionMask(directionA, lrduA);
        int mskB = nodeB.getSideConnectionMask(directionB, lrduB);
        if (compareConnectionMask(mskA, mskB)) {
            ElectricalConnection eCon = null;
            ThermalConnection tCon = null;
            PneumaticConnection pCon = null;

            IgorNodeConnection nodeConnection = new IgorNodeConnection(nodeA, directionA, lrduA, nodeB, directionB, lrduB);

            nodeA.nodeConnectionList.add(nodeConnection);
            nodeB.nodeConnectionList.add(nodeConnection);

            nodeA.setNeedPublish(true);
            nodeB.setNeedPublish(true);

            nodeA.lrduCubeMask.set(directionA, lrduA, true);
            nodeB.lrduCubeMask.set(directionB, lrduB, true);

            nodeA.newConnectionAt(nodeConnection, true);
            nodeB.newConnectionAt(nodeConnection, false);

            ElectricalLoad eLoad;
            if ((eLoad = nodeA.getElectricalLoad(directionA, lrduA, mskB)) != null) {

                ElectricalLoad otherELoad = nodeB.getElectricalLoad(directionB, lrduB, mskA);
                if (otherELoad != null) {
                    eCon = new ElectricalConnection(eLoad, otherELoad);

                    Eln.simulator.addElectricalComponent(eCon);
                    nodeConnection.addConnection(eCon);
                }
            }
            ThermalLoad tLoad;
            if ((tLoad = nodeA.getThermalLoad(directionA, lrduA, mskB)) != null) {

                ThermalLoad otherTLoad = nodeB.getThermalLoad(directionB, lrduB, mskA);
                if (otherTLoad != null) {
                    tCon = new ThermalConnection(tLoad, otherTLoad);

                    Eln.simulator.addThermalConnection(tCon);
                    nodeConnection.addConnection(tCon);
                }

            }
            if (nodeA instanceof IgorTransparentNode && nodeB instanceof IgorTransparentNode) {
                PneumaticLoad pload;
                if ((pload = ((IgorTransparentNode)nodeA).getPneumaticLoad(directionA, lrduA, mskB)) != null) {

                    PneumaticLoad otherPLoad = ((IgorTransparentNode)nodeB).getPneumaticLoad(directionB, lrduB, mskA);

                    if (otherPLoad != null) {

                        pCon = new PneumaticConnection(pload, otherPLoad);

                        pneumatic_simulator.addPneumaticComponent(pCon);
                        nodeConnection.addConnection(pCon);
                    }
                }
            }
        }
    }

    public PneumaticLoad getPneumaticLoad(Direction direction,LRDU lrdu,int intMask) {
        return ((IgorTransparentNodeElement)element).getPneumaticLoad(direction,lrdu);
    }

    @Override
    public void initializeFromThat(Direction side, EntityLivingBase entityLiving, ItemStack itemStack) {
        try {
            // Direction front = null;
            TransparentNodeDescriptor descriptor = igorTransparentNodeItem.getDescriptor(itemStack);
            /*
             * switch(descriptor.getFrontType()) { case BlockSide: front = side; break; case PlayerView: front = Utils.entityLivingViewDirection(entityLiving).getInverse(); break; case PlayerViewHorizontal: front = Utils.entityLivingHorizontalViewDirection(entityLiving).getInverse(); break;
             *
             * }
             */

            int metadata = itemStack.getItemDamage();
            elementId = metadata;
            element = (TransparentNodeElement) descriptor.ElementClass.getConstructor(TransparentNode.class, TransparentNodeDescriptor.class).newInstance(this, descriptor);
            element.initializeFromThat(side, entityLiving, itemStack.getTagCompound());
        } catch (InstantiationException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (InvocationTargetException e) {

            e.printStackTrace();
        } catch (NoSuchMethodException e) {

            e.printStackTrace();
        } catch (SecurityException e) {

            e.printStackTrace();
        }

        Utils.println("TN.iFT element = " + element + " elId = " + elementId);

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        readFromNBT_stage1(nbt.getCompoundTag("node"));

        elementId = nbt.getShort("eid");
        try {
            TransparentNodeDescriptor descriptor = igorTransparentNodeItem.getDescriptor(elementId);
            element = (TransparentNodeElement) descriptor.ElementClass.getConstructor(TransparentNode.class, TransparentNodeDescriptor.class).newInstance(this, descriptor);
        } catch (InstantiationException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (InvocationTargetException e) {

            e.printStackTrace();
        } catch (NoSuchMethodException e) {

            e.printStackTrace();
        } catch (SecurityException e) {

            e.printStackTrace();
        }
        element.readFromNBT(nbt.getCompoundTag("element"));

    }

    public void readFromNBT_stage1(NBTTagCompound nbt) {
        readFromNBT_stage2(nbt);
        try {
            //lastLight = nbt.getByte("lastLight");
            Field lastLight = Node.class.getDeclaredField("lastLight");
            lastLight.setAccessible(true);
            lastLight.set(this,nbt.getByte("lastLight"));

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public void readFromNBT_stage2(NBTTagCompound nbt) {
        try {
            Field initialized = NodeBase.class.getDeclaredField("initialized");
            initialized.setAccessible(true);

            coordonate.readFromNBT(nbt, "c");

            neighborOpaque = nbt.getByte("NBOpaque");
            neighborWrapable = nbt.getByte("NBWrap");

            initialized.set(this,true);
            //initialized = true;

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNodeUuid() {
        return igorTransparentNodeBlock.getNodeUuid();
    }
}
