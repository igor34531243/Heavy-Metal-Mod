package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.WithPipeInventory;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.IgorSixNodeWithInventoryElementRender;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeEntity;
import net.minecraft.item.ItemStack;

import java.io.DataInputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.t1PneumaticPipeDescriptor;

public class IgorSixNodeWithPipeInventoryElementRender extends IgorSixNodeWithInventoryElementRender {

    public PneumaticPipeDescriptor pipe_descriptor;
    public CableRenderDescriptor cable_render;
    public boolean has_item=false;

    public IgorSixNodeWithPipeInventoryElementRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        pipe_descriptor=t1PneumaticPipeDescriptor;
        cable_render = pipe_descriptor.cable_render;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            ItemStack pipeStack = Utils.unserialiseItemStack(stream);
            PneumaticPipeDescriptor pipe_desc = (PneumaticPipeDescriptor)PneumaticPipeDescriptor.getDescriptor(pipeStack, PneumaticPipeDescriptor.class);
            if (pipe_desc == null) {
                pipe_descriptor = t1PneumaticPipeDescriptor;
                has_item=false;
            } else {
                pipe_descriptor = pipe_desc;
                has_item=true;
            }
            cable_render = pipe_descriptor.cable_render;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return cable_render;
    }
}
