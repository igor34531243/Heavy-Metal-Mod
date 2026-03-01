package com.steve1.igortweakseaaddon.GridSensor;

import mods.eln.misc.BasicContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import java.lang.reflect.Array;

public class GridSensorContainer extends BasicContainer {

    public GridSensorContainer(EntityPlayer player) {
        super(player, null, new Slot[0]);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }
}
