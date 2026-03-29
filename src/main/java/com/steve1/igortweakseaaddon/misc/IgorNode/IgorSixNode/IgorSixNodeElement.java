package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorElementInterface;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNodeElementInterface;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElement;
import mods.eln.sim.IProcess;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.pneumatic_simulator;

public abstract class IgorSixNodeElement extends SixNodeElement implements IgorSixNodeElementInterface {

    public ArrayList<NBTPneumaticConnection> pneumaticComponentList = new ArrayList<NBTPneumaticConnection>();
    public ArrayList<NBTPneumaticLoad> pneumaticLoadList = new ArrayList<NBTPneumaticLoad>();
    public ArrayList<IProcess> pneumaticProcessList = new ArrayList<IProcess>();

    public IgorSixNodeElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);
    }

    @Override
    public void connectJob() {
        super.connectJob();

        if (sixNode != null && sixNode.isDestructing()) return;

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

    }
}