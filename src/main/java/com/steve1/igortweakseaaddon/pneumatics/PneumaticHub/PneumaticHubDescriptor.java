package com.steve1.igortweakseaaddon.pneumatics.PneumaticHub;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.EntityMetaTag;

public class PneumaticHubDescriptor extends IgorTransparentNodeDescriptor {

    public PneumaticHubDescriptor(String name, Obj3D model) {
        super(name, model, PneumaticHubElement.class, PneumaticHubRender.class, EntityMetaTag.Basic);
    }

    @Override
    public boolean mustHaveFloor() {
        return false;
    }
}
