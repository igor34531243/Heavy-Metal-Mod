package com.steve1.igortweakseaaddon.misc.PneumaticSim.Component;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;

public class NBTAtmosphereLoad extends NBTConstantPressureLoad {

    public NBTAtmosphereLoad(String name) {
        super(name, base_atmospheric_pressure);
    }

    @Override
    public void set_constant_pressure(double constant_pressure) {
        logger.error("trying to set pressure for atmospheric load");
    }
}
