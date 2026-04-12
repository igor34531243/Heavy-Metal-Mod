package com.steve1.igortweakseaaddon.misc.PneumaticSim.Component;

import mods.eln.misc.INBTTReady2;
import net.minecraft.nbt.NBTTagCompound;

public class NBTPneumaticConnection extends PneumaticConnection implements INBTTReady2 {

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
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagCompound inbt=nbt.getCompoundTag(nbt_name);
        speed=inbt.getDouble("speed");
        sanitize();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagCompound inbt= new NBTTagCompound();
        sanitize();
        inbt.setDouble("speed",speed);
        nbt.setTag(nbt_name,inbt);
    }
}
