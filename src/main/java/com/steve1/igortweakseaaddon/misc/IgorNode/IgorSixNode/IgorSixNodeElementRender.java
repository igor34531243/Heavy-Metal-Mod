package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode;

import mods.eln.misc.Direction;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElementRender;
import mods.eln.node.six.SixNodeEntity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IgorSixNodeElementRender extends SixNodeElementRender {
    public IgorSixNodeElementRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
    }

    public void clientSetLong(byte id, long value) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bos);

            preparePacketForServer(stream);

            stream.writeByte(id);
            stream.writeLong(value);

            sendPacketToServer(bos);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}
