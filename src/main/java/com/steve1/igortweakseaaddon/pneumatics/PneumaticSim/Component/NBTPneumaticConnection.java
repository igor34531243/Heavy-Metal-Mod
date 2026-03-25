package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component;

import mods.eln.misc.INBTTReady;
import net.minecraft.nbt.NBTTagCompound;

public class NBTPneumaticConnection extends PneumaticConnection implements INBTTReady {

    public String nbt_name;

    public NBTPneumaticConnection(String name) {
        super();
        nbt_name=name;
    }

    public NBTPneumaticConnection(String name,PneumaticLoad load1,PneumaticLoad load2) {
        super(load1,load2);
        nbt_name=name;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        NBTTagCompound inbt=nbt.getCompoundTag(str);
        speed=inbt.getDouble("speed");
        sanitize();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        NBTTagCompound inbt= new NBTTagCompound();
        sanitize();
        inbt.setDouble("speed",speed);
        nbt.setTag(str,inbt);
    }
}
