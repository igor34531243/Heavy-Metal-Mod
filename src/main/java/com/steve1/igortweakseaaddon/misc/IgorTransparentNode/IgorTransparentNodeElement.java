package com.steve1.igortweakseaaddon.misc.IgorTransparentNode;

import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.sim.IProcess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.igorTransparentNodeBlock;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.pneumatic_simulator;

public abstract class IgorTransparentNodeElement extends TransparentNodeElement {

    public ArrayList<PneumaticConnection> pneumaticComponentList = new ArrayList<PneumaticConnection>();
    public ArrayList<PneumaticLoad> pneumaticLoadList = new ArrayList<PneumaticLoad>();
    public ArrayList<IProcess> pneumaticProcessList = new ArrayList<IProcess>();

    public IgorTransparentNodeElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
    }

    public ItemStack getDropItemStack() {
        ItemStack itemStack = new ItemStack(igorTransparentNodeBlock, 1, node.elementId);
        itemStack.setTagCompound(getItemStackNBT());
        return itemStack;
    }

    public abstract PneumaticLoad getPneumaticLoad(Direction direction, LRDU lrdu);

    @Override
    public void connectJob() {
        super.connectJob();

        if (node != null && node.isDestructing()) return;

        pneumatic_simulator.addAllPneumaticComponent(pneumaticComponentList);
        for (PneumaticLoad load :pneumaticLoadList) pneumatic_simulator.addPneumaticLoad(load);
        pneumatic_simulator.addAllPneumaticProcess(pneumaticProcessList);

    }

    public void disconnectJob() {
        super.disconnectJob();

        pneumatic_simulator.removeAllPneumaticComponent(pneumaticComponentList);
        for (PneumaticLoad load : pneumaticLoadList) pneumatic_simulator.removePneumaticLoad(load);
        pneumatic_simulator.removeAllPneumaticProcess(pneumaticProcessList);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        for (PneumaticLoad load : pneumaticLoadList) {
            if (load!=null && load.nbt_name!=null) {
                load.writeToNBT(nbt, load.nbt_name);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        for (PneumaticLoad load : pneumaticLoadList) {
            if (load!=null && load.nbt_name!=null) {
                load.readFromNBT(nbt, load.nbt_name);
            }
        }
    }

    @Override
    public void initialize() {
        node.connect();
    }

    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        return false;
    };
}
