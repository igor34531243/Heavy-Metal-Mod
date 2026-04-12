package com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorSimpleShaft;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeElementRender;
import com.steve1.igortweakseaaddon.misc.IgorLoopedSound;
import mods.eln.cable.CableRender;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.cable.CableRenderType;
import mods.eln.misc.*;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.IOException;

public class IgorSimpleShaftRender extends IgorTransparentNodeElementRender {

    public IgorSimpleShaftDescriptor descriptor;

    public double rads=0;
    public double logRads=0;
    public double angle=0;

    public LRDUMask eConn=new LRDUMask();
    public LRDUMask mask=new LRDUMask();
    public CableRenderType connectionType=null;
    public CableRenderDescriptor cableRender=null;
    public boolean cableRefresh=true;
    public IgorLoopedSound soundLooper=null;
    public SlewLimiter volumeSetting = new SlewLimiter(0.5f);

    public IgorSimpleShaftRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor got_descriptor) {
        super(tileEntity, got_descriptor);
        descriptor=(IgorSimpleShaftDescriptor)got_descriptor;
        initSound();
        mask.set(LRDU.Down, true);
    }

    public void initSound() {
        volumeSetting.setTarget(1f);
        volumeSetting.setPosition(0f);
        String sound = descriptor.sound;
        if (sound != null) {
            soundLooper = new IgorLoopedSound(sound, coordonate()) {
                @Override
                public float getPitch() {
                    return (float) Math.max(0.05, rads / 1000);
                }

                @Override
                public float getVolume() {
                    return volumeSetting.getPosition();
                }
            };
            addIgorLoopedSound(soundLooper);
        } else {
            soundLooper = null;
        }
    }

    @Override
    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            rads = stream.readDouble();
            logRads = Math.log(rads + 1) / Math.log(1.2);
            eConn.deserialize(stream);
            cableRefresh = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void draw() {
        super.draw();

        if (cableRender != null) {
            GL11.glPushMatrix();

            if (cableRefresh) {
                cableRefresh = false;
                connectionType = CableRender.connectionType(tileEntity, eConn, front.down());
            }

            glCableTransforme(front.down());
            cableRender.bindCableTexture();

            for (LRDU lrdu : LRDU.values()) {
                Utils.setGlColorFromDye(connectionType.otherdry[lrdu.toInt()]);
                if (!eConn.get(lrdu)) continue;
                if (lrdu != front.down().getLRDUGoingTo(front) && lrdu.inverse() != front.down().getLRDUGoingTo(front))
                    continue;
                mask.set(1 << lrdu.ordinal());
                CableRender.drawCable(cableRender, mask, connectionType);
            }

            GL11.glPopMatrix();
        }
    }

    @Override
    public void refresh(float time) {
        super.refresh(time);
        angle += logRads * time;
        volumeSetting.step(time);
    }
}
