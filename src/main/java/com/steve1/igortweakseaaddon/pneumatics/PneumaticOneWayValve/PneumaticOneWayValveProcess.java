package com.steve1.igortweakseaaddon.pneumatics.PneumaticOneWayValve;

import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.PneumaticConnection;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.PneumaticLoad;
import mods.eln.sim.IProcess;

public class PneumaticOneWayValveProcess implements IProcess {

    public PneumaticOneWayValveElement element;
    public PneumaticConnection connection;
    public PneumaticLoad loadA;
    public PneumaticLoad loadB;

    public double pressure_set=0;
    public double to_open_area;
    public double to_close_area;
    public double max_pressure;
    public double max_pressure_inv;
    public double max_area;
    public boolean mode_is_p_diff=true;
    public boolean side_is_yellow=true;
    public boolean open_if_above=true;

    public PneumaticOneWayValveProcess(PneumaticOneWayValveElement got_element) {
        element=got_element;
        connection=element.connection;
        loadA=element.loadA;
        loadB=element.loadB;
        settings_changed();
    }

    public void settings_changed() {
        max_pressure=element.pipe_descriptor.max_pressure;
        max_pressure_inv=1/max_pressure;
        max_area=element.max_area;
        pressure_set=element.set_pressure;
        to_open_area=element.to_open_area;
        to_close_area=element.to_close_area;
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
            connection.set_area(to_open_area);
            element.is_open=true;
        } else {
            connection.set_area(to_close_area);
            element.is_open=false;
        }
    }
}
