package com.steve1.igortweakseaaddon.pneumatics.PneumaticHub;

import com.steve1.igortweakseaaddon.misc.IgorTransparentNode.IgorTransparentNodeDescriptor;
import mods.eln.misc.Obj3D;

public class PneumaticHubDescriptor extends IgorTransparentNodeDescriptor {

    public PneumaticHubDescriptor(String name, Obj3D model) {
        super(name, model, PneumaticHubElement.class, PneumaticHubRender.class);
    }
}
