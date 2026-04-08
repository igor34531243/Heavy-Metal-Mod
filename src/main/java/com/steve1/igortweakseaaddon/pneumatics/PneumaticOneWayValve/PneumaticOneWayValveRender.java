package com.steve1.igortweakseaaddon.pneumatics.PneumaticOneWayValve;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElementRender;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.IgorSixNodeWithInventoryElementRender;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.WithPipeInventory.IgorSixNodeWithPipeInventoryElementRender;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import mods.eln.Eln;
import mods.eln.cable.CableRender;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElementInventory;
import mods.eln.node.six.SixNodeEntity;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import mods.eln.sixnode.electricalgatesource.ElectricalGateSourceGui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.io.DataInputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.t1PneumaticPipeDescriptor;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.plot_pascals_atmospheres;

public class PneumaticOneWayValveRender extends IgorSixNodeWithPipeInventoryElementRender {
    public PneumaticOneWayValveDescriptor descriptor;

    public boolean hasChanges=false;
    public long set_pressure;
    public double to_open_area;
    public double to_close_area;
    public long max_pressure;
    public double max_area;
    public boolean mode_is_p_diff=true;
    public boolean side_is_yellow=true;
    public boolean open_if_above=true;
    public boolean is_open=false;

    public PneumaticOneWayValveRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor= (PneumaticOneWayValveDescriptor) descriptor;
        max_pressure=(long)pipe_descriptor.max_pressure;
        max_area=pipe_descriptor.area;
        cable_render=pipe_descriptor.cable_render;
        to_open_area=pipe_descriptor.area;
        to_close_area=0;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            set_pressure=stream.readLong();
            to_open_area=stream.readDouble();
            to_close_area=stream.readDouble();
            byte flags=stream.readByte();
            max_pressure=(long)pipe_descriptor.max_pressure;
            max_area=pipe_descriptor.area;
            mode_is_p_diff=((flags&1)!=0);
            side_is_yellow=(((flags>>1)&1)!=0);
            open_if_above=(((flags>>2)&1)!=0);
            is_open=(((flags>>3)&1)!=0);
            hasChanges=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
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
