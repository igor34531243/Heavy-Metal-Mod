package com.steve1.igortweakseaaddon.IgorGrid;

import mods.eln.Eln;
import mods.eln.gridnode.GridDescriptor;
import mods.eln.gridnode.GridElement;
import mods.eln.gridnode.GridRender;
import mods.eln.misc.Direction;
import mods.eln.misc.Obj3D;
import mods.eln.misc.UtilsClient;
import mods.eln.misc.VoltageLevelColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IItemRenderer;

import java.util.ArrayList;
import java.util.HashMap;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.testcube;

public class IgorGridDescriptor extends GridDescriptor {
    public Obj3D model;
    public HashMap<Direction,Vec3[]> cable_points = new HashMap<Direction,Vec3[]>();

    public IgorGridDescriptor(String name, Obj3D obj, Class<? extends GridElement> element_class, Class<? extends GridRender> render_class) {
        super(name, testcube, element_class, render_class, "textures/wire.png", Eln.instance.highVoltageCableDescriptor, 32);
        model=obj;
        if (model==null) {
            logger.info("model is not being loaded properly!");
        }
        voltageLevelColor = VoltageLevelColor.HighGrid;
        this.setStatic_parts(new ArrayList<>());
        this.setRotating_parts(new ArrayList<>());
    }

    public void add_cable_point(Direction side, int i, double x, double y, double z) {
        if (!cable_points.containsKey(side)) {
            cable_points.put(side, new Vec3[] {
                    Vec3.createVectorHelper(0, 0, 0),
                    Vec3.createVectorHelper(0, 0, 0)
            });
        }
        if (i>=2 || i<0) {
            logger.error("trying to add cable point to improper id");
            return;
        }
        Vec3[] sidearr= cable_points.get(side);
        sidearr[i]=Vec3.createVectorHelper(x,y,z);
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        if (getIcon() == null)
            return;

        voltageLevelColor.drawIconBackground(type);

        String icon = getIcon().getIconName().substring(4);
        UtilsClient.drawIcon(type, new ResourceLocation("eln", "textures/blocks/" + icon + ".png"));
    }

    public void draw(IgorGridRender render) {
        if (model!=null) {
            model.draw("main");
        }
    };

    @Override
    public boolean hasCustomIcon() {
        return true;
    }

    @Override
    public boolean rotationIsFixed() {
        return true;
    }
}
