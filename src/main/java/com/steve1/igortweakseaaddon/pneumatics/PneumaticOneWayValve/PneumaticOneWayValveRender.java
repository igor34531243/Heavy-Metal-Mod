package com.steve1.igortweakseaaddon.pneumatics.PneumaticOneWayValve;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElementRender;
import mods.eln.Eln;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeEntity;
import mods.eln.sixnode.electricalgatesource.ElectricalGateSourceGui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import java.io.DataInputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.plot_pascals_atmospheres;

public class PneumaticOneWayValveRender extends IgorSixNodeElementRender {
    public PneumaticOneWayValveDescriptor descriptor;

    public boolean hasChanges=false;
    public long set_pressure;
    public long max_pressure;
    public boolean mode_is_p_diff=true;
    public boolean side_is_yellow=true;
    public boolean open_if_above=true;
    public boolean is_open=false;

    public PneumaticOneWayValveRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor= (PneumaticOneWayValveDescriptor) descriptor;
        max_pressure=(long)this.descriptor.pipe_descriptor.max_pressure;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            set_pressure=stream.readLong();
            byte flags=stream.readByte();
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

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new PneumaticOneWayValveGui(player, this);
    }
}
