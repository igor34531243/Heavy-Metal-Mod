package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import mods.eln.sim.mna.state.State;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.sanitize_number;

public class PneumaticLoad extends State {

    public double pressure= base_atmospheric_pressure;
    public double speed=0;
    public double resistance=base_air_resistance;
    public double volume=small_pneumatic_volume;
    public double area=small_pneumatic_area;
    public double inv_volume=1/volume;
    public double R_T_inv_volume=R_T_gas*inv_volume;

    public double next_speed=0;
    public double next_mass=state;
    public double next_amount=0;
    public double next_mass_coefficient=1;

    public static final double R_T_gas=2437; // R*T at T=20 celsius
    public static final double R_T_gas_inv=1/R_T_gas;

    public PneumaticLoad() {
        state=pressure*volume*R_T_gas_inv;
    }

    public void add_next(double added_speed,double added_mass) {
        // queing values for next steps
        // we dont apply them right away to ensure
        // that no connection gets priority over another
        // or it would cause some weird issues

        next_speed=(next_speed*next_amount+added_speed)/(next_amount+1);
        next_mass+=added_mass;
        next_amount+=1;
    }

    public void check_mass_step() {

        // checking that we dont draw more mass than is present
        // and if we are then informing pneumatic connections
        // how much mass is safe to actualy move

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

        speed=next_speed;

        double static_pressure=state*R_T_inv_volume;
        double dynamic_pressure= get_density()*speed*speed/2;

        pressure=static_pressure/(1+dynamic_pressure/(static_pressure+0.00000001));
        if (pressure<0) {
            pressure=0;
        }

        next_mass_coefficient=1;
        next_speed=speed;
        next_mass=state;
        next_amount=0;
    }

    public void sanitize() {
        pressure=sanitize_number(pressure, base_atmospheric_pressure);
        speed=sanitize_number(speed,0);
        state=sanitize_number(state, base_atmospheric_pressure *volume/R_T_gas);
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

    public double get_mass() {
        return state;
    }

    public double get_pressure() {
        return pressure;
    }

    public double get_speed() {
        return speed;
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
        if (volume==0) {
            return 0;
        }
        return state*inv_volume;
    }

    public void set_area(double new_area) {
        if (new_area<0) {
            logger.error("trying to set area to negative!");
            return;
        }
        area=new_area;
    }

    public void set_volume(double new_volume) {
        if (new_volume<0) {
            logger.error("trying to set volume to negative!");
            return;
        }
        volume=new_volume;
        inv_volume=1/volume;
        R_T_inv_volume=R_T_gas*inv_volume;
    }

    public void set_resistance(double new_resistance) {
        if (new_resistance<0) {
            logger.error("trying to set resistance to negative!");
            return;
        }
        resistance=new_resistance;
    }

    public void set(double resistance,double area,double volume) {
        set_resistance(resistance);
        set_area(area);
        set_volume(volume);
    }
}
