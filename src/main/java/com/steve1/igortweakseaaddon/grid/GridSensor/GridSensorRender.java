package com.steve1.igortweakseaaddon.grid.GridSensor;

import com.steve1.igortweakseaaddon.grid.IgorGrid.IgorGridRender;
import mods.eln.misc.Direction;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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

    public void clientSetByte(byte id, byte value) {
        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bos);

            preparePacketForServer(stream);

            stream.writeByte(id);
            stream.writeByte(value);

            sendPacketToServer(bos);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void clientSetFloat(int id, float value1, float value2) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bos);

            preparePacketForServer(stream);

            stream.writeByte(id);
            stream.writeFloat(value1);
            stream.writeFloat(value2);

            sendPacketToServer(bos);
        } catch (IOException e) {

            e.printStackTrace();
        }

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
