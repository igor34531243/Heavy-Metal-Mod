package com.steve1.igortweakseaaddon.pneumatics.PneumaticTank;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.EntityMetaTag;

public class PneumaticTankDescriptor extends IgorTransparentNodeDescriptor {

    PneumaticPipeDescriptor pipe_descriptor;

    public PneumaticTankDescriptor(String name, Obj3D model, PneumaticPipeDescriptor pipe_descriptor) {
        super(name, model, PneumaticTankElement.class, PneumaticTankRender.class, EntityMetaTag.Basic);
        this.pipe_descriptor=pipe_descriptor;
    }

    @Override
    public boolean mustHaveFloor() {
        return false;
    }
}
