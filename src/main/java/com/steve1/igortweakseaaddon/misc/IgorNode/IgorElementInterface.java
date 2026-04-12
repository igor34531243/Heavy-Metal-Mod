package com.steve1.igortweakseaaddon.misc.IgorNode;

import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;

public interface IgorElementInterface {
    public PneumaticLoad getPneumaticLoad(Direction direction, LRDU lrdu);
}
