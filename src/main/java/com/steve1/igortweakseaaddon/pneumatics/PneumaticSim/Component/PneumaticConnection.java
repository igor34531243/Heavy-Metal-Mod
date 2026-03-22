package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import mods.eln.sim.mna.SubSystem;
import mods.eln.sim.mna.component.Component;
import mods.eln.sim.mna.state.State;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.check_for_nan;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.sanitize_number;

public class PneumaticConnection extends Component {

    public PneumaticLoad load1;
    public PneumaticLoad load2;

    public boolean working=false;
    public boolean loaded=false;

    public double speed;
    public double area;
    public double length=1;
    public double resistance;

    public double to_move_mass=0;

    public static final double     small_value    = 0.000000000001;
    public static final double small_bigger_value = 0.00000000001;

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
        // and increase it by dv=(pressure1-pressure2)/(length*(average_dencity))*time
        // after the simulation step is finished we apply the parameters to loads
        // also adding small value in a special way to ensure
        // that speed of the load from which mass is moving
        // is higher than the one it moves to (needed for correct loading from nbt
        // since we cant store a direction inside a load when saving
        // so it is guessed from the direction of speed decreasing)

        double d_pressure=load1.pressure-load2.pressure;
        double averge_dencity=(load1.get_density()+load2.get_density())/2;

        double acceleration_pressure=0;
        if (averge_dencity!=0 && d_pressure!=0) {
            acceleration_pressure = d_pressure / (length * averge_dencity);
        }

        if ((speed>=0)==(acceleration_pressure>=0)) {
            acceleration_pressure*=0.1;  // 0.1 to slow it down, or it oscilates wildly
        } else {
            acceleration_pressure*=0.5;  // 0.5 to allow it to reach balance faster or it becomes a jelly
        }

        speed+=acceleration_pressure*time;
        speed*=(1-resistance*time);

        if (speed>343) {
            speed=343;
        } else if (speed<-343) {
            speed=-343;
        }

        check_for_nan(averge_dencity,"averge_dencity");

        to_move_mass=averge_dencity*area*speed*time;

        check_for_nan(to_move_mass,"to_move_mass");

        if (Math.abs(speed)>small_bigger_value && Math.abs(acceleration_pressure)>small_bigger_value) {
            load1.add_next(Math.abs(speed + small_value) - small_value, -to_move_mass);
            load2.add_next(Math.abs(speed - small_value) - small_value, to_move_mass);
        } else {
            to_move_mass=0;
        }
    }

    public void move_mass_step(double time) {

        // done after each load checks if it has enough mass
        // moving only the smallest amount which two loads
        // can provide to not make mass negative

        if (to_move_mass!=0) {
            double mass_cof = Math.min(load1.next_mass_coefficient, load2.next_mass_coefficient);
            double actual_to_move_mass = to_move_mass * mass_cof;
            load1.move_mass(load2, actual_to_move_mass);
        }
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

    public void set_high_resistance() {
        this.resistance=Double.POSITIVE_INFINITY;
    }

    public void load_stats() {
        if (load1 == null || load2 == null || loaded) {
            return;
        }
        area=(load1.get_area()+load2.get_area())/2;
        resistance=(load1.get_resistance()+load2.get_resistance())/2;
//        double speed1 = load1.get_speed();
//        double speed2 = load2.get_speed();
//        double loaded_speed = (Math.abs(speed1) + Math.abs(speed2)) / 2;
//        if (speed1 > speed2) {
//            speed = loaded_speed;
//        } else {
//            speed = -loaded_speed;
//        }
        loaded=true;
    }

    public void connect(PneumaticLoad load1,PneumaticLoad load2) {
        breakConnection();

        this.load1=load1;
        this.load2=load2;

        if (load1!=null) {
            load1.add(this);
        }
        if (load2!=null) {
            load2.add(this);
        }
        if (load1!=null && load2!=null) {
            load_stats();
            if (loaded) {
                working = true;
            }
        }
    }

    @Override
    public void breakConnection() {
        working=false;
        if (load1 != null) load1.remove(this);
        if (load2 != null) load2.remove(this);
    }

    @Override
    public void applyTo(SubSystem s) {

    }

    @Override
    public State[] getConnectedStates() {
        return new State[]{load1, load2};
    }
}
