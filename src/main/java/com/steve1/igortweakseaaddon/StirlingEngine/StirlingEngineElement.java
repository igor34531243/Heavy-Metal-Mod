package com.steve1.igortweakseaaddon.StirlingEngine;

import mods.eln.mechanical.SimpleShaftElement;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.NodeBase;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.nbt.NbtThermalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class StirlingEngineElement extends SimpleShaftElement {

    NbtThermalLoad thermal_load_1= new NbtThermalLoad("thermal_load_1");
    NbtThermalLoad thermal_load_2= new NbtThermalLoad("thermal_load_2");
    IProcess stirlingProcess;

    public StirlingEngineElement(TransparentNode node, TransparentNodeDescriptor desc_) {
        super(node, desc_);

        stirlingProcess=new StirlingEngineProcess(this);

        thermalFastProcessList.add(stirlingProcess);

        thermal_load_1.set(0.005,100,20);
        thermal_load_2.set(0.005,100,20);

        thermalLoadList.add(thermal_load_1);
        thermalLoadList.add(thermal_load_2);
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction direction, LRDU lrdu) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction direction, LRDU lrdu) {
        if (direction==front) {
            return thermal_load_1;
        } else if (direction==front.getInverse()) {
            return thermal_load_2;
        }
        return null;
    }

    @Override
    public int getConnectionMask(Direction direction, LRDU lrdu) {
        if (direction==front || direction==front.getInverse()) {
            return NodeBase.maskThermal;
        }
        return 0;
    }

    @Override
    public String thermoMeterString(Direction direction) {
        return "";
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction direction, float v, float v1, float v2) {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        thermal_load_1.writeToNBT(nbt,"thermal_load_1");
        thermal_load_2.writeToNBT(nbt,"thermal_load_2");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        thermal_load_1.readFromNBT(nbt,"thermal_load_1");
        thermal_load_2.readFromNBT(nbt,"thermal_load_2");
    }
}
