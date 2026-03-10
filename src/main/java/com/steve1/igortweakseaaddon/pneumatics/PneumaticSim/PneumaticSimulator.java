package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim;

import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.Eln;
import mods.eln.sim.IProcess;

import java.util.ArrayList;
import java.util.Collection;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class PneumaticSimulator implements IProcess {

    public ArrayList<PneumaticConnection> pneumatic_components=new ArrayList<PneumaticConnection>();

    public ArrayList<PneumaticLoad> pneumatic_loads=new ArrayList<PneumaticLoad>();

    public ArrayList<IProcess> pneumatic_processes=new ArrayList<IProcess>();

    public double left_time=0;
    public double one_call_time;

    public PneumaticSimulator(double one_call_time) {
        this.one_call_time=one_call_time;
    }

    public void free() {
        Eln.simulator.removeSlowProcess(this);
    }

    @Override
    public void process(double time) {
        left_time+=time;
        while (left_time>0) {
            sim_step(one_call_time);
            for (IProcess process: pneumatic_processes) {
                process.process(one_call_time);
            }
            left_time-=one_call_time;
        }
    }

    public void sim_step(double time) {
        for (PneumaticConnection connection: pneumatic_components) {
            connection.start_step(time);
        }

        for (PneumaticLoad connection: pneumatic_loads) {
            connection.check_mass_step();
        }

        for (PneumaticConnection connection: pneumatic_components) {
            connection.move_mass_step(time);
        }

        for (PneumaticLoad connection: pneumatic_loads) {
            connection.step_fin();
        }

        //logger.info("=================================================");

        for (PneumaticLoad load: pneumatic_loads) {
            load.sanitize();
            //logger.info("mass: "+load.get_mass()+", speed: "+load.speed);
        }

        for (PneumaticConnection connection: pneumatic_components) {
            connection.sanitize();
            //logger.info("speed: "+connection.speed+", pdiff: "+(connection.load1.pressure-connection.load2.pressure));
        }
    }

    public void addPneumaticLoad(PneumaticLoad p_load) {
        pneumatic_loads.add(p_load);
    }

    public void removePneumaticLoad(PneumaticLoad p_load) {
        pneumatic_loads.remove(p_load);
    }

    public void addAllPneumaticProcess(Collection<IProcess> p_processes) {
        pneumatic_processes.addAll(p_processes);
    }

    public void addPneumaticProcess(IProcess p_process) {
        pneumatic_processes.add(p_process);
    }

    public void removeAllPneumaticProcess(Collection<IProcess> p_processes) {
        pneumatic_processes.removeAll(p_processes);
    }

    public void removePneumaticProcess(IProcess p_process) {
        pneumatic_processes.remove(p_process);
    }

    public void addAllPneumaticComponent(Collection<PneumaticConnection> p_components) {
        pneumatic_components.addAll(p_components);
    }

    public void addPneumaticComponent(PneumaticConnection p_component) {
        pneumatic_components.add(p_component);
    }

    public void removeAllPneumaticComponent(Collection<PneumaticConnection> p_components) {
        pneumatic_components.removeAll(p_components);
    }

    public void removePneumaticComponent(PneumaticConnection p_component) {
        pneumatic_components.remove(p_component);
    }
}
