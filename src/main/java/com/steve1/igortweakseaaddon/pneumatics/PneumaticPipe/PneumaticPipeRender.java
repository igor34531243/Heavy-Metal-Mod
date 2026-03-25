package com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElementRender;
import mods.eln.cable.CableRender;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.UtilsClient;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeEntity;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class PneumaticPipeRender extends IgorSixNodeElementRender {

    PneumaticPipeDescriptor descriptor;

    public PneumaticPipeRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        this.descriptor = (PneumaticPipeDescriptor) descriptor;
    }


    public boolean drawCableAuto() {
        return false;
    }

    @Override
    public void draw() {
        Minecraft.getMinecraft().mcProfiler.startSection("ECable");

        UtilsClient.bindTexture(descriptor.cable_render.cableTexture);
        glListCall();

        GL11.glColor3f(1f, 1f, 1f);
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    @Override
    public void glListDraw() {
        CableRender.drawCable(descriptor.cable_render, connectedSide, CableRender.connectionType(this, side));
        CableRender.drawNode(descriptor.cable_render, connectedSide, CableRender.connectionType(this, side));
    }

    @Override
    public boolean glListEnable() {
        return true;
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        return descriptor.cable_render;
    }
}
