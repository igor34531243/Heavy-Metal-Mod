package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode;

import com.steve1.igortweakseaaddon.misc.IgorLoopedSound;
import com.steve1.igortweakseaaddon.misc.IgorLoopedSoundManager;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticOneWayValve.PneumaticOneWayValveGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.eln.client.ClientProxy;
import mods.eln.misc.Direction;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElementRender;
import mods.eln.node.six.SixNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IgorSixNodeElementRender extends SixNodeElementRender {

    public IgorSixNodeDescriptor igorSixNodeDescriptor;

    public IgorSixNodeElementRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        igorSixNodeDescriptor=(IgorSixNodeDescriptor)descriptor;
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

    public void clientSetBoolean(byte id, boolean value) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bos);

            preparePacketForServer(stream);

            stream.writeByte(id);
            stream.writeBoolean(value);

            sendPacketToServer(bos);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void clientSetDouble(byte id, double value1, double value2) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bos);

            preparePacketForServer(stream);

            stream.writeByte(id);
            stream.writeDouble(value1);
            stream.writeDouble(value2);

            sendPacketToServer(bos);
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return igorSixNodeDescriptor.make_gui(side,player,this);
    }

    public IgorLoopedSoundManager igorLoopedSoundManager = new IgorLoopedSoundManager();

    @SideOnly(Side.CLIENT)
    public void addIgorLoopedSound(final IgorLoopedSound loopedSound) {
        igorLoopedSoundManager.add(loopedSound);
    }

    @Override
    public void destructor() {
        super.destructor();

        igorLoopedSoundManager.dispose();
    }

    @Override
    public void refresh(float deltaT) {
        super.refresh(deltaT);
        igorLoopedSoundManager.process(deltaT);
    }
}
