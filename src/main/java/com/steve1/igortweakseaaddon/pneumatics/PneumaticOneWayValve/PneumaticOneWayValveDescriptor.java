package com.steve1.igortweakseaaddon.pneumatics.PneumaticOneWayValve;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.misc.VoltageLevelColor;

public class PneumaticOneWayValveDescriptor extends IgorSixNodeDescriptor {

    public Obj3D model;
    public Obj3D.Obj3DPart main;
    public PneumaticPipeDescriptor pipe_descriptor;

    public PneumaticOneWayValveDescriptor(String name, PneumaticPipeDescriptor pipe_descriptor, Obj3D model) {
        super(name, PneumaticOneWayValveElement.class, PneumaticOneWayValveRender.class);
        this.voltageLevelColor= VoltageLevelColor.Neutral;
        this.model=model;
        this.main=model.getPart("main");
        this.pipe_descriptor=pipe_descriptor;
    }

    public void draw() {
        if (main!=null) {
            main.draw();
        }
    }
}
