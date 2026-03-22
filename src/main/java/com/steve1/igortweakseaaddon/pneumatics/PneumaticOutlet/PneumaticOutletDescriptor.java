package com.steve1.igortweakseaaddon.pneumatics.PneumaticOutlet;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.EntityMetaTag;

public class PneumaticOutletDescriptor extends IgorTransparentNodeDescriptor {

    public PneumaticOutletDescriptor(String name, Obj3D model) {
        super(name, model, PneumaticOutletElement.class, PneumaticOutletRender.class, EntityMetaTag.Basic);
    }

    @Override
    public boolean mustHaveFloor() {
        return false;
    }
}
