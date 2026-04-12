package com.steve1.igortweakseaaddon.grid.GridSensor;

import com.steve1.igortweakseaaddon.misc.IgorGrid.IgorGridRender;
import mods.eln.misc.Direction;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import java.io.DataInputStream;
import java.io.IOException;

public class GridSensorRender extends IgorGridRender {

    TransparentNodeDescriptor descriptor;

    int typeOfSensor = 0;
    float lowValue = 0, highValue = 50;
    byte dirType;

    public GridSensorRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        this.descriptor = descriptor;
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            Byte b;
            b = stream.readByte();
            typeOfSensor = b & 0x3;
            lowValue = stream.readFloat();
            highValue = stream.readFloat();
            dirType = stream.readByte();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new GridSensorGui(player, this);
    }
}
