package com.steve1.igortweakseaaddon.LogicPort;

import mods.eln.misc.INBTTReady;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class LogicPortInterface implements INBTTReady {
    LogicPortMaster master;
    HashMap<String, InterfaceConnection> connections = new HashMap<String, InterfaceConnection>();

    public LogicPortInterface(LogicPortMaster master) {
        this.master=master;
    }

    public Boolean safe_to_get(String name) {
        if (!master.is_valid()) {
            return false;
        }
        if (!connections.containsKey(name)) {
            return false;
        }
        return true;
    }

    public double get_port_u(String name) {
        if (!master.is_valid()) {
            logger.error("trying to get value of invalid master");
            return 0;
        }
        if (!connections.containsKey(name)) {
            logger.error("trying to get value of non existent connection");
            return 0;
        }
        InterfaceConnection connection = connections.get(name);
        return connection.get_u();
    }

    public Boolean safe_to_set(String name) {
        if (!master.is_valid()) {
            return false;
        }
        if (!connections.containsKey(name)) {
            return false;
        }
        InterfaceConnection connection = connections.get(name);
        if (!connection.is_input) {
            return false;
        }
        return true;
    }

    public void set_port_u(String name,double new_u) {
        if (!master.is_valid()) {
            logger.error("trying to get value of invalid master ");
            return;
        }
        if (!connections.containsKey(name)) {
            logger.error("trying to get value of non existent connection ");
            return;
        }
        InterfaceConnection connection = connections.get(name);
        if (!connection.is_input) {
            logger.error("trying to set value of input connection ");
            return;
        }
        connection.set_u(new_u);
    }

    public void register_connection(String name,Boolean is_input) {
        if (connections.containsKey(name)) {
            logger.error("trying to register already existing conenction");
            return;
        }
        connections.put(name,new InterfaceConnection(name,is_input));
    }

    public void free_all_connections() {
        for (Map.Entry<String, InterfaceConnection> entry : connections.entrySet()) {
            entry.getValue().remove_all_ports();
        }
    }

    public boolean attach_port(SlavePort port) {
        if (!master.is_valid()) {
            return false;
        }
        if (!port.is_valid()) {
            return false;
        }
        String pname = port.get_name();
        if (!connections.containsKey(pname)) {
            return false;
        }
        InterfaceConnection connection = connections.get(pname);
        return connection.add_slave_port(port);
    }

    public void remove_port(SlavePort port) {
        String pname = port.get_name();
        if (!connections.containsKey(pname)) {
            logger.error("trying to remove port from unregistered connection");
            return;
        }
        InterfaceConnection connection = connections.get(pname);
        connection.remove_slave_port(port);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt, String str) {
        NBTTagCompound outputs = nbt.getCompoundTag(str);
        for (Object key_o : outputs.func_150296_c()) {
            String key=(String) key_o;
            if (!connections.containsKey(key)) {
                continue;
            }
            InterfaceConnection connection = connections.get(key);
            double value=outputs.getDouble(key);
            connection.set_u(value);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt, String str) {
        NBTTagCompound outputs = new NBTTagCompound();
        for (Map.Entry<String, InterfaceConnection> entry : connections.entrySet()) {
            if (!entry.getValue().is_input) {
                outputs.setDouble(entry.getKey(), entry.getValue().stored_u);
            }
        }
        nbt.setTag(str,outputs);
    }

    public class InterfaceConnection {
        String name;
        Boolean is_input;
        ArrayList<SlavePort> ports=new ArrayList<SlavePort>();
        double stored_u=0;
        public InterfaceConnection(String name,Boolean is_input) {
            this.name=name;
            this.is_input=is_input;
        }
        public boolean add_slave_port(SlavePort port) {
            if (port.is_input()!=is_input) {
                return false;
            }
            if (ports.contains(port)) {
                logger.error("trying to add same logic port twice");
                return true;
            }
            if (is_input && ports.size()!=0) {
                logger.error("trying to add more than one input port for same name");
                // i dont want to deal with that, its not needed any time soon
                return false;
            }
            ports.add(port);
            if (!is_input) {
                port.set_signal_level(stored_u);
            }
            return true;
        }
        public void remove_slave_port(SlavePort port) {
            if (port.is_input()!=is_input) {
                return;
            }
            if (!ports.contains(port)) {
                logger.error("trying to remove non existent logic port");
                return;
            }
            port.master_removed();
            ports.remove(port);
        }
        public void remove_all_ports() {
            for (SlavePort port : new ArrayList<SlavePort>(ports)) {
                remove_slave_port(port);
            }
        }
        public double get_u() {
            if (is_input) {
                double res=0;
                for (SlavePort port : ports) {
                    res=Math.max(res,port.get_signal_level());
                }
                return res;
            } else {
                return stored_u;
            }
        }
        public void set_u(double new_u) {
            if (is_input) {
                logger.error("trying to set voltage for input gate");
            } else {
                stored_u=new_u;
                for (SlavePort port : ports) {
                    port.set_signal_level(stored_u);
                }
            }
        }
    }
}
