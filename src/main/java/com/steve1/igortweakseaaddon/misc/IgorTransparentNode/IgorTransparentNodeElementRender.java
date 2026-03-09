package com.steve1.igortweakseaaddon.misc.IgorTransparentNode;

import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;
import mods.eln.node.transparent.TransparentNodeEntity;
import org.lwjgl.opengl.GL11;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class IgorTransparentNodeElementRender extends TransparentNodeElementRender {

    public IgorTransparentNodeDescriptor stored_descriptor;

    public IgorTransparentNodeElementRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        stored_descriptor=(IgorTransparentNodeDescriptor)descriptor;
    }

    @Override
    public void draw() {
        logger.info("trying to draw model");
        GL11.glPushMatrix();
        front.glRotateXnRef();
        GL11.glTranslated(-0.5, -0.5, -0.5);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        stored_descriptor.draw_initial(this);
        GL11.glPopMatrix();
    }
}
