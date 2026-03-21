package com.steve1.igortweakseaaddon.grid.GridSwitch;

import com.steve1.igortweakseaaddon.grid.IgorGrid.IgorGridDescriptor;
import com.steve1.igortweakseaaddon.grid.IgorGrid.IgorGridRender;
import mods.eln.misc.Obj3D;
import java.util.HashMap;

import static mods.eln.misc.Direction.XN;

public class GridSwitchDescriptor extends IgorGridDescriptor {

    public float speed;
    public String[] parts = {"mech1", "mech2", "mech3_1", "mech3_2", "mech4_1", "mech4_2", "mech4_3", "mech4_4"};
    HashMap<String, float[]> OnOff = new HashMap();

    public GridSwitchDescriptor(String name, Obj3D obj) {
        super(name, obj, GridSwitchElement.class, GridSwitchRender.class);
        add_cable_point(XN.left(), 0, 0.25 - 0.5, 5.13 - 0.5, -0.7 - 0.5);
        add_cable_point(XN.left(), 1, 2.73 - 0.5, 5.13 - 0.5, -0.7 - 0.5);
        add_cable_point(XN.right(), 0, 0.25 - 0.5, 5.13 - 0.5, 3.7 - 0.5);
        add_cable_point(XN.right(), 1, 2.73 - 0.5, 5.13 - 0.5, 3.7 - 0.5);

        if (obj != null) {
            for (String part : parts) {
                if (obj.getPart(part) != null) {
                    OnOff.put(part, new float[]{obj.getPart(part).getFloat("A1"), obj.getPart(part).getFloat("A2")});
                }
            }
        }
    }

//    @Override
//    public void draw(IgorGridRender render) {
//        super.draw(render);
//        if (model != null) {
//            model.draw("static")
//            if (((GridSwitchRender)render).is_open) {
//                // no model for that yet, just basic shape
//            } else {
//            }
//        }
//    }
//
}
