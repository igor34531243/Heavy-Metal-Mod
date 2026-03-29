package com.steve1.igortweakseaaddon.pneumatics.PneumaticValve;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElement;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PressureWatchdog;
import mods.eln.Eln;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBase;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.nbt.NbtElectricalGateInput;
import mods.eln.sim.process.destruct.WorldExplosion;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.pneumaticMask;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.t1PneumaticPipeDescriptor;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;

public class PneumaticValveElement extends IgorSixNodeElement {
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
    public PneumaticPipeDescriptor pipe_descriptor;

    public double max_area;
    public double open_value=0;

    public PneumaticValveElement(SixNode sixNode, Direction side, SixNodeDescriptor passed_descriptor) {
        super(sixNode, side, passed_descriptor);
        descriptor= (PneumaticValveDescriptor) passed_descriptor;
        pipe_descriptor=descriptor.pipe_descriptor;

        max_area=pipe_descriptor.area;

        pipe_descriptor.apply_to_reset(loadA);
        pipe_descriptor.apply_to_reset(loadB);
        pipe_descriptor.apply_to(pconnection);
        pipe_descriptor.apply_to(pressure_watchdogA);
        pipe_descriptor.apply_to(pressure_watchdogB);

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
                open_value=Math.max(gate.getNormalized(),loadGate.get_pressure_normalized());
                pconnection.set_area(open_value*max_area);
            }
        };

        pneumaticProcessList.add(valve_process);
    }

    @Override
    public PneumaticLoad getPneumaticLoad(LRDU lrdu,int mask) {
        if (front.left()==lrdu) {
            return loadA;
        }
        if (front.right()==lrdu) {
            return loadB;
        }
        if (front==lrdu) {
            return loadGate;
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
        if (front.left()==lrdu || front.right()==lrdu || front==lrdu) {
            return pneumaticMask;
        }
        if (front.inverse() == lrdu) {
            return NodeBase.maskElectricalInputGate;
        }
        return 0;
    }

    @Override
    public String multiMeterString() {
        return "Open: "+plot_percent(open_value)+", PA: "+plot_pascals_atmospheres(loadA.get_pressure())+", PB: "+plot_pascals_atmospheres(loadB.get_pressure())+", Flow: "+plot_speed(Math.abs(pconnection.get_speed()));
    }

    @Override
    public String thermoMeterString() {
        return "";
    }
}
