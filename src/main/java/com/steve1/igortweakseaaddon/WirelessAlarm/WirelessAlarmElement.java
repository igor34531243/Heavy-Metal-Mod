package com.steve1.igortweakseaaddon.WirelessAlarm;

import mods.eln.Eln;
import mods.eln.i18n.I18N;
import mods.eln.item.IConfigurable;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBase;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElement;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.nbt.NbtElectricalGateInput;
import mods.eln.sixnode.electricalalarm.ElectricalAlarmDescriptor;
import mods.eln.sixnode.electricalalarm.ElectricalAlarmSlowProcess;
import mods.eln.sixnode.wirelesssignal.IWirelessSignalSpot;
import mods.eln.sixnode.wirelesssignal.aggregator.BiggerAggregator;
import mods.eln.sixnode.wirelesssignal.aggregator.IWirelessSignalAggregator;
import mods.eln.sixnode.wirelesssignal.aggregator.SmallerAggregator;
import mods.eln.sixnode.wirelesssignal.aggregator.ToogleAggregator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WirelessAlarmElement extends SixNodeElement  implements IConfigurable {

    WirelessAlarmDescriptor descriptor;

    public WirelessAlarmProcess slowProcess = new WirelessAlarmProcess(this);

    boolean warm = false;

    public String channel = "Default channel";

    ToogleAggregator toogleAggregator;

    boolean connection = false;

    public static final byte setChannelId = 1;
    public static final byte setSelectedAggregator = 2;
    IWirelessSignalAggregator[] aggregators;

    int selectedAggregator = 0;

    double signal_value=0;

    public WirelessAlarmElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);

        this.descriptor = (WirelessAlarmDescriptor) descriptor;

        slowProcessList.add(slowProcess);
        IWirelessSignalSpot.spots.add(slowProcess);

        aggregators = new IWirelessSignalAggregator[3];
        aggregators[0] = new BiggerAggregator();
        aggregators[1] = new SmallerAggregator();
        aggregators[2] = toogleAggregator = new ToogleAggregator();
    }

    public static boolean canBePlacedOnSide(Direction side, int type) {
        return true;
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);

        try {
            switch (stream.readByte()) {
                case setChannelId:
                    channel = stream.readUTF();
                    slowProcess.sleepTimer_reciever = 0;
                    needPublish();
                    break;

                case setSelectedAggregator:
                    selectedAggregator = stream.readByte();
                    needPublish();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        byte value = nbt.getByte("front");
        front = LRDU.fromInt((value >> 0) & 0x3);
        channel = nbt.getString("channel");
        connection = nbt.getBoolean("connection");
        selectedAggregator = nbt.getInteger("selectedAggregator");
        toogleAggregator.readFromNBT(nbt, "toogleAggregator");
        signal_value = nbt.getDouble("signal_value");
        slowProcess.signal_value=signal_value;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("front", (byte) ((front.toInt() << 0)));
        nbt.setString("channel", channel);
        nbt.setBoolean("connection", connection);
        nbt.setInteger("selectedAggregator", selectedAggregator);
        toogleAggregator.writeToNBT(nbt, "toogleAggregator");
        nbt.setDouble("signal_value",signal_value);
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        return 0;
    }

    @Override
    public String multiMeterString() {
        if (warm) {
            return "Alarm is ON!";
        } else {
            return "Alarm is off.";
        }
    }

    @Override
    public String thermoMeterString() {
        return "";
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeByte((front.toInt() << 4) + (warm ? 1 : 0));
            stream.writeUTF(channel);
            stream.writeBoolean(connection);
            stream.writeByte(selectedAggregator);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IWirelessSignalAggregator getAggregator() {
        if (selectedAggregator >= 0 && selectedAggregator < aggregators.length)
            return aggregators[selectedAggregator];
        return null;
    }

    public void setWarm(boolean value) {
        if (warm != value) {
            warm = value;
            sixNode.recalculateLightValue();
            needPublish();
        }
    }

    @Override
    public void initialize() {
        slowProcess.process(0.05);
    }

    public int getLightValue() {
        return warm ? descriptor.light : 0;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void globalBoot() {
        super.globalBoot();
        slowProcess.process(0.05);
    }

    @Override
    public void destroy(EntityPlayerMP entityPlayer) {
        unregister();
        super.destroy(entityPlayer);
    }

    @Override
    public void unload() {
        super.unload();
        unregister();
    }

    void unregister() {
        IWirelessSignalSpot.spots.remove(slowProcess);
    }


    void setConnection(boolean connection) {
        if (connection != this.connection) {
            this.connection = connection;
            needPublish();
        }
    }

    @Override
    public void readConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        if(compound.hasKey("wirelessChannels")) {
            String newChannel = compound.getTagList("wirelessChannels", 8).getStringTagAt(0);
            if(newChannel != null && newChannel != "") {
                channel = newChannel;
                needPublish();
            }
        }
    }

    @Override
    public void writeConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagString(channel));
        compound.setTag("wirelessChannels", list);
    }
}