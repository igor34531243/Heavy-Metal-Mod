package com.steve1.igortweakseaaddon.WirelessAlarm;

import mods.eln.Eln;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.RcInterpolator;
import mods.eln.misc.Utils;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElementRender;
import mods.eln.node.six.SixNodeEntity;
import mods.eln.sixnode.electricalalarm.ElectricalAlarmDescriptor;
import mods.eln.sixnode.electricalalarm.ElectricalAlarmGui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import java.io.DataInputStream;
import java.io.IOException;

public class WirelessAlarmRender extends SixNodeElementRender {

    WirelessAlarmDescriptor descriptor;

    LRDU front;

    RcInterpolator interpol = new RcInterpolator(0.4f);

    float rotAlpha = 0;
    boolean warm = false;

    boolean connection;

    String channel;
    int selectedAggregator;

    public WirelessAlarmRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (WirelessAlarmDescriptor) descriptor;
    }

    @Override
    public void draw() {
        super.draw();

        descriptor.draw(warm, rotAlpha);
    }

    @Override
    public void refresh(float deltaT) {
        interpol.setTarget(warm ? descriptor.rotSpeed : 0f);
        interpol.step(deltaT);

        rotAlpha += interpol.get() * deltaT;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            Byte b;
            b = stream.readByte();
            front = LRDU.fromInt((b >> 4) & 3);
            warm = (b & 1) != 0 ? true : false;
            channel = stream.readUTF();
            connection = stream.readBoolean();
            selectedAggregator = stream.readByte();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return Eln.instance.signalCableDescriptor.render;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {
        return new WirelessAlarmGui(this);
    }
}
