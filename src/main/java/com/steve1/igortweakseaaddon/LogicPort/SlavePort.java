package com.steve1.igortweakseaaddon.LogicPort;

import mods.eln.misc.Coordonate;

public interface SlavePort {
    public double get_signal_level();
    public void set_signal_level(double value);
    public Boolean is_input();
    public Coordonate get_coordonate();
    public String get_name();
    public void master_removed();
    public Boolean is_valid();
    public void set_logic_values(String port_name,Boolean is_input);
}
