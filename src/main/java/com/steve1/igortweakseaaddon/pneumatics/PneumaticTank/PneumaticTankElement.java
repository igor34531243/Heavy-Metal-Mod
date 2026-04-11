package com.steve1.igortweakseaaddon.pneumatics.PneumaticTank;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeElement;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PressureWatchdog;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.process.destruct.WorldExplosion;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.pneumaticMask;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;

public class PneumaticTankElement extends IgorTransparentNodeElement {

    PneumaticTankDescriptor descriptor;
    PneumaticPipeDescriptor pipe_descriptor;

    NBTPneumaticLoad pneumatic_load=new NBTPneumaticLoad("pneumatic_load");
    PressureWatchdog pressureWatchdog=new PressureWatchdog();

    public PneumaticTankElement(TransparentNode transparentNode, TransparentNodeDescriptor got_descriptor) {
        super(transparentNode, got_descriptor);
        descriptor=(PneumaticTankDescriptor)got_descriptor;
        pipe_descriptor=descriptor.pipe_descriptor;

        pipe_descriptor.apply_to_reset(pneumatic_load);
        pneumatic_load.set_volume(1);
        pneumatic_load.set_area(pneumatic_load.get_area()*2);

        pressureWatchdog.set(pneumatic_load);
        pressureWatchdog.set(new WorldExplosion(this).cableExplosion());
        pipe_descriptor.apply_to(pressureWatchdog);

        pneumaticLoadList.add(pneumatic_load);
        slowProcessList.add(pressureWatchdog);
    }

    @Override
    public PneumaticLoad getPneumaticLoad(Direction direction, LRDU lrdu) {
        return pneumatic_load;
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        return pneumaticMask;
    }

    @Override
    public String multiMeterString(Direction side) {
        return "Pressure: "+plot_pascals_atmospheres(pneumatic_load.get_pressure())+", Flow: "+plot_speed(pneumatic_load.get_speed());
    }

    @Override
    public String thermoMeterString(Direction side) {
        return "";
    }
}
