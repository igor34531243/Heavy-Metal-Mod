package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import mods.eln.misc.INBTTReady;
import mods.eln.misc.INBTTReady2;
import net.minecraft.nbt.NBTTagCompound;

public class NBTPneumaticLoad extends PneumaticLoad implements INBTTReady2 {

    public String nbt_name;

    public NBTPneumaticLoad(String name) {
        super();
        nbt_name=name;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagCompound inbt=nbt.getCompoundTag(nbt_name);
        state=inbt.getDouble("mass");
        sanitize();
        update_cache();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagCompound inbt= new NBTTagCompound();
        sanitize();
        inbt.setDouble("mass",state);
        nbt.setTag(nbt_name,inbt);
    }
}
