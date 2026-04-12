package com.steve1.igortweakseaaddon.pneumatics.PneumaticTurbine;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorSimpleShaft.IgorSimpleShaftElement;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.PneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import mods.eln.misc.BasicContainer;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNode;import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementInventory;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.get_stack_in_slot;
import static com.steve1.igortweakseaaddon.pneumatics.PneumaticTurbine.PneumaticTurbineDescriptor.pipeIdTurbine;

public class PneumaticTurbineElement extends IgorSimpleShaftElement {
    public PneumaticTurbineDescriptor descriptor;
    public IInventory inventory=new TransparentNodeElementInventory(1,64,this);

    public double air_to_rot_cof;
    public double rot_to_air_cof;
    public PneumaticPipeDescriptor pipe_descriptor;
    public boolean has_item=false;

    public NBTPneumaticLoad loadA = new NBTPneumaticLoad("loadA");
    public NBTPneumaticLoad loadB = new NBTPneumaticLoad("loadB");
    public NBTPneumaticConnection pConnection = new NBTPneumaticConnection("pConnection");

    public PneumaticTurbineElement(TransparentNode transparentNode, TransparentNodeDescriptor got_descriptor) {
        super(transparentNode, got_descriptor);
        descriptor=(PneumaticTurbineDescriptor)got_descriptor;
        pipe_descriptor=t1PneumaticPipeDescriptor;

        air_to_rot_cof=descriptor.air_to_rot_cof;
        rot_to_air_cof=descriptor.rot_to_air_cof;

        pipe_descriptor_changed();

        pConnection.connect(loadA,loadB);

        pneumaticLoadList.add(loadA);
        pneumaticLoadList.add(loadB);
        pneumaticComponentList.add(pConnection);
    }

    @Override
    public PneumaticLoad getPneumaticLoad(Direction direction, LRDU lrdu) {
        if (has_item && direction==front) {
            return loadA;
        }
        if (has_item && direction==front.getInverse()) {
            return loadB;
        }
        return null;
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        if (has_item && (side==front || side==front.getInverse())) {
            return pneumaticMask;
        }
        return 0;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public BasicContainer newContainer(Direction side, EntityPlayer player) {
        return descriptor.make_container(player,inventory);
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            Utils.serialiseItemStack(stream,get_stack_in_slot(inventory,pipeIdTurbine));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        update_item();
    }

    public void pipe_descriptor_changed() {
        pipe_descriptor.apply_to_reset(loadA);
        pipe_descriptor.apply_to_reset(loadB);
        pipe_descriptor.apply_to(pConnection);
    }

    public void on_item_swapped() {
        reset_all_loads();
        reset_all_connections();
    }

    public void update_item_descriptor() {
        ItemStack pipeStack = get_stack_in_slot(inventory, pipeIdTurbine);
        PneumaticPipeDescriptor pipe_desc = (PneumaticPipeDescriptor) PneumaticPipeDescriptor.getDescriptor(pipeStack, PneumaticPipeDescriptor.class);
        if (pipe_desc == null) {
            pipe_descriptor = t1PneumaticPipeDescriptor;
            has_item=false;
        } else {
            pipe_descriptor = pipe_desc;
            has_item=true;
        }
        pipe_descriptor_changed();
    }

    public void update_item() {
        update_item_descriptor();
        reconnect();
        needPublish();
    }

    public void update_item_swapped() {
        update_item_descriptor();
        on_item_swapped();
        reconnect();
        needPublish();
    }

    @Override
    public void initialize() {
        super.initialize();
        update_item();
    }

    @Override
    public void inventoryChange(IInventory inventory) {
        super.inventoryChange(inventory);
        if (inventory==this.inventory) {
            update_item_swapped();
        }
    }
}
