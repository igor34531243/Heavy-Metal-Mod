package com.steve1.igortweakseaaddon.pneumatics.PneumaticValve;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import mods.eln.misc.Obj3D;

public class PneumaticValveDescriptor extends IgorSixNodeDescriptor {

    PneumaticPipeDescriptor pipe_descriptor;
    Obj3D model;
    Obj3D.Obj3DPart main;

    public PneumaticValveDescriptor(String name, PneumaticPipeDescriptor pipe_descriptor, Obj3D model) {
        super(name, PneumaticValveElement.class, PneumaticValveRender.class);
        this.pipe_descriptor=pipe_descriptor;
        this.model=model;
        main=model.getPart("main");
    }

    public void draw() {
        if (main!=null) {
            main.draw();
        }
    }
}
