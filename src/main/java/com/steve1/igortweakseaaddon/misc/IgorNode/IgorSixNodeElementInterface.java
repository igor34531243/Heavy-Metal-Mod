package com.steve1.igortweakseaaddon.misc.IgorNode;

import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;

public interface IgorSixNodeElementInterface{
    public PneumaticLoad getPneumaticLoad(LRDU lrdu,int mask);
}
