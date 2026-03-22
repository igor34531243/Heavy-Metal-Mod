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

import java.lang.reflect.Field;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;

public class IgorTransparentNode extends TransparentNode implements IgorNodeInterface {

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
            //logger.info("mirror at least running!!!");

            if (nodeA instanceof IgorNodeInterface && nodeB instanceof IgorNodeInterface) {
                //logger.info("connection for the neuamtics starting!!!!");
                PneumaticLoad pload;
                if ((pload = ((IgorNodeInterface)nodeA).getPneumaticLoad(directionA, lrduA, mskB)) != null) {

                    PneumaticLoad otherPLoad = ((IgorNodeInterface)nodeB).getPneumaticLoad(directionB, lrduB, mskA);

                    if (otherPLoad != null) {

                        pCon = new PneumaticConnection(pload, otherPLoad);

                        pneumatic_simulator.addPneumaticComponent(pCon);
                        nodeConnection.addConnection(pCon);
                    }
                }
            }
        }
    }

    @Override
    public PneumaticLoad getPneumaticLoad(Direction direction,LRDU lrdu,int intMask) {
        return ((IgorElementInterface)element).getPneumaticLoad(direction,lrdu);
    }
}
