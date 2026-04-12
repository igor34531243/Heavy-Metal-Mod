package com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode;

import com.steve1.igortweakseaaddon.misc.IgorLoopedSound;
import com.steve1.igortweakseaaddon.misc.IgorLoopedSoundManager;
import com.steve1.igortweakseaaddon.misc.NetLRDUCubeMask;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.eln.cable.CableRender;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.cable.CableRenderType;
import mods.eln.misc.*;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;
import mods.eln.node.transparent.TransparentNodeEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class IgorTransparentNodeElementRender extends TransparentNodeElementRender {

    public IgorTransparentNodeDescriptor stored_descriptor;

    public LRDUCubeMask connectedSide = new LRDUCubeMask();
    public int cableList[];
    public boolean cableListReady[];
    public boolean cableDirListReady[];
    public CableRenderType[] connectionType;
    public boolean needRedraw=true;

    public IgorTransparentNodeElementRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor) {
        super(tileEntity, descriptor);
        stored_descriptor=(IgorTransparentNodeDescriptor)descriptor;

        cableList = new int[6*4];
        cableListReady = new boolean[6*4];
        cableDirListReady = new boolean[6];
        connectionType = new CableRenderType[6];

        for (Direction direction : Direction.values()) {
            for (LRDU lrdu : LRDU.values()) {
                int ids=direction.getInt()*4 + lrdu.toInt();
                cableList[ids] = UtilsClient.glGenListsSafe();
                cableListReady[ids]=false;
            }
            cableDirListReady[direction.getInt()]=false;
        }
    }

    @Override
    public void draw() {
        cable_draw_main();
        GL11.glPushMatrix();
        front.glRotateXnRef();
        stored_descriptor.draw_initial(this);
        GL11.glPopMatrix();
    }

    public IgorLoopedSoundManager igorLoopedSoundManager = new IgorLoopedSoundManager();

    @SideOnly(Side.CLIENT)
    public void addIgorLoopedSound(final IgorLoopedSound loopedSound) {
        igorLoopedSoundManager.add(loopedSound);
    }

    @Override
    public void destructor() {
        super.destructor();

        for (Direction direction : Direction.values()) {
            for (LRDU lrdu : LRDU.values()) {
                int ids=direction.getInt()*4 + lrdu.toInt();
                UtilsClient.glDeleteListsSafe(cableList[ids]);
            }
        }

        igorLoopedSoundManager.dispose();
    }

    @Override
    public void refresh(float deltaT) {
        super.refresh(deltaT);
        igorLoopedSoundManager.process(deltaT);
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            connectedSide=NetLRDUCubeMask.deserialize(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        needRedrawCable();
    }

    @Override
    public void notifyNeighborSpawn() {
        needRedrawCable();
    }

    public void needRedrawCable() {
        needRedraw = true;
    }

    public void drawCables() {
        for (Direction direction : Direction.values()) {
            cableDirListReady[direction.getInt()]=false;
            for (LRDU lrdu : LRDU.values()) {
                int idx=direction.getInt()*4 + lrdu.toInt();
                cableListReady[idx] = false;
                if (connectedSide.get(direction,lrdu)) {
                    CableRenderDescriptor render = getCableRender(direction,lrdu);
                    if (render==null) {
                        continue;
                    }
                    GL11.glNewList(cableList[idx], GL11.GL_COMPILE);
                    GL11.glPushMatrix();
                    Direction turn_to=direction.applyLRDU(lrdu);
                    LRDU to_put=lrdu.inverse();
                    if (turn_to.isY()) {
                        if (turn_to==Direction.YN) {
                            direction.getInverse().glRotateXnRef();
                        } else {
                            direction.glRotateXnRef();
                        }
                    }
                    if (direction.isY()) {
                        if (direction==Direction.YP && lrdu==LRDU.Up || direction==Direction.YN && lrdu==LRDU.Down) {
                            to_put=to_put.inverse();
                        }
                        if (lrdu == LRDU.Right || lrdu == LRDU.Left) {
                            if ((lrdu == LRDU.Right) == (direction == Direction.YP)) {
                                to_put = to_put.right();
                            } else {
                                to_put = to_put.left();
                            }
                        }
                    }
                    turn_to.glRotateXnRef();
                    GL11.glTranslatef(-0.5F, 0f, 0f);
                    UtilsClient.bindTexture(render.cableTexture);
                    CableRender.drawCable(render, new LRDUMask(1 << to_put.toInt()), connectionType[direction.getInt()]);
                    GL11.glPopMatrix();
                    if (direction==Direction.YP && lrdu==LRDU.Down && false ) {
                        GL11.glPushMatrix();
                        turn_to=direction.applyLRDU(lrdu);
                        if (direction.isY()) {
                            if (direction==Direction.YP && lrdu==LRDU.Down) {
                                Direction.XN.glRotateXnRef();
                            }
                        }
                        turn_to.glRotateXnRef();
                        GL11.glTranslatef(-0.5F, 0f, 0f);
                        UtilsClient.bindTexture(render.cableTexture);
                        CableRender.drawCable(render, new LRDUMask(1 << lrdu.inverse().toInt()), connectionType[direction.getInt()]);
                        GL11.glPopMatrix();
                    }
                    GL11.glEndList();
                    cableListReady[idx] = true;
                    cableDirListReady[direction.getInt()]=true;
                }
            }
        }
    }

    public void cable_draw_main() {
        if (needRedraw) {
            needRedraw = false;
            for (Direction direction : Direction.values()) {
                connectionType[direction.getInt()] = CableRender.connectionType(tileEntity, connectedSide.get(direction), direction);
            }
            drawCables();
        }


        for (int ids = 0; ids < 6; ids++) {
            if (cableDirListReady[ids]) {
                for (int idx = 0; idx < 4; idx++) {
                    int idn = ids * 4 + idx;
                    if (cableListReady[idn]) {
                        Utils.setGlColorFromDye(connectionType[ids].otherdry[idx]);
                        GL11.glCallList(cableList[idn]);
                    }
                }
            }
        }

        GL11.glColor3f(1f, 1f, 1f);
    }
}
