package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.WithPipeInventory;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.IgorSixNodeWithInventoryElement;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.Utils;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import net.minecraft.item.ItemStack;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.t1PneumaticPipeDescriptor;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.get_stack_in_slot;

public abstract class IgorSixNodeWithPipeInventoryElement extends IgorSixNodeWithInventoryElement {

    public IgorSixNodeWithPipeInventoryDescriptor descriptor_pipe_inv;

    public PneumaticPipeDescriptor pipe_descriptor;
    public boolean has_item=false;

    public IgorSixNodeWithPipeInventoryElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);
        pipe_descriptor=t1PneumaticPipeDescriptor;
        this.descriptor_pipe_inv=(IgorSixNodeWithPipeInventoryDescriptor) descriptor;
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            Utils.serialiseItemStack(stream,get_stack_in_slot(inventory,descriptor_pipe_inv.pipeId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void pipe_descriptor_changed();

    public void update_item() {
        ItemStack pipeStack = get_stack_in_slot(inventory, descriptor_pipe_inv.pipeId);
        PneumaticPipeDescriptor pipe_desc = (PneumaticPipeDescriptor) PneumaticPipeDescriptor.getDescriptor(pipeStack, PneumaticPipeDescriptor.class);
        if (pipe_desc == null) {
            pipe_descriptor = t1PneumaticPipeDescriptor;
            has_item=false;
        } else {
            pipe_descriptor = pipe_desc;
            has_item=true;
        }
        pipe_descriptor_changed();
        reconnect();
        needPublish();
    }

    @Override
    public void initialize() {
        super.initialize();
        update_item();
    }

    @Override
    public void inventoryChanged() {
        super.inventoryChanged();
        update_item();
    }

}
