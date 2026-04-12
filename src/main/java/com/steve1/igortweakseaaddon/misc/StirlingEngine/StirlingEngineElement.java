package com.steve1.igortweakseaaddon.misc.StirlingEngine;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorSimpleShaft.IgorSimpleShaftElement;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.NodeBase;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.nbt.NbtThermalLoad;

public class StirlingEngineElement extends IgorSimpleShaftElement {

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
    public PneumaticLoad getPneumaticLoad(Direction direction, LRDU lrdu) {
        return null;
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
            if (lrdu==LRDU.Down) {
                return NodeBase.maskThermal;
            }
        }
        return 0;
    }
}
