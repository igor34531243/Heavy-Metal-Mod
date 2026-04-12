package com.steve1.igortweakseaaddon.pneumatics.PneumaticTurbine;

import com.steve1.igortweakseaaddon.misc.IgorGuiContainerEln;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorSimpleShaft.IgorSimpleShaftRender;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementInventory;
import mods.eln.node.transparent.TransparentNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.io.DataInputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.t1PneumaticPipeDescriptor;

public class PneumaticTurbineRender extends IgorSimpleShaftRender {
    public IInventory inventory=new TransparentNodeElementInventory(1,64,this);

    public PneumaticPipeDescriptor pipe_descriptor;
    public CableRenderDescriptor cable_render;
    public boolean has_item=false;

    public PneumaticTurbineDescriptor descriptor;

    public PneumaticTurbineRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor got_descriptor) {
        super(tileEntity, got_descriptor);
        descriptor=(PneumaticTurbineDescriptor) got_descriptor;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new IgorGuiContainerEln(descriptor.make_container(player,inventory),30);
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
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
    public CableRenderDescriptor getCableRender(Direction side,LRDU lrdu) {
        if (has_item) {
            if (lrdu==LRDU.Down && (side == front || side == front.getInverse())) {
                return cable_render;
            }
        }
        return null;
    }
}
