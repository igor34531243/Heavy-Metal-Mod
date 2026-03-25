package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import mods.eln.misc.INBTTReady;
import net.minecraft.nbt.NBTTagCompound;

public class NBTPneumaticLoad extends PneumaticLoad implements INBTTReady {

    public String nbt_name;

    public NBTPneumaticLoad(String name) {
        super();
        nbt_name=name;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        NBTTagCompound inbt=nbt.getCompoundTag(str);
        state=inbt.getDouble("mass");
        pressure=inbt.getDouble("pressure");
        speed=inbt.getDouble("speed");
        sanitize();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        NBTTagCompound inbt= new NBTTagCompound();
        sanitize();
        inbt.setDouble("mass",state);
        inbt.setDouble("pressure",pressure);
        inbt.setDouble("speed",speed);
        nbt.setTag(str,inbt);
    }
}
