package com.steve1.igortweakseaaddon.pneumatics.PneumaticOutlet;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeElement;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTAtmosphereLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;

public class PneumaticOutletElement extends IgorTransparentNodeElement {
    public NBTPneumaticLoad pneumatic_load;
    public NBTAtmosphereLoad atmosphere_load;
    public NBTPneumaticConnection pneumatic_connection;

    public PneumaticOutletElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        pneumatic_load=new NBTPneumaticLoad("pneumatic_load");
        pneumaticLoadList.add(pneumatic_load);
        atmosphere_load=new NBTAtmosphereLoad("atmosphere_load");
        pneumaticLoadList.add(atmosphere_load);
        pneumatic_connection=new NBTPneumaticConnection("pneumatic_connection",pneumatic_load,atmosphere_load);
        pneumaticComponentList.add(pneumatic_connection);
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction direction, LRDU lrdu) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction direction, LRDU lrdu) {
        return null;
    }

    @Override
    public PneumaticLoad getPneumaticLoad(Direction direction, LRDU lrdu) {
        return pneumatic_load;
    }

    @Override
    public int getConnectionMask(Direction direction, LRDU lrdu) {
        return pneumaticMask;
    }

    @Override
    public String multiMeterString(Direction direction) {
        return "Pressure: "+plot_pascals(pneumatic_load.get_pressure())+" "+plot_atmospheres(pneumatic_load.get_pressure())+", Flow: "+plot_speed(pneumatic_load.get_speed());
    }

    @Override
    public String thermoMeterString(Direction direction) {
        return "";
    }
}
