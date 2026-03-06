package com.steve1.igortweakseaaddon.WirelessAlarm;

import mods.eln.Eln;
import mods.eln.misc.Coordonate;
import mods.eln.misc.INBTTReady;
import mods.eln.misc.Utils;
import mods.eln.sim.IProcess;
import mods.eln.sixnode.electricalalarm.ElectricalAlarmElement;
import mods.eln.sixnode.wirelesssignal.IWirelessSignalSpot;
import mods.eln.sixnode.wirelesssignal.IWirelessSignalTx;
import mods.eln.sixnode.wirelesssignal.WirelessUtils;
import mods.eln.sixnode.wirelesssignal.repeater.WirelessSignalRepeaterElement;
import mods.eln.sixnode.wirelesssignal.rx.WirelessSignalRxElement;
import mods.eln.sound.SoundCommand;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class WirelessAlarmProcess implements IProcess, INBTTReady, IWirelessSignalSpot {

    WirelessAlarmElement element;

    double timeCounter = 0, soundTimeTimeout = Math.random() * 2;
    static final double refreshPeriode = 0.25;
    int soundUuid = Utils.getUuid();
    boolean oldWarm = false;

    private WirelessAlarmElement rx;

    double sleepTimer_reciever = 0;

    public double signal_value=0;

    HashMap<String, HashSet<IWirelessSignalTx>> txSet = new HashMap<String, HashSet<IWirelessSignalTx>>();
    HashMap<IWirelessSignalTx, Double> txStrength = new HashMap<IWirelessSignalTx, Double>();

    double sleepTimer_repeater = 0;
    IWirelessSignalSpot spot;

    boolean boot = true;

    public WirelessAlarmProcess(WirelessAlarmElement element) {
        this.element = element;
        this.rx = element;
    }

    @Override
    public void process(double time) {
        repeater_process(time);
        alarm_process(time);
        wireless_process(time);
    }

    public void alarm_process(double time) {
        timeCounter += time;
        if (timeCounter > refreshPeriode) {
            timeCounter -= refreshPeriode;

            boolean warm = signal_value > Eln.instance.SVU / 2;
            element.setWarm(warm);
            if (warm) {
                if (soundTimeTimeout == 0) {
                    float speed = 1f;
                    Coordonate coord = element.sixNode.coordonate;
                    element.play(new SoundCommand(element.descriptor.soundName).mulVolume(1F, 1.0F).longRange().addUuid(soundUuid));
                    soundTimeTimeout = element.descriptor.soundTime;
                }
            }
            if ((oldWarm && !warm)) {
                stopSound();
            }

            oldWarm = warm;
        }
        soundTimeTimeout -= time;
        if (soundTimeTimeout < 0) soundTimeTimeout = 0;
    }

    void stopSound() {
        element.stop(soundUuid);
        soundTimeTimeout = 0;
    }

    public void wireless_process(double time) {
        double output;
        sleepTimer_reciever -= time;

        if (sleepTimer_reciever < 0) {
            sleepTimer_reciever += Utils.rand(1.2, 2);

            IWirelessSignalSpot spot = WirelessUtils.buildSpot(rx.getCoordonate(), rx.channel, 0);
            WirelessUtils.getTx(spot, txSet, txStrength);
        }

        HashSet<IWirelessSignalTx> txs = txSet.get(rx.channel);
        if (txs == null) {
            output = 0;
            rx.setConnection(false);
        } else {
            output = rx.getAggregator().aggregate(txs);
            rx.setConnection(true);
        }

        set_signal_value(output * Eln.SVU);
        //rx.outputGateProcess.setOutputNormalized(output);
    }

    public void set_signal_value(double nvalue) {
        signal_value=nvalue;
        element.signal_value=nvalue;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
    }

    public void repeater_process(double time) {
        sleepTimer_repeater -= time;
        if (sleepTimer_repeater < 0) {
            sleepTimer_repeater += Utils.rand(1.2, 2);

            spot = WirelessUtils.buildSpot(rx.getCoordonate(), null, rx.descriptor.range);

            if (boot) {
                boot = false;
                //IWirelessSignalSpot.spots.add(this);
            }
        }
    }

    @Override
    public HashMap<String, ArrayList<IWirelessSignalTx>> getTx() {
        return spot.getTx();
    }

    @Override
    public ArrayList<IWirelessSignalSpot> getSpot() {
        return spot.getSpot();
    }

    @Override
    public Coordonate getCoordonate() {
        return rx.getCoordonate();
    }

    @Override
    public int getRange() {
        return rx.descriptor.range;
    }
}
