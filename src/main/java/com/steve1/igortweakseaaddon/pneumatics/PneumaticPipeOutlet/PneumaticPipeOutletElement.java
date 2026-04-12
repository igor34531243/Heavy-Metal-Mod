package com.steve1.igortweakseaaddon.pneumatics.PneumaticPipeOutlet;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.WithPipeInventory.IgorSixNodeWithPipeInventoryElement;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.NBTAtmosphereLoad;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;

public class PneumaticPipeOutletElement extends IgorSixNodeWithPipeInventoryElement {

    public NBTPneumaticLoad pneumatic_load=new NBTPneumaticLoad("pneumatic_load");
    public NBTAtmosphereLoad atmosphere_load=new NBTAtmosphereLoad("atmosphere_load");
    public NBTPneumaticConnection pneumatic_connection=new NBTPneumaticConnection("pneumatic_connection",pneumatic_load,atmosphere_load);

    public PneumaticPipeOutletElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);

        pipe_descriptor_changed();

        pneumaticLoadList.add(pneumatic_load);
        pneumaticLoadList.add(atmosphere_load);
        pneumaticComponentList.add(pneumatic_connection);
    }

    @Override
    public void pipe_descriptor_changed() {
        pipe_descriptor.apply_to_reset(pneumatic_load);
        pipe_descriptor.apply_to_reset(atmosphere_load);
        pipe_descriptor.apply_to(pneumatic_connection);
    }

    @Override
    public PneumaticLoad getPneumaticLoad(LRDU lrdu, int mask) {
        if (!has_item) {
            return null;
        }
        return pneumatic_load;
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
        if (!has_item) {
            return 0;
        }
        return pneumaticMask;
    }

    @Override
    public String multiMeterString() {
        return "Pressure: "+plot_pascals(pneumatic_load.get_pressure())+" "+plot_atmospheres(pneumatic_load.get_pressure())+", Flow: "+plot_speed(pneumatic_load.get_speed());
    }

    @Override
    public String thermoMeterString() {
        return "";
    }
}
