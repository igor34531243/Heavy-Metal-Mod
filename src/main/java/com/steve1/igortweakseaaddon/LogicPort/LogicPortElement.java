package com.steve1.igortweakseaaddon.LogicPort;

import mods.eln.Eln;
import mods.eln.ghost.GhostElement;
import mods.eln.ghost.GhostObserver;
import mods.eln.misc.Coordonate;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.NodeBase;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.nbt.NbtElectricalGateInput;
import mods.eln.sim.nbt.NbtElectricalGateOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.io.DataOutputStream;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class LogicPortElement extends TransparentNodeElement implements SlavePort{

    public Coordonate coordonate;
    public boolean can_break_safely=true;
    public LogicPortMaster master;
    public LogicPortDescriptor desc;
    public ElectricalLoad gate;
    public boolean is_input;
    public String logic_port_name;
    public IProcess master_search_process;
    Boolean valid=false;
    Boolean initialized=false;
    Boolean process_finished=false;
    Boolean in_thread=false;

    public LogicPortElement(TransparentNode node, TransparentNodeDescriptor descriptor) {
        super(node, descriptor);
        coordonate=node.coordonate;
        desc= (LogicPortDescriptor) descriptor;
        if (is_input) {
            gate = new NbtElectricalGateInput("control");
        } else {
            gate = new NbtElectricalGateOutput("control");
        }
        electricalLoadList.add(gate);
    }

    @Override
    public void set_logic_values(String port_name, Boolean is_input) {
        this.logic_port_name=port_name;
        this.is_input=is_input;
        this.initialized=true;
        this.valid = true;
        master_attached();
    }

    public Boolean is_input() {
        return is_input;
    }

    public Coordonate get_coordonate() {
        return coordonate;
    }

    public String get_name() {
        return logic_port_name;
    }

    @Override
    public void onBreakElement() {
        if (can_break_safely) {
            can_break_safely=false;
            disable_logic();
            GhostElement element = Eln.ghostManager.getGhost(coordonate);
            if (element != null) {
                element.breakBlock();
            }
        }
        super.onBreakElement();
    }

    @Override
    public ItemStack getDropItemStack() {
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("port_name") && nbt.hasKey("port_is_input")) {
            set_logic_values(nbt.getString("port_name"),nbt.getBoolean("port_is_input"));
        }
        if (nbt.hasKey("current_signal")) {
            gate.setU(nbt.getDouble("current_signal"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setString("port_name",logic_port_name);
        nbt.setBoolean("port_is_input",is_input);
        nbt.setDouble("current_signal",gate.getU());
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        if (gate==null) {
            logger.error("missing control for electrical load at logic port");
            return null;
        }
        return gate;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        if (is_input) {
            return NodeBase.maskElectricalInputGate;
        } else {
            return NodeBase.maskElectricalOutputGate;
        }
    }

    @Override
    public String multiMeterString(Direction side) {
        if (!master_attached()) return " no master found ";
        return master.multiMeterString(side);
    }

    @Override
    public String thermoMeterString(Direction side) {
        if (!master_attached()) return " no master found ";
        return master.thermoMeterString(side);
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
    }

    @Override
    public void initialize() {
        connect();
        master_attached();
    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction side, float vx, float vy, float vz) {
        if (!master_attached()) {
            return false;
        }
        return master.onBlockActivated(entityPlayer,side,vx,vy,vz);
    }

    @Override
    public void disconnectJob() {
        super.disconnectJob();
    }

    @Override
    public void unload() {
        disable_logic();
    }

    public void disable_logic() {
        valid=false;
        remove_search_process();
        if (master != null) {
            master.remove_slave_port(this);
            master=null;
        }
    }

    @Override
    public void connectJob() {
        check_search_process();
        super.connectJob();
        if (initialized) {
            valid = true;
            master_attached();
        }
    }

    @Override
    public Boolean is_valid() {
        check_search_process();
        return valid;
    }

    @Override
    public void master_removed() {
        check_search_process();
        master=null;
        master_attached();
    }

    public boolean master_attached() {
        check_search_process();
        if (master!=null) return true;
        if (!valid) return false;
        Boolean got_master = try_attach_master();
        if (got_master) {
            remove_search_process();
            return true;
        } else if (master_search_process==null) {
            process_finished=false;
            master_search_process= new IProcess() {
                @Override
                public void process(double time) {
                    in_thread=true;
                    if (!process_finished) {
                        Boolean res = try_attach_master();
                        if (res || !valid) {
                            process_finished = true;
                        }
                    }
                    in_thread=false;
                }
            };
            slowProcessList.add(master_search_process);
        }
        return false;
    }

    public void check_search_process() {
        if (process_finished && !in_thread) {
            remove_search_process();
        }
    }

    public void remove_search_process() {
        if (master_search_process!=null && !in_thread) {
            Boolean sucsess=true;
            while (sucsess) {
                sucsess=slowProcessList.remove(master_search_process);
            }
            master_search_process=null;
            process_finished=false;
        }
    }

    public boolean try_attach_master() {
        check_search_process();
        GhostElement element = Eln.ghostManager.getGhost(coordonate);
        if (element != null) {
            Coordonate observer_cord=element.getObservatorCoordonate();
            GhostObserver observer= Eln.ghostManager.getObserver(observer_cord);
            if (observer instanceof LogicPortMaster) {
                master = (LogicPortMaster) observer;
                if (!master.is_valid()) {
                    logger.info("master for logic port is invalid!");
                    return false;
                }
                Boolean attached= master.add_slave_port(this);
                if (attached) {
                    logger.info("master for logic port has been found!");
                    return true;
                } else {
                    logger.info("failed to attach logic port to master");
                }
            } else {
                logger.info("incorrect observer for logic port");
            }
        } else {
            logger.info("cant find element for logic port: "+coordonate.toString()+" "+logic_port_name);
        }
        return false;
    }

    @Override
    public double get_signal_level() {
        check_search_process();
        return gate.getU();
    }

    @Override
    public void set_signal_level(double value) {
        check_search_process();
        if (is_input) {
            logger.error("trying to set signal at input gate");
            return;
        }
        gate.setU(value);
    }
}
