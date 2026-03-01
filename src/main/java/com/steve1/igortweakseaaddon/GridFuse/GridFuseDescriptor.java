package com.steve1.igortweakseaaddon.GridFuse;

import com.steve1.igortweakseaaddon.IgorGrid.IgorGridDescriptor;
import com.steve1.igortweakseaaddon.IgorGrid.IgorGridRender;
import mods.eln.item.ElectricalFuseDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.misc.VoltageLevelColor;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.fuseBlown;
import static mods.eln.misc.Direction.XN;

public class GridFuseDescriptor extends IgorGridDescriptor {

    public GridFuseDescriptor(String name, Obj3D obj_model) {
        super(name, obj_model, GridFuseElement.class, GridFuseRender.class);
        add_cable_point(XN,0,0.5,2.8,-1.58);
        add_cable_point(XN,1,0.5,2.8,1.58);
        add_cable_point(XN.getInverse(),0,2.4,4.8,-1.58);
        add_cable_point(XN.getInverse(),1,2.4,4.8,1.58);
    }

    @Override
    public void draw(IgorGridRender render) {
        super.draw(render);
        GridFuseItem fuse_descriptor=((GridFuseRender)render).installedFuseClient;
        if (model!=null) {
            if (fuse_descriptor != null) {
                if (fuse_descriptor == fuseBlown) {
                    model.draw("fuse_open");
                } else {
                    model.draw("fuse_closed");
                }
            }
        }
    }
}
