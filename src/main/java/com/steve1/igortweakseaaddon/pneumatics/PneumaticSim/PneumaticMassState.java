package com.steve1.igortweakseaaddon.pneumatics.PneumaticSim;

import mods.eln.sim.mna.state.State;

public class PneumaticMassState extends State {

    public double get_mass() {
        return state;
    }

    public void set_mass(double new_mass) {
        state=new_mass;
    }

}
