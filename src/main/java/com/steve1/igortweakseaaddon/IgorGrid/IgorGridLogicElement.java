package com.steve1.igortweakseaaddon.IgorGrid;

import com.steve1.igortweakseaaddon.LogicPort.LogicPortInterface;
import com.steve1.igortweakseaaddon.LogicPort.LogicPortMaster;
import com.steve1.igortweakseaaddon.LogicPort.SlavePort;
import mods.eln.misc.Direction;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class IgorGridLogicElement extends IgorGridElement implements LogicPortMaster {

    public Boolean is_valid_master=true;
    public LogicPortInterface port_interface;

    public IgorGridLogicElement(@NotNull TransparentNode transparentNode, @NotNull TransparentNodeDescriptor descriptor, int connectRange) {
        super(transparentNode, descriptor, connectRange);
        port_interface= new LogicPortInterface(this);
    }

    @Override
    public void onBreakElement() {
        disable_logic();
        super.onBreakElement();
    }

    @Override
    public void unload() {
        disable_logic();
        logger.info("unload has been called for master");
    }

    public void disable_logic() {
        is_valid_master=false;
        port_interface.free_all_connections();
    }

    @Override
    public Boolean add_slave_port(SlavePort slave_port) {
        return port_interface.attach_port(slave_port);
    }

    @Override
    public void remove_slave_port(SlavePort slave_port) {
        port_interface.remove_port(slave_port);
    }

    @Override
    public Boolean is_valid() {
        return is_valid_master;
    }

    @Override
    public void connectJob() {
        super.connectJob();
        logger.info("connect job has been called on master");
        is_valid_master=true;
    }

    @Override
    public void disconnectJob() {
        super.disconnectJob();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        try {
            super.writeToNBT(nbt);
            port_interface.writeToNBT(nbt,"port_interface");
        } catch (Exception e) {;
            e.printStackTrace();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        try {
            super.readFromNBT(nbt);
            port_interface.readFromNBT(nbt,"port_interface");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
