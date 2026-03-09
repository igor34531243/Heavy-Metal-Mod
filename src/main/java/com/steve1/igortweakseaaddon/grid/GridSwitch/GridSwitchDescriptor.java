package com.steve1.igortweakseaaddon.grid.GridSwitch;

import com.steve1.igortweakseaaddon.grid.IgorGrid.IgorGridDescriptor;
import com.steve1.igortweakseaaddon.grid.IgorGrid.IgorGridRender;
import mods.eln.misc.Obj3D;

import static mods.eln.misc.Direction.XN;

public class GridSwitchDescriptor extends IgorGridDescriptor {

    public GridSwitchDescriptor(String name, Obj3D obj) {
        super(name, obj, GridSwitchElement.class, GridSwitchRender.class);
        add_cable_point(XN.left(),0,0.25-0.5,5.13-0.5,-0.7-0.5);
        add_cable_point(XN.left(),1,2.73-0.5,5.13-0.5,-0.7-0.5);
        add_cable_point(XN.right(),0,0.25-0.5,5.13-0.5,3.7-0.5);
        add_cable_point(XN.right(),1,2.73-0.5,5.13-0.5,3.7-0.5);
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
