package com.steve1.igortweakseaaddon.pneumatics.PneumaticHub;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeElement;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import net.minecraft.entity.player.EntityPlayer;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;

public class PneumaticHubElement extends IgorTransparentNodeElement {
    public NBTPneumaticLoad pneumatic_load;

    public PneumaticHubElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        pneumatic_load=new NBTPneumaticLoad("pneumatic_load");
        creativePneumaticPipeDescriptor.apply_to_reset(pneumatic_load);
        pneumaticLoadList.add(pneumatic_load);
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

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        pneumatic_load.set_mass(0);
        return true;
    };
}
