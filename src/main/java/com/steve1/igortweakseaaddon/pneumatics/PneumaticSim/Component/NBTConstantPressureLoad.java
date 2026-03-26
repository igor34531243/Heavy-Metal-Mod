package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import net.minecraft.nbt.NBTTagCompound;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.PneumaticSimulator.R_T_gas;

public class NBTConstantPressureLoad extends NBTPneumaticLoad{

    public double constant_pressure;

    public NBTConstantPressureLoad(String name, double constant_pressure) {
        super(name);
        state=Double.POSITIVE_INFINITY;
        volume=Double.POSITIVE_INFINITY;
        pressure=constant_pressure;
        this.constant_pressure=constant_pressure;
    }

    @Override
    public void add_next(double added_mass) {
        // do nothing obviously, we dont want it changing

    }

    @Override
    public void check_mass_step() {
        // do almost nothing obviously, we dont want it changing

        next_mass_coefficient=1;
    }

    @Override
    public void step_fin() {
        // do nothing obviously, we dont want it changing

        next_mass_coefficient=1;
        next_mass=state;
    }

    @Override
    public void activate_connections() {

    }

    @Override
    public void check_for_sleeping_connections() {

    }

    @Override
    public void set_mass(double new_mass) {

    }

    @Override
    public void add_mass(double added_mass) {

    }

    @Override
    public void remove_mass(double removed_mass) {

    }

    @Override
    public void move_mass(PneumaticLoad move_to,double moved_mass) {
        if (move_to.state+moved_mass<0) {
            logger.error("trying to set air mass to negative when moving!");
            return;
        }
        move_to.state+=moved_mass;
    }

    @Override
    public void set_volume(double new_volume) {

    }

    @Override
    public void update_cache() {
        pressure=constant_pressure;
        density=constant_pressure/R_T_gas;
    }

    public void set_constant_pressure(double constant_pressure) {
        this.constant_pressure=constant_pressure;
        pressure=constant_pressure;
        update_cache();
    }

    @Override
    public void sanitize() {

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagCompound inbt=nbt.getCompoundTag(nbt_name);
        constant_pressure=inbt.getDouble("constant_pressure");
        update_cache();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagCompound inbt= new NBTTagCompound();
        inbt.setDouble("constant_pressure",constant_pressure);
        nbt.setTag(nbt_name,inbt);
    }
}
