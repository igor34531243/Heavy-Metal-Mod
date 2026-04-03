package com.steve1.igortweakseaaddon.pneumatics.PneumaticOneWayValve;

import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.sim.IProcess;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class PneumaticOneWayValveProcess implements IProcess {

    PneumaticOneWayValveElement element;
    PneumaticConnection connection;
    PneumaticLoad loadA;
    PneumaticLoad loadB;

    double pressure_set=0;
    double max_pressure;
    double max_pressure_inv;
    double max_area;
    boolean mode_is_p_diff=true;
    boolean side_is_yellow=true;
    boolean open_if_above=true;

    public PneumaticOneWayValveProcess(PneumaticOneWayValveElement got_element) {
        element=got_element;
        connection=element.connection;
        loadA=element.loadA;
        loadB=element.loadB;
        max_pressure=element.descriptor.pipe_descriptor.max_pressure;
        max_pressure_inv=1/max_pressure;
        max_area=element.max_area;
        settings_changed();
    }

    public void settings_changed() {
        pressure_set= element.set_pressure;
        mode_is_p_diff=element.mode_is_p_diff;
        side_is_yellow=element.side_is_yellow;
        open_if_above=element.open_if_above;
    }

    @Override
    public void process(double time) {
        double pressure_got=0;
        if (mode_is_p_diff) {
            if (side_is_yellow) {
                pressure_got = loadA.get_pressure() - loadB.get_pressure();
            } else {
                pressure_got = loadB.get_pressure() - loadA.get_pressure();
            }
        } else {
            if (side_is_yellow) {
                pressure_got = loadA.get_pressure();
            } else {
                pressure_got = loadB.get_pressure();
            }
        }
        if (open_if_above == (pressure_got>pressure_set)) {
            connection.set_area(max_area);
            element.is_open=true;
        } else {
            connection.set_area(0);
            element.is_open=false;
        }
    }
}
