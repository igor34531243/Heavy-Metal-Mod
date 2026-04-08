package com.steve1.igortweakseaaddon.pneumatics.PneumaticValve;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.WithPipeInventory.IgorSixNodeWithPipeInventoryElementRender;
import mods.eln.Eln;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.t1PneumaticPipeRender;

public class PneumaticValveRender extends IgorSixNodeWithPipeInventoryElementRender {

    public PneumaticValveDescriptor descriptor;

    public boolean selected_position=false;
    public boolean has_changes=true;

    public PneumaticValveRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor= (PneumaticValveDescriptor) descriptor;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            selected_position=stream.readBoolean();
            has_changes=true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        if (front.inverse() == lrdu) {
            return Eln.instance.stdCableRenderSignal;
        }
        if (front==lrdu) {
            return t1PneumaticPipeRender;
        }
        if (front.left()==lrdu || front.right()==lrdu) {
            return cable_render;
        }
        return null;
    }

    @Override
    public void draw() {
        super.draw();

        front.glRotateOnX();

        descriptor.draw();
    }
}
