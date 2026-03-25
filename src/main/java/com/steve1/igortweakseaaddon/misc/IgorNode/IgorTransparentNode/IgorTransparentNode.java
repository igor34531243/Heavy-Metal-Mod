package com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorElementInterface;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorNodeConnection;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorNodeInterface;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.Eln;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.NodeBase;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.sim.ElectricalConnection;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalConnection;
import mods.eln.sim.ThermalLoad;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Field;
import java.util.HashMap;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.plot_speed;

public class IgorTransparentNode extends TransparentNode implements IgorNodeInterface {

    Field isAdded;

    public HashMap<Integer, PneumaticConnection> pneumatic_connections_map = new HashMap<>();
    public HashMap<Integer,Double> pneumatic_speeds_map = new HashMap<>();
    public HashMap<Integer,Boolean> pneumatic_isA_map = new HashMap<>();

    public IgorTransparentNode() {
        super();
        pneumatic_connections_map = new HashMap<>();
        pneumatic_speeds_map = new HashMap<>();
        pneumatic_isA_map = new HashMap<>();
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

    @Override
    public void disconnectJob() {
        updatePneumaticConnections();
        super.disconnectJob();
    }

    public void connectJob_mirror() {
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
            if (nodeA instanceof IgorNodeInterface && nodeB instanceof IgorNodeInterface) {
                PneumaticLoad pload;
                if ((pload = ((IgorNodeInterface)nodeA).getPneumaticLoad(directionA, lrduA, mskB)) != null) {
                    PneumaticLoad otherPLoad = ((IgorNodeInterface)nodeB).getPneumaticLoad(directionB, lrduB, mskA);

                    if (otherPLoad != null) {

                        pCon = new PneumaticConnection(pload, otherPLoad);

                        pCon.load_stats();

                        IgorNodeInterface.linkPneumaticConnection((IgorNodeInterface) nodeA,directionA,lrduA, (IgorNodeInterface) nodeB,directionB,lrduB,pCon);

                        pneumatic_simulator.addPneumaticComponent(pCon);
                        nodeConnection.addConnection(pCon);
                    }
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        updatePneumaticConnections();
        {
            NBTTagCompound inbt = new NBTTagCompound();
            for (int intid : pneumatic_speeds_map.keySet()) {
                inbt.setDouble(Integer.toString(intid),pneumatic_speeds_map.get(intid));
            }
            nbt.setTag("pneumatic_speeds_map",inbt);
        }
        {
            NBTTagCompound inbt = new NBTTagCompound();
            for (int intid : pneumatic_isA_map.keySet()) {
                inbt.setBoolean(Integer.toString(intid),pneumatic_isA_map.get(intid));
            }
            nbt.setTag("pneumatic_isA_map",inbt);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        pneumatic_speeds_map.clear();
        if (nbt.hasKey("pneumatic_speeds_map")) {
            NBTTagCompound inbt = nbt.getCompoundTag("pneumatic_speeds_map");
            for (Object str_intid : inbt.func_150296_c()) {
                int intid = Integer.parseInt((String) str_intid);
                double speed = inbt.getDouble((String) str_intid);
                pneumatic_speeds_map.put(intid, speed);
            }
        }
        pneumatic_isA_map.clear();
        if (nbt.hasKey("pneumatic_isA_map")) {
            NBTTagCompound inbt = nbt.getCompoundTag("pneumatic_isA_map");
            for (Object str_intid : inbt.func_150296_c()) {
                int intid = Integer.parseInt((String) str_intid);
                boolean isA = inbt.getBoolean((String) str_intid);
                pneumatic_isA_map.put(intid, isA);
            }
        }
        super.readFromNBT(nbt);
    }

    @Override
    public PneumaticLoad getPneumaticLoad(Direction direction,LRDU lrdu,int intMask) {
        if (!(element instanceof IgorElementInterface)) {
            return null;
        }
        return ((IgorElementInterface)element).getPneumaticLoad(direction,lrdu);
    }

    @Override
    public void clearPneumaticConnections() {
        pneumatic_speeds_map.clear();
        pneumatic_connections_map.clear();
        pneumatic_isA_map.clear();
    }

    @Override
    public void addPneumaticConnection(Direction direction,LRDU lrdu, PneumaticConnection connection) {
        pneumatic_connections_map.put(IgorNodeInterface.ppos_to_int(direction,lrdu),connection);
    }

    @Override
    public void addPneumaticConnectionSpeed(Direction direction,LRDU lrdu, Double speed) {
        pneumatic_speeds_map.put(IgorNodeInterface.ppos_to_int(direction,lrdu),speed);
    }

    @Override
    public void addPneumaticConnectionIsA(Direction direction,LRDU lrdu, Boolean isA) {
        pneumatic_isA_map.put(IgorNodeInterface.ppos_to_int(direction,lrdu),isA);
    }

    @Override
    public PneumaticConnection getPneumaticConnection(Direction direction,LRDU lrdu) {
        return pneumatic_connections_map.get(IgorNodeInterface.ppos_to_int(direction,lrdu));
    }

    @Override
    public Double getPneumaticConnectionSpeed(Direction direction,LRDU lrdu) {
        return pneumatic_speeds_map.get(IgorNodeInterface.ppos_to_int(direction,lrdu));
    }

    @Override
    public Boolean getPneumaticConnectionIsA(Direction direction,LRDU lrdu) {
        return pneumatic_isA_map.get(IgorNodeInterface.ppos_to_int(direction,lrdu));
    }

    @Override
    public void updatePneumaticConnections() {
        for (int intid : pneumatic_connections_map.keySet()) {
            PneumaticConnection connection = pneumatic_connections_map.get(intid);
            double speed=connection.get_speed();
            pneumatic_speeds_map.put(intid,speed);
        }
    }
}
