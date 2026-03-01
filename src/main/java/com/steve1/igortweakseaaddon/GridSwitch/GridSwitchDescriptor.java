package com.steve1.igortweakseaaddon.GridSwitch;

import com.steve1.igortweakseaaddon.IgorGrid.IgorGridDescriptor;
import com.steve1.igortweakseaaddon.IgorGrid.IgorGridRender;
import mods.eln.misc.Direction;
import mods.eln.misc.Obj3D;
import mods.eln.misc.VoltageLevelColor;

import static mods.eln.misc.Direction.XN;

public class GridSwitchDescriptor extends IgorGridDescriptor {

    public GridSwitchDescriptor(String name, Obj3D obj) {
        super(name, obj, GridSwitchElement.class, GridSwitchRender.class);
        add_cable_point(XN.left(),0,0,4,0);
        add_cable_point(XN.left(),1,1,4,0);
        add_cable_point(XN.right(),0,0,4,1);
        add_cable_point(XN.right(),1,1,4,1);
    }

    @Override
    public void draw(IgorGridRender render) {
        super.draw(render);
        if (model != null) {
            if (((GridSwitchRender)render).is_open) {
                // no model for that yet, just basic shape
                //model.draw("switch_open");
            } else {
                //model.draw("switch_closed");
            }
        }
    }

}
