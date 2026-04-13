package com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorSimpleShaft;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeDescriptor;
import mods.eln.misc.BoundingBox;
import mods.eln.misc.Direction;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.EntityMetaTag;
import mods.eln.node.transparent.TransparentNodeElementRender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class IgorSimpleShaftDescriptor extends IgorTransparentNodeDescriptor {

    public Obj3D.Obj3DPart rotating_part;
    public String sound=null;

    public IgorSimpleShaftDescriptor(String name, Obj3D model, Class<? extends IgorSimpleShaftElement> ElementClass, Class<? extends IgorSimpleShaftRender> RenderClass, EntityMetaTag metattag) {
        super(name, model, ElementClass, RenderClass, metattag);
        if (model!=null) {
            rotating_part=get_rotating_part();
        }
    }

    @Override
    public Obj3D.Obj3DPart get_main_part() {
        return get_static_part();
    }

    public Obj3D.Obj3DPart get_static_part() {
        return model.getPart("static");
    }

    public  Obj3D.Obj3DPart get_rotating_part() {
        return model.getPart("rotating");
    }

    @Override
    public void draw_initial(TransparentNodeElementRender got_render) {
        IgorSimpleShaftRender render= (IgorSimpleShaftRender) got_render;
        if (render.front == Direction.XP || render.front == Direction.ZP)
            draw(render.angle);
        else
            draw(-render.angle);
    }

    public void draw(double angle) {
        if (model!=null) {
            GL11.glPushMatrix();

            model_main.draw();
            BoundingBox bb = rotating_part.boundingBox();
            Vec3 centre = bb.centre();
            double ox = centre.xCoord;
            double oy = centre.yCoord;
            double oz = centre.zCoord;
            GL11.glTranslated(ox, oy, oz);
            GL11.glRotatef((float) ((angle * 360) / 2.0 / Math.PI), 0f, 0f, 1f);
            GL11.glTranslated(-ox, -oy, -oz);
            rotating_part.draw();

            GL11.glPopMatrix();
        }
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item,Object... data) {
        if (type == IItemRenderer.ItemRenderType.INVENTORY) {
            super.renderItem(type, item, data);
        } else {
            objItemScale(model);
            GL11.glPushMatrix();

            Direction.ZN.glRotateXnRef();
            GL11.glTranslatef(0f, -1f, 0f);
            GL11.glScalef(0.6f, 0.6f, 0.6f);
            draw(0.0);

            GL11.glPopMatrix();
        }
    }

    @Override
    public boolean handleRenderType(ItemStack add, ItemRenderType itemRenderType) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType itemRenderType, ItemStack add, ItemRendererHelper itemRendererHelper) {
        return itemRenderType != IItemRenderer.ItemRenderType.INVENTORY;
    }
}
