package com.steve1.igortweakseaaddon.misc.PneumaticSim;

import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.PneumaticConnection;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.PneumaticLoad;
import mods.eln.Eln;
import mods.eln.sim.IProcess;

import java.util.ArrayList;
import java.util.HashSet;

public class PneumaticSimulator implements IProcess {

    public static final double global_pneumatic_epsilon_small  =1e-6;
    public static final double global_pneumatic_epsilon_medium =1e-4;
    public static final double global_pneumatic_epsilon_big    =1e-2;

    public static final double R_T_gas=2437; // R*T at T=20 celsius
    public static final double R_T_gas_inv=1/R_T_gas;

    public HashSet<PneumaticConnection> pneumatic_components_added=new HashSet<>();

    public ArrayList<PneumaticConnection> pneumatic_components=new ArrayList<PneumaticConnection>();

    public ArrayList<PneumaticConnection> active_pneumatic_components=new ArrayList<PneumaticConnection>();

    public HashSet<PneumaticConnection> active_pneumatic_components_added=new HashSet<>();

    public HashSet<PneumaticConnection> components_to_activate=new HashSet<PneumaticConnection>();

    public HashSet<PneumaticConnection> components_to_deactivate=new HashSet<PneumaticConnection>();

    public ArrayList<PneumaticLoad> pneumatic_loads=new ArrayList<PneumaticLoad>();

    public HashSet<PneumaticLoad> pneumatic_loads_added=new HashSet<>();

    public ArrayList<IProcess> pneumatic_processes=new ArrayList<IProcess>();

    public HashSet<IProcess> pneumatic_processes_added=new HashSet<>();

    public double left_time=0;
    public double left_time_sleep_check=0;
    public double one_call_time;
    public double sleep_check_interval;

    public PneumaticSimulator(double one_call_time,double sleep_check_interval) {
        this.one_call_time=one_call_time;
        this.sleep_check_interval=sleep_check_interval;
    }

    public void start() {
        pneumatic_components_added.clear();
        pneumatic_components.clear();
        active_pneumatic_components.clear();
        active_pneumatic_components_added.clear();
        components_to_activate.clear();
        components_to_deactivate.clear();
        pneumatic_loads.clear();
        pneumatic_loads_added.clear();
        pneumatic_processes.clear();
        pneumatic_processes_added.clear();

        left_time=0;
        left_time_sleep_check=0;

        Eln.simulator.addThermalFastProcess(this);
    }

    public void stop() {
        pneumatic_components_added.clear();
        pneumatic_components.clear();
        active_pneumatic_components.clear();
        active_pneumatic_components_added.clear();
        components_to_activate.clear();
        components_to_deactivate.clear();
        pneumatic_loads.clear();
        pneumatic_loads_added.clear();
        pneumatic_processes.clear();
        pneumatic_processes_added.clear();

        left_time=0;
        left_time_sleep_check=0;

        Eln.simulator.removeThermalFastProcess(this);
    }

    @Override
    public void process(double time) {
        left_time+=time;
        left_time_sleep_check+=time;
        while (left_time>0) {
            sim_step(one_call_time);
            for (IProcess process: pneumatic_processes) {
                process.process(one_call_time);
            }
            left_time-=one_call_time;
        }
        while (left_time_sleep_check>0) {
            sim_sleepy_step();
            left_time_sleep_check-=sleep_check_interval;
        }
    }

    public void sim_step(double time) {

        update_activation_deactivation();

        for (PneumaticConnection connection: active_pneumatic_components) {
            connection.start_step(time);
        }

        for (PneumaticLoad load: pneumatic_loads) {
            load.check_mass_step();
        }

        for (PneumaticConnection connection: active_pneumatic_components) {
            connection.move_mass_step(time);
        }

        for (PneumaticLoad load: pneumatic_loads) {
            load.step_fin();
        }

        //logger.info("=================================================");

        for (PneumaticLoad load: pneumatic_loads) {
            load.sanitize();
            //logger.info("mass: "+load.get_mass()+", speed: "+plot_speed(load.get_speed())+", pressure: "+plot_pascals(load.get_pressure()));
        }

        for (PneumaticConnection connection: active_pneumatic_components) {
            connection.sanitize();
            //logger.info("speed: "+plot_speed(connection.speed)+", pdiff: "+plot_pascals(connection.load1.pressure-connection.load2.pressure));
        }
    }

    public void sim_sleepy_step() {

        for (PneumaticConnection connection: pneumatic_components) {
            connection.refresh_sleeping();
        }

        update_activation_deactivation();

        for (PneumaticConnection connection: active_pneumatic_components) {
            connection.sleepy_step();
        }
    }

    public void update_activation_deactivation() {
        //logger.info("got connections: "+pneumatic_components.size()+", active: "+active_pneumatic_components.size()+", active stored: "+active_pneumatic_components_added.size());
        for (PneumaticConnection connection : components_to_activate) {
            if (!active_pneumatic_components_added.add(connection)) {
                continue;
            }
            active_pneumatic_components.add(connection);
        }
        for (PneumaticConnection connection : components_to_deactivate) {
            if (!active_pneumatic_components_added.remove(connection)) {
                continue;
            }
            active_pneumatic_components.remove(connection);
        }
        components_to_activate.clear();
        components_to_deactivate.clear();
    }

    public void activatePneumaticComponent(PneumaticConnection p_component) {
        if (!pneumatic_components_added.contains(p_component)) {
            return;
        }
        components_to_activate.add(p_component);
    }

    public void deactivatePneumaticComponent(PneumaticConnection p_component) {
        components_to_deactivate.add(p_component);
    }

    public void addPneumaticLoad(PneumaticLoad p_load) {
        if (!pneumatic_loads_added.add(p_load)) {
            return;
        }
        pneumatic_loads.add(p_load);
    }

    public void removePneumaticLoad(PneumaticLoad p_load) {
        if (!pneumatic_loads_added.remove(p_load)) {
            return;
        }
        pneumatic_loads.remove(p_load);
    }

    public void addPneumaticProcess(IProcess p_process) {
        if (!pneumatic_processes_added.add(p_process)) {
            return;
        }
        pneumatic_processes.add(p_process);
    }

    public void removePneumaticProcess(IProcess p_process) {
        if (!pneumatic_processes_added.remove(p_process)) {
            return;
        }
        pneumatic_processes.remove(p_process);
    }

    public void addPneumaticComponent(PneumaticConnection p_component) {
        if (!pneumatic_components_added.add(p_component)) {
            return;
        }
        pneumatic_components.add(p_component);
        if (active_pneumatic_components_added.add(p_component)) {
            active_pneumatic_components.add(p_component);
        }
        components_to_deactivate.remove(p_component);
        components_to_activate.remove(p_component);
    }

    public void removePneumaticComponent(PneumaticConnection p_component) {
        if (!pneumatic_components_added.remove(p_component)) {
            return;
        }
        pneumatic_components.remove(p_component);
        active_pneumatic_components.remove(p_component);
        active_pneumatic_components_added.remove(p_component);
        components_to_deactivate.remove(p_component);
        components_to_activate.remove(p_component);
    }
}
