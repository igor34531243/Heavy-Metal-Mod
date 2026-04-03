package com.steve1.igortweakseaaddon.pneumatics.PneumaticOneWayValve;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElement;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PressureWatchdog;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.process.destruct.WorldExplosion;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.pneumaticMask;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;

public class PneumaticOneWayValveElement extends IgorSixNodeElement {

    public PneumaticOneWayValveDescriptor descriptor;
    public PneumaticPipeDescriptor pipe_descriptor;

    public NBTPneumaticLoad loadA=new NBTPneumaticLoad("loadA");
    public NBTPneumaticLoad loadB=new NBTPneumaticLoad("loadB");
    public NBTPneumaticConnection connection=new NBTPneumaticConnection("connection");

    public PressureWatchdog pressureWatchdogA=new PressureWatchdog();
    public PressureWatchdog pressureWatchdogB=new PressureWatchdog();

    public PneumaticOneWayValveProcess process;

    public double max_area;
    public long set_pressure=0;
    public boolean mode_is_p_diff=true;
    public boolean side_is_yellow=true;
    public boolean open_if_above=true;
    public boolean is_open=true;

    public final static byte setPressureId=1;
    public final static byte setFlagId=2;

    public PneumaticOneWayValveElement(SixNode sixNode, Direction side, SixNodeDescriptor got_descriptor) {
        super(sixNode, side, got_descriptor);
        descriptor=(PneumaticOneWayValveDescriptor)got_descriptor;
        pipe_descriptor=descriptor.pipe_descriptor;

        max_area=pipe_descriptor.area;

        pipe_descriptor.apply_to_reset(loadA);
        pipe_descriptor.apply_to_reset(loadB);
        pipe_descriptor.apply_to(connection);
        pipe_descriptor.apply_to(pressureWatchdogA);
        pipe_descriptor.apply_to(pressureWatchdogB);

        connection.connect(loadA,loadB);
        connection.can_fall_asleep=false;

        process=new PneumaticOneWayValveProcess(this);
        pneumaticProcessList.add(process);

        pressureWatchdogA
                .set(loadA)
                .set(new WorldExplosion(this).cableExplosion());
        slowProcessList.add(pressureWatchdogA);

        pressureWatchdogB
                .set(loadB)
                .set(new WorldExplosion(this).cableExplosion());
        slowProcessList.add(pressureWatchdogB);

        pneumaticComponentList.add(connection);
        pneumaticLoadList.add(loadA);
        pneumaticLoadList.add(loadB);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        set_pressure=nbt.getLong("set_pressure");
        mode_is_p_diff=nbt.getBoolean("mode_is_p_diff");
        side_is_yellow=nbt.getBoolean("side_is_yellow");
        open_if_above=nbt.getBoolean("open_if_above");
        process.settings_changed();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setLong("set_pressure",set_pressure);
        nbt.setBoolean("mode_is_p_diff",mode_is_p_diff);
        nbt.setBoolean("side_is_yellow",side_is_yellow);
        nbt.setBoolean("open_if_above",open_if_above);
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            byte flags=0;
            flags+=(byte)(mode_is_p_diff ? 1 : 0);
            flags+=(byte)((side_is_yellow ? 1 : 0)<<1);
            flags+=(byte)((open_if_above ? 1 : 0)<<2);
            flags+=(byte)((is_open ? 1 : 0)<<3);
            stream.writeLong(set_pressure);
            stream.writeByte(flags);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            switch (stream.readByte()) {
                case setPressureId:
                    set_pressure=stream.readLong();
                    process.settings_changed();
                    needPublish();
                    break;
                case setFlagId:
                    byte flags=stream.readByte();
                    byte flag_type= (byte) (flags>>4);
                    byte flag_value= (byte) (flags & 0xf);
                    switch (flag_type) {
                        case 1:
                            mode_is_p_diff=(flag_value!=0);
                            break;
                        case 2:
                            side_is_yellow=(flag_value!=0);
                            break;
                        case 3:
                            open_if_above=(flag_value!=0);
                            break;
                    }
                    process.settings_changed();
                    needPublish();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PneumaticLoad getPneumaticLoad(LRDU lrdu, int mask) {
        if (front.left()==lrdu) {
            return loadA;
        }
        if (front.right()==lrdu) {
            return loadB;
        }
        return null;
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
        if (front.left()==lrdu || front.right()==lrdu) {
            return pneumaticMask;
        }
        return 0;
    }

    @Override
    public String multiMeterString() {
        return (is_open ? "Open" :"Closed")+", §ePA: "+plot_pascals_atmospheres(loadA.get_pressure())+"§f, §aPB: "+plot_pascals_atmospheres(loadB.get_pressure())+", Flow: "+plot_speed(Math.abs(connection.get_speed()));
    }

    @Override
    public String thermoMeterString() {
        return "";
    }

    @Override
    public boolean hasGui() {
        return true;
    }
}
