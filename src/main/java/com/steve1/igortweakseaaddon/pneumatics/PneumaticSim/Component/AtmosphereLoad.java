package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;

public class AtmosphereLoad extends ConstantPressureLoad {

    public AtmosphereLoad(String name) {
        super(name,base_armospheric_pressure);
    }

    @Override
    public void set_constant_pressure(double constant_pressure) {
        logger.error("trying to set pressure for atmospheric load");
    }
}
