package com.steve1.igortweakseaaddon.grid.GridFuse;

import com.steve1.igortweakseaaddon.misc.IgorGrid.IgorGridDescriptor;
import com.steve1.igortweakseaaddon.misc.IgorGrid.IgorGridRender;
import mods.eln.misc.Obj3D;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.fuseBlown;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static mods.eln.i18n.I18N.tr;
import static mods.eln.misc.Direction.XN;

public class GridFuseDescriptor extends IgorGridDescriptor {

    public GridFuseDescriptor(String name, Obj3D obj_model) {
        super(name, obj_model, GridFuseElement.class, GridFuseRender.class);
        add_cable_point(XN,0,0.5,2.8,-1.58);
        add_cable_point(XN,1,0.5,2.8,1.58);
        add_cable_point(XN.getInverse(),0,2.4,4.8,-1.58);
        add_cable_point(XN.getInverse(),1,2.4,4.8,1.58);

        if (obj_model == null) {
            logger.error("No grid breaker model given!");
        }
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        Collections.addAll(list, tr("A grid breaker is an electrical device used to automatically\nsever electrical connections upon exceeding the\nrated cable current or voltage.").split("\n"));
        Collections.addAll(list, tr("The grid breaker accepts only T1 and T2 grid fuses.\n").split("\n"));
    }
    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type != ItemRenderType.INVENTORY){
            if (model != null) {
                model.draw("main");
            }
        }else{
            super.renderItem(type, item, data);
        }
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
