package com.steve1.igortweakseaaddon.pneumatics.PneumaticValve;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.WithPipeInventory.IgorSixNodeWithPipeInventoryElement;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PressureWatchdog;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.NodeBase;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.nbt.NbtElectricalGateInput;
import mods.eln.sim.process.destruct.WorldExplosion;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.pneumaticMask;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.t1PneumaticPipeDescriptor;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;

public class PneumaticValveElement extends IgorSixNodeWithPipeInventoryElement {

    public static final byte setPositionId = 0;

    public NBTPneumaticLoad loadA=new NBTPneumaticLoad("loadA");
    public NBTPneumaticLoad loadB=new NBTPneumaticLoad("loadB");
    public NBTPneumaticLoad loadGate=new NBTPneumaticLoad("loadGate");
    public NBTPneumaticConnection pconnection=new NBTPneumaticConnection("pconnection");
    public NbtElectricalGateInput gate = new NbtElectricalGateInput("gate");
    public IProcess valve_process;

    public PressureWatchdog pressure_watchdogA=new PressureWatchdog();
    public PressureWatchdog pressure_watchdogB=new PressureWatchdog();
    public PressureWatchdog pressure_watchdogGate=new PressureWatchdog();

    public PneumaticValveDescriptor descriptor;

    public double max_area;
    public double open_value=0;
    public boolean selected_position=false;

    public PneumaticValveElement(SixNode sixNode, Direction side, SixNodeDescriptor passed_descriptor) {
        super(sixNode, side, passed_descriptor);
        descriptor=(PneumaticValveDescriptor) passed_descriptor;

        t1PneumaticPipeDescriptor.apply_to_reset(loadGate);
        t1PneumaticPipeDescriptor.apply_to(pressure_watchdogGate);

        pconnection.connect(loadA,loadB);
        pconnection.can_fall_asleep=false;

        pressure_watchdogA
                .set(loadA)
                .set(new WorldExplosion(this).cableExplosion());
        slowProcessList.add(pressure_watchdogA);

        pressure_watchdogB
                .set(loadB)
                .set(new WorldExplosion(this).cableExplosion());
        slowProcessList.add(pressure_watchdogB);

        pressure_watchdogGate
                .set(loadGate)
                .set(new WorldExplosion(this).cableExplosion());
        slowProcessList.add(pressure_watchdogGate);

        pneumaticComponentList.add(pconnection);
        pneumaticLoadList.add(loadA);
        pneumaticLoadList.add(loadB);
        pneumaticLoadList.add(loadGate);
        electricalLoadList.add(gate);

        valve_process=new IProcess() {
            @Override
            public void process(double time) {
                if (!selected_position) {
                    open_value = Math.max(gate.getNormalized(), loadGate.get_pressure_normalized());
                } else {
                    open_value = 1-Math.max(gate.getNormalized(), loadGate.get_pressure_normalized());
                }
                pconnection.set_area(open_value*max_area);
            }
        };

        pneumaticProcessList.add(valve_process);
    }

    @Override
    public void pipe_descriptor_changed() {
        pipe_descriptor.apply_to_reset(loadA);
        pipe_descriptor.apply_to_reset(loadB);
        pipe_descriptor.apply_to(pconnection);
        pipe_descriptor.apply_to(pressure_watchdogA);
        pipe_descriptor.apply_to(pressure_watchdogB);
        max_area=pipe_descriptor.area;
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeBoolean(selected_position);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte igorNetworkUnserialize(DataInputStream stream) {
        byte res=super.igorNetworkUnserialize(stream);
        if (res==-128) {
            return -128;
        }
        try {
            switch (res) {
                case setPositionId:
                    selected_position=stream.readBoolean();
                    break;
                default:
                    return res;
            }
            needPublish();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -128;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        selected_position=nbt.getBoolean("selected_position");
        open_value=nbt.getDouble("open_value");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("selected_position",selected_position);
        nbt.setDouble("open_value",open_value);
    }

    @Override
    public PneumaticLoad getPneumaticLoad(LRDU lrdu,int mask) {
        if (front==lrdu) {
            return loadGate;
        }
        if (has_item && front.left()==lrdu) {
            return loadA;
        }
        if (has_item && front.right()==lrdu) {
            return loadB;
        }
        return null;
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (front.inverse() == lrdu) {
            return gate;
        }
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (front.inverse() == lrdu) {
            return NodeBase.maskElectricalInputGate;
        }
        if (front==lrdu) {
            return pneumaticMask;
        }
        if (has_item && (front.left()==lrdu || front.right()==lrdu)) {
            return pneumaticMask;
        }
        return 0;
    }

    @Override
    public String multiMeterString() {
        return "Open: "+plot_percent(open_value)+", §ePA: "+plot_pascals_atmospheres(loadA.get_pressure())+"§f, §a PB: "+plot_pascals_atmospheres(loadB.get_pressure())+"§f, Flow: "+plot_speed(Math.abs(pconnection.get_speed()));
    }

    @Override
    public String thermoMeterString() {
        return "";
    }
}
