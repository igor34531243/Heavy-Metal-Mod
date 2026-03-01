package com.steve1.igortweakseaaddon.GridSwitch;

import com.steve1.igortweakseaaddon.IgorGrid.IgorGridElement;
import com.steve1.igortweakseaaddon.IgorGrid.IgorGridLogicElement;
import com.steve1.igortweakseaaddon.LogicPort.LogicPortMaster;
import com.steve1.igortweakseaaddon.LogicPort.SlavePort;
import mods.eln.Eln;
import mods.eln.gridnode.GridDescriptor;
import mods.eln.gridnode.GridElement;
import mods.eln.misc.Coordonate;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBase;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.mna.component.Resistor;
import mods.eln.sim.nbt.NbtElectricalGateInput;
import mods.eln.sim.nbt.NbtElectricalLoad;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static mods.eln.misc.Direction.XN;

public class GridSwitchElement extends IgorGridLogicElement {

    public GridSwitchDescriptor desc;
    public NbtElectricalLoad loadA = new NbtElectricalLoad("loadA");
    public NbtElectricalLoad loadB = new NbtElectricalLoad("loadB");
    public Resistor switchResistor = new Resistor(loadA, loadB);
    public boolean is_open = false;
    public boolean previously_open=false;

    World world;

    public double x = node.coordonate.x;
    public double y = node.coordonate.y;
    public double z = node.coordonate.z;

    public GridSwitchElement(@NotNull TransparentNode transparentNode, @NotNull TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor, 32);
        desc = (GridSwitchDescriptor)descriptor;

        attach_grid_load(XN.left(),loadA);
        attach_grid_load(XN.right(),loadB);
        port_interface.register_connection("input1",true);

        electricalLoadList.add(loadA);
        electricalLoadList.add(loadB);
        electricalComponentList.add(switchResistor);

        Eln.instance.veryHighVoltageCableDescriptor.applyTo(loadA);
        Eln.instance.veryHighVoltageCableDescriptor.applyTo(loadB);

        electricalComponentList.add(new Resistor(loadA, null).pullDown());
        electricalComponentList.add(new Resistor(loadB, null).pullDown());

        refreshSwitchResistor();

        IProcess physicsProcess = new IProcess() {
            @Override
            public void process(double timeStep) {
                if (port_interface.safe_to_get("input1")) {
                    double control_voltage = port_interface.get_port_u("input1");
                    if (control_voltage > 35) {
                        set_open(false);
                    } else if (control_voltage < 15) {
                        set_open(true);
                    }
                }
            }
        };

        slowProcessList.add(physicsProcess);
    }

    @Override
    public String multiMeterString(Direction side) {
        String str = Utils.plotVolt("  U in: ", loadA.getU()) +
                Utils.plotVolt("  U out:", loadB.getU());

        str +=Utils.plotAmpere("I: ", Math.abs(switchResistor.getCurrent()));

        return str;
    }

    @Override
    public String thermoMeterString(Direction side) {
        return "";  // heating might or might not be added later, im too lazy for now
    }

    public void set_open(boolean new_is_open){
        set_open(new_is_open,false);
    }

    public void set_open(boolean new_is_open, boolean force_refresh){
        is_open=new_is_open;
        refreshSwitchResistor(force_refresh);
    }

    public void refreshSwitchResistor() {
        refreshSwitchResistor(false);
    }

    public void refreshSwitchResistor(boolean force_refresh) {
        if (previously_open != is_open || force_refresh) {
            previously_open=is_open;
            if (is_open) {
                switchResistor.setR(Double.POSITIVE_INFINITY);
            } else {
                cable.applyTo(switchResistor);
            }
            needPublish();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        try {
            super.readFromNBT(nbt);

            if (nbt.hasKey("is_open")) {
                set_open(nbt.getBoolean("is_open"),true);
            } else {
                set_open(false,true);
            }

            refreshSwitchResistor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        try {
            super.writeToNBT(nbt);

            nbt.setBoolean("is_open", is_open);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeBoolean(is_open);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}