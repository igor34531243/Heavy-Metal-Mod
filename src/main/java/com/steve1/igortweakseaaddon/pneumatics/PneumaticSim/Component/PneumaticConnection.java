package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import mods.eln.sim.mna.SubSystem;
import mods.eln.sim.mna.component.Component;
import mods.eln.sim.mna.state.State;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;
import static com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.PneumaticSimulator.global_pneumatic_epsilon_medium;
import static com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.PneumaticSimulator.global_pneumatic_epsilon_small;

public class PneumaticConnection extends Component{

    public PneumaticLoad load1;
    public PneumaticLoad load2;

    public boolean working=false;
    public boolean ready_to_sleep=true;
    public boolean sleeping=false;
    public boolean can_fall_asleep=true;

    public double speed=0;
    public double area=small_pneumatic_area;
    public double length=1;
    public double resistance=base_air_resistance;

    public double to_move_mass=0;
    public double to_move_mass_sleepy=0;
    public double previous_step_speed=0;
    public double speed_epsilon=global_pneumatic_epsilon_medium;

    public static final double stable_d_pressure = base_atmospheric_pressure*0.05;
    public static final double stable_d_pressure_inv = 1/stable_d_pressure;

    public PneumaticConnection() {
        this(null,null);
    }

    public PneumaticConnection(PneumaticLoad load1,PneumaticLoad load2) {
        connect(load1,load2);
    }

    public void start_step(double time) {
        if (!working) {
            return;
        }

        // the simulation is pretty simple:
        // we move air from one load to another by mass
        // and amount of mass moved is dM=(mass/length)*speed*time
        // but the speed changes with some inertia:
        // we slow down speed by dv=speed*resistance*time
        // and increase it by dv=(pressure1-pressure2)/(length*(average_density))*time
        // after the simulation step is finished we apply the parameters to loads

        // comments are somewhat outdated since i changed the code a lot
        // but general idea is same

        double d_pressure=load1.pressure-load2.pressure;
        double average_density =(load1.density+load2.density)/2;

        double acceleration_pressure=0;
        if (average_density >1e-12 && d_pressure!=0) {
            acceleration_pressure = 0.3 * d_pressure / (length * average_density);
        }

        speed+=acceleration_pressure*time;
        speed*=(1-resistance*time);

        to_move_mass=average_density*area*speed*time;

        if (Math.abs(speed-previous_step_speed)>speed_epsilon) {
            ready_to_sleep=false;
            previous_step_speed=speed;
            speed_epsilon=speed*0.0001;
        }

        if (Math.abs(speed)> global_pneumatic_epsilon_small || Math.abs(acceleration_pressure)> global_pneumatic_epsilon_small) {
            load1.add_next(-to_move_mass);
            load2.add_next(to_move_mass);
        } else {
            to_move_mass=0;
            speed=0;
        }

    }

    public void move_mass_step(double time) {
        if (!working) {
            return;
        }

        // done after each load checks if it has enough mass
        // moving only the smallest amount which two loads
        // can provide to not make mass negative

        if (to_move_mass!=0) {
            double mass_cof = Math.min(load1.next_mass_coefficient, load2.next_mass_coefficient);
            double actual_to_move_mass = to_move_mass * mass_cof;
            load1.move_mass(load2, actual_to_move_mass);
        }
    }

    public void sleepy_step() {
        this.ready_to_sleep=true;
    }

    public double get_sleepy_to_move_mass(PneumaticLoad load) {
        if (!working) {
            return 0;
        }
        if (load==load1) {
            return -to_move_mass_sleepy;
        } else if (load==load2) {
            return to_move_mass_sleepy;
        }
        logger.error("Trying to get to_move_mass_sleepy with node not related to connection or null");
        return 0;
    }

    public double get_relative_speed(PneumaticLoad load) {
        if (load==load1) {
            return -speed;
        } else if (load==load2) {
            return speed;
        }
        logger.error("Trying to get relative speed with node not related to connection or null");
        return 0;
    }

    public void refresh_sleeping() {
        if (!sleeping && ready_to_sleep && can_fall_asleep) {
            deactivate();
        }
    }

    public void deactivate() {
        to_move_mass_sleepy=to_move_mass;
        previous_step_speed=speed;
        this.sleeping=true;
        if (load1!=null) {
            load1.check_for_sleeping_connections();
        }
        if (load2!=null) {
            load2.check_for_sleeping_connections();
        }
        pneumatic_simulator.deactivatePneumaticComponent(this);
    }

    public void activate() {
        activate_partial(null);
    }

    public void activate_partial(PneumaticLoad load) {
        this.sleeping=false;
        this.ready_to_sleep=false;
        if (load1!=null && load1!=load) {
            load1.check_for_sleeping_connections();
        }
        if (load2!=null && load2!=load) {
            load2.check_for_sleeping_connections();
        }
        pneumatic_simulator.activatePneumaticComponent(this);
    }

    public void sanitize() {
        speed=sanitize_number(speed,0);
    }

    public double get_area() {
        return area;
    }

    public double get_resistance() {
        return resistance;
    }

    public double get_speed() {
        return speed;
    }

    public void set(double area, double resistance) {
        set(area,resistance,1);
    }

    public void set(double area, double resistance, double length) {
        if (resistance<0) {
            logger.error("trying to set resistance to negative! ");
            return;
        } else if (area<0) {
            logger.error("trying to set area to negative! ");
            return;
        } else if (length<0) {
            logger.error("trying to set length to negative! ");
            return;
        }
        this.area=area;
        this.resistance=resistance;
        this.length=length;
    }

    public void set_area(double area) {
        if (area<0) {
            logger.error("trying to set area to negative!");
            return;
        }
        this.area=area;
    }

    public void set_resistance(double resistance) {
        if (resistance<0) {
            logger.error("trying to set resistance to negative!");
            return;
        }
        this.resistance=resistance;
    }

    public void set_length(double length) {
        if (length<0) {
            logger.error("trying to set length to negative!");
            return;
        }
        this.length=length;
    }

    public void set_speed(double speed) {
        this.speed=speed;
    }

    public void set_high_resistance() {
        this.resistance=Double.POSITIVE_INFINITY;
    }

    public void load_stats() {
        if (load1 == null || load2 == null) {
            return;
        }
        area=(load1.get_area()+load2.get_area())/2;
        resistance=(load1.get_resistance()+load2.get_resistance())/2;
    }

    public void connect(PneumaticLoad load1,PneumaticLoad load2) {
        breakConnection();

        this.load1=load1;
        this.load2=load2;

        if (load1!=null) {
            load1.connections.add(this);
            load1.check_for_sleeping_connections();
        }
        if (load2!=null) {
            load2.connections.add(this);
            load2.check_for_sleeping_connections();
        }
        if (load1!=null && load2!=null) {
            working = true;
            activate();
        }
    }

    @Override
    public void breakConnection() {
        working=false;
        sleeping=false;
        if (load1 != null) {
            load1.connections.remove(this);
            load1.check_for_sleeping_connections();
        }
        if (load2 != null) {
            load2.connections.remove(this);
            load2.check_for_sleeping_connections();
        }
    }

    @Override
    public void applyTo(SubSystem s) {

    }

    @Override
    public State[] getConnectedStates() {
        return new State[]{load1, load2};
    }
}
