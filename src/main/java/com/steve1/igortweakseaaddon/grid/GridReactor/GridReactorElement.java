package com.steve1.igortweakseaaddon.grid.GridReactor;

import com.steve1.igortweakseaaddon.grid.IgorGrid.IgorGridElement;
import mods.eln.misc.Direction;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementInventory;
import mods.eln.sim.IProcess;
import mods.eln.sim.mna.component.Inductor;
import mods.eln.sim.nbt.NbtElectricalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static mods.eln.misc.Direction.XN;


public class GridReactorElement extends IgorGridElement {

    GridReactorDescriptor descriptor;
    NbtElectricalLoad loadA = new NbtElectricalLoad("loadA");
    NbtElectricalLoad loadB = new NbtElectricalLoad("loadB");

    Inductor inductor = new Inductor("inductor", loadA, loadB);
    public double current;
    public double last_sent=0;

    public GridReactorElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor, 32);

        this.descriptor = (GridReactorDescriptor) descriptor;

        attach_grid_load(XN.left(),loadA);
        attach_grid_load(XN.right(),loadB);

        cable.applyTo(loadA);
        cable.applyTo(loadB);

        IProcess physicsProcess = new IProcess() {
            @Override
            public void process(double timeStep) {
                current = inductor.getCurrent();
                double delta_current = Math.abs(last_sent - current);
                if (delta_current > 0.05 && current > 0.05 || delta_current > 0.5){
                    last_sent = inductor.getCurrent();
                    needPublish();
                }
            }
        };
        electricalProcessList.add(physicsProcess);

        electricalLoadList.add(loadA);
        electricalLoadList.add(loadB);
        electricalComponentList.add(inductor);
        loadA.setAsMustBeFarFromInterSystem();
    }
    TransparentNodeElementInventory inventory = new TransparentNodeElementInventory(2, 64, this);

    @Override
    public String multiMeterString(Direction side) {
        String str = Utils.plotVolt("  U: ", Math.abs(loadA.getU()-loadB.getU()));
        str += Utils.plotAmpere("I: ", Math.abs(inductor.getCurrent()));
        str += Utils.plotPower(Math.abs(inductor.getCurrent())*Math.abs(loadA.getU()-loadB.getU()));
        return str;
    }

    @Override
    public void initialize() {
        super.initialize();
        setupPhysical();
    }

    @Override
    public String thermoMeterString(Direction side) {
        return null;
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try{
            stream.writeDouble(current);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void inventoryChange(IInventory inventory) {
        super.inventoryChange(inventory);
        setupPhysical();
    }


    boolean fromNbt = false;

    public void setupPhysical() {
        double rs = descriptor.getRsValue(inventory);
        inductor.setL(descriptor.getlValue(inventory));
        loadA.setRs(rs);
        loadB.setRs(rs);

        if (fromNbt) {
            fromNbt = false;
        } else {
            inductor.resetStates();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        fromNbt = true;
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public Container newContainer(Direction side, EntityPlayer player) {
        return new GridReactorContainer(player, inventory);
    }

}
