package com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorElementInterface;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.sim.IProcess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.pneumatic_simulator;

public abstract class IgorTransparentNodeElement extends TransparentNodeElement implements IgorElementInterface {

    public ArrayList<NBTPneumaticConnection> pneumaticComponentList = new ArrayList<NBTPneumaticConnection>();
    public ArrayList<NBTPneumaticLoad> pneumaticLoadList = new ArrayList<NBTPneumaticLoad>();
    public ArrayList<IProcess> pneumaticProcessList = new ArrayList<IProcess>();

    public IgorTransparentNodeElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
    }

    @Override
    public abstract PneumaticLoad getPneumaticLoad(Direction direction, LRDU lrdu);

    @Override
    public void connectJob() {
        super.connectJob();

        if (node != null && node.isDestructing()) return;

        for (NBTPneumaticConnection connection : pneumaticComponentList) pneumatic_simulator.addPneumaticComponent(connection);
        for (NBTPneumaticLoad load :pneumaticLoadList) pneumatic_simulator.addPneumaticLoad(load);
        for (IProcess process : pneumaticProcessList) pneumatic_simulator.addPneumaticProcess(process);

    }

    @Override
    public void disconnectJob() {
        super.disconnectJob();

        for (NBTPneumaticConnection connection : pneumaticComponentList) pneumatic_simulator.removePneumaticComponent(connection);
        for (NBTPneumaticLoad load : pneumaticLoadList) pneumatic_simulator.removePneumaticLoad(load);
        for (IProcess process : pneumaticProcessList) pneumatic_simulator.removePneumaticProcess(process);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        for (NBTPneumaticLoad load : pneumaticLoadList) {
            if (load!=null && load.nbt_name!=null) {
                load.writeToNBT(nbt);
            }
        }
        for (NBTPneumaticConnection connection : pneumaticComponentList) {
            if (connection!=null && connection.nbt_name!=null) {
                connection.writeToNBT(nbt);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        for (NBTPneumaticLoad load : pneumaticLoadList) {
            if (load!=null && load.nbt_name!=null) {
                load.readFromNBT(nbt);
            }
        }
        for (NBTPneumaticConnection connection : pneumaticComponentList) {
            if (connection!=null && connection.nbt_name!=null) {
                connection.readFromNBT(nbt);
            }
        }
    }

    @Override
    public void initialize() {
        node.connect();
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        return false;
    };
}
