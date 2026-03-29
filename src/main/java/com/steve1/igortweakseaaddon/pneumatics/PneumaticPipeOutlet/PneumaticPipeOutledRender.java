package com.steve1.igortweakseaaddon.pneumatics.PneumaticPipeOutlet;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElementRender;
import mods.eln.misc.Direction;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeEntity;

public class PneumaticPipeOutledRender extends IgorSixNodeElementRender {

    PneumaticPipeOutletDescriptor descriptor;

    public PneumaticPipeOutledRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor passed_descriptor) {
        super(tileEntity, side, passed_descriptor);
        descriptor= (PneumaticPipeOutletDescriptor) passed_descriptor;
    }

    @Override
    public void draw() {
        super.draw();
        if (side.isY()) {
            front.glRotateOnX();
        }
        descriptor.draw();
    }
}
