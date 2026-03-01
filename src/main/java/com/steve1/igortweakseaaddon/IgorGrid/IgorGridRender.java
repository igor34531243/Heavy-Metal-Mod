package com.steve1.igortweakseaaddon.IgorGrid;

import mods.eln.gridnode.GridElement;
import mods.eln.gridnode.GridRender;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class IgorGridRender extends GridRender {
    IgorGridDescriptor stored_descriptor;
    public IgorGridRender(@NotNull TransparentNodeEntity tileEntity, @NotNull TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        stored_descriptor= (IgorGridDescriptor) descriptor;
    }

    @Override
    public void draw() {
        super.draw();

        GL11.glPushMatrix();
        front.glRotateXnRef();
        GL11.glTranslated(-0.5, -0.5, -0.5);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        stored_descriptor.draw(this);
        GL11.glPopMatrix();
    }
}
