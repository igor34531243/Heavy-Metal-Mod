package com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PressureWatchdog;
import mods.eln.cable.CableRenderDescriptor;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;

public class PneumaticPipeDescriptor extends IgorSixNodeDescriptor {

    public CableRenderDescriptor cable_render;

    public double resistance=base_air_resistance;
    public double area=small_pneumatic_area;
    public double volume=small_pneumatic_volume;
    public double max_pressure= base_atmospheric_pressure *2;

    public PneumaticPipeDescriptor(String name,CableRenderDescriptor cable_render) {
        super(name, PneumaticPipeElement.class, PneumaticPipeRender.class);
        this.cable_render=cable_render;
    }

    public void set(double resistance,double area,double volume,double max_pressure) {
        this.resistance=resistance;
        this.area=area;
        this.volume=volume;
        this.max_pressure=max_pressure;
    }

    public void apply_to(PneumaticLoad pload) {
        pload.set(resistance,area,volume);
    }

    public void apply_to(PneumaticConnection connection) {
        connection.set(area,resistance,1);
    }

    public void apply_to(PressureWatchdog watchdog) {
        watchdog.set_max_pressure(max_pressure);
    }

    public void set_resistance(double resistance) {
        if (resistance<0) {
            logger.error("Trying to set air resistance to negative!");
            return;
        } else if (resistance>1) {
            logger.error("Trying to set air resistance coefficient to bigger than 1!");
            return;
        }
        this.resistance=resistance;
    }

    public void set_area(double area) {
        if (area<0) {
            logger.error("Trying to set area to negative!");
            return;
        }
        this.area=area;
    }

    public void set_volume(double volume) {
        if (volume<0) {
            logger.error("Trying to set volume to negative!");
            return;
        }
        this.volume=volume;
    }

    public void set_max_pressure(double max_pressure) {
        if (max_pressure<0) {
            logger.error("Trying to set maximum pressure to negative!");
            return;
        }
        this.max_pressure=max_pressure;
    }
}
