package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class PressureWatchdog extends ValueWatchdogCopy{

    PneumaticLoad pload;

    @Override
    public double getValue() {
        if (pload==null) {
            return 0;
        }
        return pload.get_pressure();
    }

    public PressureWatchdog set(PneumaticLoad pload) {
        this.pload=pload;
        return this;
    }

    public PressureWatchdog set_max_pressure(double pressure) {
        if (pressure<0) {
            logger.error("Trying to set maximum pressure to negative!");
            return this;
        }
        this.max=pressure;
        this.min=-1;
        return this;
    }
}
