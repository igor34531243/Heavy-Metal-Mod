package com.steve1.igortweakseaaddon.misc.IgorNode;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNode;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;

import java.util.HashMap;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public interface IgorNodeInterface {

    public PneumaticLoad getPneumaticLoad(Direction direction,LRDU lrdu,int intMask);

    public void updatePneumaticConnections();

    public static void linkPneumaticConnection(IgorNodeInterface nodeA, Direction directionA,LRDU lrduA,IgorNodeInterface nodeB, Direction directionB,LRDU lrduB, PneumaticConnection connection) {

        Boolean isAA = nodeA.getPneumaticConnectionIsA(directionA,lrduA);
        Boolean isAB = nodeB.getPneumaticConnectionIsA(directionB,lrduB);

        Double speedA = nodeA.getPneumaticConnectionSpeed(directionA,lrduA); // a function to get speed for node a
        Double speedB = nodeB.getPneumaticConnectionSpeed(directionB,lrduB);

        if (speedA==null || speedB==null || isAA==null || isAB==null){
            nodeA.addPneumaticConnectionSpeed(directionA,lrduA, 0d);
            nodeB.addPneumaticConnectionSpeed(directionB,lrduB, 0d);
        } else if (Math.abs(speedA-speedB)>0.0000001 || isAA==isAB) {
            nodeA.addPneumaticConnectionSpeed(directionA,lrduA, 0d);
            nodeB.addPneumaticConnectionSpeed(directionB,lrduB, 0d);
        } else {
            Double average_speed=(speedA+speedB)/2;
            if (isAB) {
                average_speed=-average_speed;
            }
            connection.set_speed(average_speed);
            nodeA.addPneumaticConnectionSpeed(directionA,lrduA,average_speed);
            nodeB.addPneumaticConnectionSpeed(directionB,lrduB,average_speed);
        }

        nodeA.addPneumaticConnection(directionA,lrduA, connection);
        nodeB.addPneumaticConnection(directionB,lrduB, connection);

        nodeA.addPneumaticConnectionIsA(directionA,lrduA,true);
        nodeB.addPneumaticConnectionIsA(directionB,lrduB,false);

        //pneumatic_connections_map.put(direction,connection);
    }

    public void clearPneumaticConnections();

    public void addPneumaticConnection(Direction direction,LRDU lrdu,PneumaticConnection connection);

    public void addPneumaticConnectionSpeed(Direction direction,LRDU lrdu,Double speed);

    public void addPneumaticConnectionIsA(Direction direction,LRDU lrdu,Boolean isA);

    public PneumaticConnection getPneumaticConnection(Direction direction,LRDU lrdu);

    public Double getPneumaticConnectionSpeed(Direction direction,LRDU lrdu);

    public Boolean getPneumaticConnectionIsA(Direction direction,LRDU lrdu);

    public static int ppos_to_int(Direction direction,LRDU lrdu) {
        return (direction.getInt()<<4) | lrdu.toInt();
    }
}
