package com.steve1.igortweakseaaddon.pneumatics.PneumaticSource;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.EntityMetaTag;

public class PneumaticSourceDescriptor extends IgorTransparentNodeDescriptor {
    public PneumaticSourceDescriptor(String name, Obj3D model) {
        super(name, model, PneumaticSourceElement.class, PneumaticSourceRender.class, EntityMetaTag.Basic);
    }

    @Override
    public boolean mustHaveFloor() {
        return false;
    }
}
