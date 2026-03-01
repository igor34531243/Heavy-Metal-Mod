package com.steve1.igortweakseaaddon.GridSensor;

import com.steve1.igortweakseaaddon.IgorGrid.IgorGridDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.Obj3D;
import mods.eln.misc.VoltageLevelColor;

import static mods.eln.misc.Direction.XN;

public class GridSensorDescriptor extends IgorGridDescriptor {

    public GridSensorDescriptor(String name, Obj3D model) {
        super(name, model, GridSensorElement.class, GridSensorRender.class);
        add_cable_point(XN.left(),0,0,4,0);
        add_cable_point(XN.left(),1,1,4,0);
        add_cable_point(XN.right(),0,0,4,1);
        add_cable_point(XN.right(),1,1,4,1);
    }
}
