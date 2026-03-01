package com.steve1.igortweakseaaddon.GridFuse;

import mods.eln.generic.GenericItemUsingDamageDescriptor;
import mods.eln.i18n.I18N;
import mods.eln.misc.VoltageLevelColor;

import static mods.eln.i18n.I18N.TR_NAME;

public class GridFuseItem extends GenericItemUsingDamageDescriptor {
    public Boolean is_blown;
    public double voltage_regular;
    public double voltage_maximal;

    public GridFuseItem(String name, double voltage_regular, Boolean is_blown) {
        super(TR_NAME(I18N.Type.NONE,name));
        this.is_blown=is_blown;
        this.voltage_regular=voltage_regular;
        this.voltageLevelColor = VoltageLevelColor.Neutral;
        this.voltage_maximal=voltage_regular*1.3;
    }
}
