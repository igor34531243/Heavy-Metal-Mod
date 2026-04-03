package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import mods.eln.sim.mna.state.State;

import java.util.HashSet;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;
import static com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.PneumaticSimulator.*;

public class PneumaticLoad extends State {

    public double pressure;
    public double resistance;
    public double volume;
    public double area;
    public double inv_volume;
    public double inv_volume_density;
    public double R_T_inv_volume;
    public double density;

    public double next_mass=state;
    public double next_mass_coefficient=1;
    public boolean has_sleeping_connection=false;
    public boolean changed_pressure=false;
    public HashSet<PneumaticConnection> connections=new HashSet<>();
    public double sleeping_mass_change=0;
    public double previous_step_pressure=0;
    public double pressure_epsilon=global_pneumatic_epsilon_big;
    public double deafult_state=0;

    public PneumaticLoad() {
        resistance=base_air_resistance;
        volume=small_pneumatic_volume;
        area=small_pneumatic_area;
        state=base_atmospheric_pressure*volume*R_T_gas_inv;
        update_cache();
    }

    public void add_next(double added_mass) {
        // queing values for next steps
        // we dont apply them right away to ensure
        // that no connection gets priority over another
        // or it would cause some weird issues

        next_mass+=added_mass;
    }

    public void check_mass_step() {

        // checking that we dont draw more mass than is present
        // and if we are then informing pneumatic connections
        // how much mass is safe to actualy move

        state+=sleeping_mass_change;

        if (state<0) {
            state=0;
            activate_connections();
        }

        next_mass_coefficient=1;
        if (next_mass<=0) {

            next_mass_coefficient = 0.99 * state / (state-next_mass);

            if (next_mass_coefficient<0.00001) {
                next_mass_coefficient=0;
            }

            // adding 99 to ensure that we dont drop mass to 0
            // or at boundary it could cause mass to go negative
            // due to floating point errors
        }
    }

    public void step_fin() {

        // finaly applying the speed and pressure values
        // and resetting collected along the step values

        pressure=state*R_T_inv_volume;
        changed_pressure=Math.abs(previous_step_pressure-pressure)> pressure_epsilon;

        density=state*inv_volume_density;

        next_mass_coefficient=1;
        next_mass=state;

        if (changed_pressure) {
            if (has_sleeping_connection) {
                activate_connections();
            }
            previous_step_pressure=pressure;
            pressure_epsilon=pressure*0.0001;
        }
    }

    public void activate_connections() {
        if (has_sleeping_connection) {
            for (PneumaticConnection connection : connections) {
                if (connection.sleeping) {
                    connection.activate_partial(this);
                }
            }
            check_for_sleeping_connections();
        }
    }

    public void check_for_sleeping_connections() {
        has_sleeping_connection=false;
        sleeping_mass_change=0;
        for (PneumaticConnection connection : connections) {
            if (connection.sleeping) {
                has_sleeping_connection=true;
                sleeping_mass_change+=connection.get_sleepy_to_move_mass(this);
            }
        }
    }

    public void sanitize() {
        state=sanitize_number(state, deafult_state);
    }

    public void update_cache() {
        inv_volume=1/volume;
        if (volume!=0) {
            inv_volume_density=inv_volume;
        } else {
            inv_volume_density=0;
        }
        R_T_inv_volume=R_T_gas*inv_volume;
        pressure=state*R_T_inv_volume;
        density=state*inv_volume;
        deafult_state=base_atmospheric_pressure*volume*R_T_gas_inv;
    }

    public void set_mass(double new_mass) {
        if (new_mass<0) {
            logger.error("trying to set air mass to negative!");
            return;
        }
        state=new_mass;
    }

    public void add_mass(double added_mass) {
        if (state+added_mass<0) {
            logger.error("trying to set air mass to negative when adding!");
            return;
        }
        state+=added_mass;
    }

    public void remove_mass(double removed_mass) {
        if (state-removed_mass<0) {
            logger.error("trying to set air mass to negative when removing!");
            return;
        }
        state-=removed_mass;
    }

    public void move_mass(PneumaticLoad move_to,double moved_mass) {
        if (state-moved_mass<0 || move_to.state+moved_mass<0) {
            logger.error("trying to set air mass to negative when moving!");
            return;
        }
        move_to.state+=moved_mass;
        this.state-=moved_mass;
    }

    public void set_pressure(double pressure) {
        state=pressure*volume*R_T_gas_inv;
        update_cache();
    }

    public double get_mass() {
        return state;
    }

    public double get_pressure() {
        return pressure;
    }

    public double get_pressure_normalized() {
        double res_pressure=pressure-logic_pressure_min;
        if (res_pressure<0) {
            return 0;
        } else if (res_pressure>logic_pressure_range) {
            return 1;
        }
        return res_pressure*logic_pressure_range_inv;
    }

    public double get_speed() {
        double speed_positive=0;
        double speed_negative=0;
        for (PneumaticConnection connection : connections) {
            double speed=connection.get_relative_speed(this);
            if (speed>0) {
                speed_positive+=speed;
            } else {
                speed_negative-=speed;
            }
        }
        return (speed_positive+speed_negative)/2;
    }

    public double get_resistance() {
        return resistance;
    }

    public double get_area() {
        return area;
    }

    public double get_volume() {
        return volume;
    }

    public double get_density() {
        return density;
    }

    public void set_area(double new_area) {
        if (new_area<0) {
            logger.error("trying to set area to negative!");
            return;
        }
        area=new_area;
        update_cache();
    }

    public void set_volume(double new_volume) {
        if (new_volume<0) {
            logger.error("trying to set volume to negative!");
            return;
        }
        volume=new_volume;
        update_cache();
    }

    public void set_resistance(double new_resistance) {
        if (new_resistance<0) {
            logger.error("trying to set resistance to negative!");
            return;
        }
        resistance=new_resistance;
        update_cache();
    }

    public void set(double resistance,double area,double volume) {
        set_resistance(resistance);
        set_area(area);
        set_volume(volume);
    }
}
