package com.steve1.igortweakseaaddon.pneumatics.PneumaticSource;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeElementRender;
import mods.eln.misc.Direction;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import java.io.DataInputStream;
import java.io.IOException;

public class PneumaticSourceRender extends IgorTransparentNodeElementRender {

    public static final int setPressureId = 1;

    public double pressure;

    public PneumaticSourceRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            pressure=stream.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new PneumaticSourceGui(this);
    }

}
