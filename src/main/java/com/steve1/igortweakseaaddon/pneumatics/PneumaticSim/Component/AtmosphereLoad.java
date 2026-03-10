package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import net.minecraft.nbt.NBTTagCompound;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.base_armospheric_pressure;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class AtmosphereLoad extends PneumaticLoad {

    public AtmosphereLoad(String name) {
        super(name);
        state=Double.POSITIVE_INFINITY;
        volume=Double.POSITIVE_INFINITY;
        pressure=base_armospheric_pressure;
    }

    public void add_next(double added_speed,double added_mass) {
        // do nothing obviously, we dont want it changing

        next_speed=(next_speed*next_amount+added_speed)/(next_amount+1);
        next_amount+=1;
    }

    public void check_mass_step() {
        // do almost nothing obviously, we dont want it changing

        next_mass_coefficient=1;
    }

    public void step_fin() {
        // do nothing obviously, we dont want it changing

        speed=next_speed;

        next_mass_coefficient=1;
        next_speed=0;
        next_mass=0;
        next_amount=0;
    }

    public double get_density() {
        return base_armospheric_pressure/R_T_gas;
    }

    public void set_mass(double new_mass) {

    }

    public void add_mass(double added_mass) {

    }

    public void remove_mass(double removed_mass) {

    }

    public void move_mass(PneumaticLoad move_to,double moved_mass) {
        if (state-moved_mass<0 || move_to.state+moved_mass<0) {
            logger.error("trying to set air mass to negative when moving!");
            return;
        }
        move_to.state+=moved_mass;
    }

    public void set_volume(double new_volume) {

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        NBTTagCompound inbt=nbt.getCompoundTag(str);
        state=Double.POSITIVE_INFINITY;
        pressure=base_armospheric_pressure;
        speed=inbt.getDouble("speed");
        resistance=inbt.getDouble("resistance");
        volume=Double.POSITIVE_INFINITY;
        area=inbt.getDouble("area");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        NBTTagCompound inbt= new NBTTagCompound();
        inbt.setDouble("speed",speed);
        inbt.setDouble("resistance",resistance);
        inbt.setDouble("area",area);
        nbt.setTag(str,inbt);
    }
}
