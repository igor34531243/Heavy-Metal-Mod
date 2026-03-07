package com.steve1.igortweakseaaddon.StirlingEngine;

import mods.eln.mechanical.ShaftNetwork;
import mods.eln.misc.Utils;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class StirlingEngineProcess implements IProcess {

    StirlingEngineElement element;

    public StirlingEngineProcess(StirlingEngineElement element) {
        this.element=element;
    }

    @Override
    public void process(double time) {
        double thermal_conductance=1/element.thermal_load_1.Rs;
        double t1=element.thermal_load_1.getT();
        double t2=element.thermal_load_2.getT();
        double dt=Math.abs(t1-t2);
        if (dt<0.01) {
            return;
        }
        double efficiency=(1-(Math.min(t1,t2)+293.15)/(Math.max(t1,t2)+293.15));
        double energy=dt*thermal_conductance*time;

        double cur_energy=element.getShaft().getEnergy();
        double cur_rads=element.getShaft().getRads();

        efficiency=efficiency*(Math.pow(Math.cos(((800-cur_rads)/800)*2*Math.PI),2));

        if (efficiency<0.01) {
            efficiency=0.01;
        }

        //logger.info(Utils.plotPercent("efficiency: ",efficiency));

        if (efficiency>=0.45) {
            element.getShaft().setEnergy(cur_energy + energy * efficiency * 0.7);
        }

        if (t1>t2) {
            element.thermal_load_1.movePowerTo(-energy);
            element.thermal_load_2.movePowerTo(energy*efficiency);
        } else {
            element.thermal_load_2.movePowerTo(-energy);
            element.thermal_load_1.movePowerTo(energy*efficiency);
        }
    }
}
