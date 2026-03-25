package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorElementInterface;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorNodeConnection;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorNodeInterface;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorNodeInterface.*;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.Eln;
import mods.eln.misc.Coordonate;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBase;
import mods.eln.node.NodeManager;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeElement;
import mods.eln.sim.ElectricalConnection;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalConnection;
import mods.eln.sim.ThermalLoad;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.pneumatic_simulator;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.plot_speed;

public class IgorSixNode extends SixNode implements IgorNodeInterface {

    Field isAdded_field;

    public HashMap<Integer, PneumaticConnection> pneumatic_connections_map = new HashMap<>();
    public HashMap<Integer,Double> pneumatic_speeds_map = new HashMap<>();
    public HashMap<Integer,Boolean> pneumatic_isA_map = new HashMap<>();

    public HashMap<Integer, PneumaticConnection> internal_pneumatic_connections_map = new HashMap<>();
    public HashMap<Integer,Double> internal_pneumatic_speeds_map = new HashMap<>();

    public ArrayList<PneumaticConnection> internalPneumaticConnectionList = new ArrayList<PneumaticConnection>(1);

    public IgorSixNode() {
        super();
        try {
            isAdded_field = NodeBase.class.getDeclaredField("isAdded");
            isAdded_field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void connectJob() {
        connectJob_mirror();
        for (SixNodeElement element : sideElementList) {
            if (element != null) {
                element.connectJob();
            }
        }

        //INTERNAL
        {
            Direction side = Direction.YN;
            SixNodeElement element = sideElementList[side.getInt()];
            if (element != null) {
                for (LRDU lrdu : LRDU.values()) {
                    Direction otherSide = side.applyLRDU(lrdu);
                    SixNodeElement otherElement = sideElementList[otherSide.getInt()];
                    if (otherElement != null) {
                        LRDU otherLRDU = otherSide.getLRDUGoingTo(side);
                        tryConnectTwoInternalElement(side, element, lrdu, otherSide, otherElement, otherLRDU);
                    }
                }
            }
        }
        {
            Direction side = Direction.YP;
            SixNodeElement element = sideElementList[side.getInt()];
            if (element != null) {
                for (LRDU lrdu : LRDU.values()) {
                    Direction otherSide = side.applyLRDU(lrdu);
                    SixNodeElement otherElement = sideElementList[otherSide.getInt()];
                    if (otherElement != null) {
                        LRDU otherLRDU = otherSide.getLRDUGoingTo(side);
                        tryConnectTwoInternalElement(side, element, lrdu, otherSide, otherElement, otherLRDU);
                    }
                }
            }
        }

        {
            Direction side = Direction.XN;
            for (int idx = 0; idx < 4; idx++) {
                Direction otherSide = side.right();
                SixNodeElement element = sideElementList[side.getInt()];
                SixNodeElement otherElement = sideElementList[otherSide.getInt()];
                if (element != null && otherElement != null) {
                    tryConnectTwoInternalElement(side, element, LRDU.Right, otherSide, otherElement, LRDU.Left);
                }

                side = otherSide;
            }
        }
    }

    @Override
    public void disconnectJob() {
        update_internal_pneumatic_connections();
        updatePneumaticConnections();
        super.disconnectJob();
    }

    public void update_internal_pneumatic_connections() {
        for (Integer intid : internal_pneumatic_connections_map.keySet()) {
            Double speed=internal_pneumatic_connections_map.get(intid).get_speed();
            internal_pneumatic_speeds_map.put(intid,speed);
        }
    }

    public void connectJob_mirror() {
        try {

            // EXTERNAL OTHERS SIXNODE
            {
                int[] emptyBlockCoord = new int[3];
                int[] otherBlockCoord = new int[3];
                for (Direction direction : Direction.values()) {
                    if (isBlockWrappable(direction)) {
                        emptyBlockCoord[0] = coordonate.x;
                        emptyBlockCoord[1] = coordonate.y;
                        emptyBlockCoord[2] = coordonate.z;
                        direction.applyTo(emptyBlockCoord, 1);
                        for (LRDU lrdu : LRDU.values()) {
                            Direction elementSide = direction.applyLRDU(lrdu);
                            otherBlockCoord[0] = emptyBlockCoord[0];
                            otherBlockCoord[1] = emptyBlockCoord[1];
                            otherBlockCoord[2] = emptyBlockCoord[2];
                            elementSide.applyTo(otherBlockCoord, 1);
                            NodeBase otherNode = NodeManager.instance.getNodeFromCoordonate(new Coordonate(otherBlockCoord[0], otherBlockCoord[1], otherBlockCoord[2], coordonate.dimention));
                            if (otherNode == null) continue;
                            Direction otherDirection = elementSide.getInverse();
                            LRDU otherLRDU = otherDirection.getLRDUGoingTo(direction).inverse();
                            if (this instanceof SixNode || otherNode instanceof SixNode) {
                                tryConnectTwoIgorNode(this, direction, lrdu, otherNode, otherDirection, otherLRDU);
                            }
                        }
                    }
                }
            }

            for (Direction dir : Direction.values()) {
                NodeBase otherNode = getNeighbor(dir);
                if (otherNode != null && ((boolean)isAdded_field.get(otherNode))) {
                    for (LRDU lrdu : LRDU.values()) {
                        tryConnectTwoIgorNode(this, dir, lrdu, otherNode, dir.getInverse(), lrdu.inverseIfLR());
                    }
                }

            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public void tryConnectTwoInternalElement(Direction side, SixNodeElement element, LRDU lrdu, Direction otherSide, SixNodeElement otherElement, LRDU otherLRDU) {
        Utils.println("SixNode.tCTIE:");
        int mskThis = element.getConnectionMask(lrdu);
        int mskOther = otherElement.getConnectionMask(otherLRDU);
        if (compareConnectionMask(mskThis, mskOther)) {
            Utils.println("\tConnection OK.");
            lrduElementMask.set(side, lrdu, true);
            lrduElementMask.set(otherSide, otherLRDU, true);
            IgorNodeConnection nodeConnection = new IgorNodeConnection(this, side, lrdu, this, otherSide, otherLRDU);
            nodeConnectionList.add(nodeConnection);
            element.newConnectionAt(nodeConnection, false);
            otherElement.newConnectionAt(nodeConnection, true);
            ElectricalLoad eLoad;
            if ((eLoad = element.getElectricalLoad(lrdu, mskOther)) != null) {
                ElectricalLoad otherELoad = otherElement.getElectricalLoad(otherLRDU, mskThis);
                if (otherELoad != null) {
                    ElectricalConnection eCon;
                    eCon = new ElectricalConnection(eLoad, otherELoad);

                    Eln.simulator.addElectricalComponent(eCon);

                    internalElectricalConnectionList.add(eCon);
                    nodeConnection.addConnection(eCon);
                }
            }
            ThermalLoad tLoad;
            if ((tLoad = this.getThermalLoad(side, lrdu, mskOther)) != null) {

                ThermalLoad otherTLoad = element.getThermalLoad(otherLRDU, mskThis);
                if (otherTLoad != null) {
                    ThermalConnection tCon;
                    tCon = new ThermalConnection(tLoad, otherTLoad);

                    Eln.simulator.addThermalConnection(tCon);

                    internalThermalConnectionList.add(tCon);
                    nodeConnection.addConnection(tCon);
                }

            }
            if (element instanceof IgorElementInterface && otherElement instanceof IgorElementInterface) {
                PneumaticLoad pload;

                if ((pload = ((IgorElementInterface)element).getPneumaticLoad(side, lrdu)) != null) {

                    PneumaticLoad otherPLoad = ((IgorElementInterface)otherElement).getPneumaticLoad(otherSide, otherLRDU);

                    if (otherPLoad != null) {

                        PneumaticConnection pCon = new PneumaticConnection(pload, otherPLoad);

                        pCon.load_stats();

                        Integer intid=(side.getInt()<<4)|otherSide.getInt();

                        internal_pneumatic_connections_map.put(intid,pCon);

                        Double nspeed=internal_pneumatic_speeds_map.get(intid);

                        if (nspeed!=null) {
                            pCon.set_speed(nspeed);
                        }

                        pneumatic_simulator.addPneumaticComponent(pCon);
                        internalPneumaticConnectionList.add(pCon);
                        nodeConnection.addConnection(pCon);
                    }
                }
            }
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
        update_internal_pneumatic_connections();
        {
            NBTTagCompound inbt = new NBTTagCompound();
            for (Integer intid : internal_pneumatic_speeds_map.keySet()) {
                inbt.setDouble(intid.toString(), internal_pneumatic_speeds_map.get(intid));
            }
            nbt.setTag("internal_pcons_speed", inbt);
        }
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
        internal_pneumatic_speeds_map.clear();
        if (nbt.hasKey("internal_pcons_speed")) {
            NBTTagCompound inbt = nbt.getCompoundTag("internal_pcons_speed");
            for (Object str_intid : inbt.func_150296_c()) {
                Integer intid = Integer.parseInt((String) str_intid);
                double speed = inbt.getDouble((String) str_intid);
                internal_pneumatic_speeds_map.put(intid, speed);
            }
        }
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
    public void connectInit() {
        super.connectInit();

        internalPneumaticConnectionList.clear();

        for (PneumaticConnection connection : internalPneumaticConnectionList) pneumatic_simulator.removePneumaticComponent(connection);
    }

    @Override
    public PneumaticLoad getPneumaticLoad(Direction direction,LRDU lrdu,int intMask) {
        Direction elementSide = direction.applyLRDU(lrdu);
        SixNodeElement element = sideElementList[elementSide.getInt()];
        if (element == null || !(element instanceof IgorElementInterface)) {
            return null;
        }
        return ((IgorElementInterface)element).getPneumaticLoad(direction,elementSide.getLRDUGoingTo(direction));
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
