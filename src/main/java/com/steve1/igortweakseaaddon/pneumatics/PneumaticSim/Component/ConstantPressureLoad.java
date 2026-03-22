package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import net.minecraft.nbt.NBTTagCompound;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.sanitize_number;

public class ConstantPressureLoad extends PneumaticLoad{

    public double constant_pressure;
    public double constant_density;

    public ConstantPressureLoad(String name, double constant_pressure) {
        super(name);
        state=Double.POSITIVE_INFINITY;
        volume=Double.POSITIVE_INFINITY;
        pressure=constant_pressure;
        this.constant_pressure=constant_pressure;
        constant_density=constant_pressure/R_T_gas;
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
        return constant_density;
    }

    public void set_mass(double new_mass) {

    }

    public void add_mass(double added_mass) {

    }

    public void remove_mass(double removed_mass) {

    }

    public void move_mass(PneumaticLoad move_to,double moved_mass) {
        if (move_to.state+moved_mass<0) {
            logger.error("trying to set air mass to negative when moving!");
            return;
        }
        move_to.state+=moved_mass;
    }

    public void set_volume(double new_volume) {

    }

    public void set_constant_pressure(double constant_pressure) {
        this.constant_pressure=constant_pressure;
        pressure=constant_pressure;
        constant_density=constant_pressure/R_T_gas;
    }

    public void sanitize() {
        speed=sanitize_number(speed,0);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        NBTTagCompound inbt=nbt.getCompoundTag(str);
        state=Double.POSITIVE_INFINITY;
        constant_pressure=inbt.getDouble("constant_pressure");
        pressure=constant_pressure;
        constant_density=constant_pressure*R_T_gas;
        speed=inbt.getDouble("speed");
        resistance=inbt.getDouble("resistance");
        volume=Double.POSITIVE_INFINITY;
        area=inbt.getDouble("area");
        sanitize();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        NBTTagCompound inbt= new NBTTagCompound();
        sanitize();
        inbt.setDouble("constant_pressure",constant_pressure);
        inbt.setDouble("speed",speed);
        inbt.setDouble("resistance",resistance);
        inbt.setDouble("area",area);
        nbt.setTag(str,inbt);
    }
}
