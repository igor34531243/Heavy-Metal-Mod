package com.steve1.igortweakseaaddon.pneumatics.PneumaticPipeOutlet;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.misc.VoltageLevelColor;

public class PneumaticPipeOutletDescriptor extends IgorSixNodeDescriptor {

    Obj3D model;
    Obj3D.Obj3DPart main;

    public PneumaticPipeOutletDescriptor(String name, Obj3D model) {
        super(name, PneumaticPipeOutletElement.class, PneumaticPipeOutledRender.class);
        this.voltageLevelColor=VoltageLevelColor.Neutral;
        this.model=model;
        main=model.getPart("main");
    }

    public void draw() {
        if (main!=null) {
            main.draw();
        }
    }

}
