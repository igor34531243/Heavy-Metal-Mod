package com.steve1.igortweakseaaddon.LogicPort;

import mods.eln.misc.Coordonate;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.sim.nbt.NbtElectricalGateInput;
import net.minecraft.entity.player.EntityPlayer;

public interface LogicPortMaster {
    public String multiMeterString(Direction side) ;
    public String thermoMeterString(Direction side);
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz);
    public Boolean add_slave_port(SlavePort slave_port);
    public void remove_slave_port(SlavePort slave_port);
    public Boolean is_valid();
}
