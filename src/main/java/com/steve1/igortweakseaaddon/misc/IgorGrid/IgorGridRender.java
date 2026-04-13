package com.steve1.igortweakseaaddon.misc.IgorGrid;

import mods.eln.gridnode.GridRender;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IgorGridRender extends GridRender {
    IgorGridDescriptor stored_descriptor;
    public IgorGridRender(@NotNull TransparentNodeEntity tileEntity, @NotNull TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        stored_descriptor= (IgorGridDescriptor) descriptor;
    }

    @Override
    public void draw() {
        super.draw();

        GL11.glPushMatrix();
        front.glRotateXnRef();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        stored_descriptor.draw(this);
        GL11.glPopMatrix();
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
}
