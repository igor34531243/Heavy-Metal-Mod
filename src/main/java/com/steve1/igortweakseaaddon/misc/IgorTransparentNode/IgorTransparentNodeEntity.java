package com.steve1.igortweakseaaddon.misc.IgorTransparentNode;

import com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon;
import mods.eln.Eln;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBlockEntity;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;
import mods.eln.node.transparent.TransparentNodeEntity;
import net.minecraft.world.EnumSkyBlock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.igorTransparentNodeBlock;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.igorTransparentNodeItem;

public class IgorTransparentNodeEntity extends TransparentNodeEntity {
    @Override
    public void serverPublishUnserialize(DataInputStream stream) {
        serverPublishUnserialize_stage1(stream);
        try {
            Field elementRenderId = TransparentNodeEntity.class.getDeclaredField("elementRenderId");
            Field elementRender = TransparentNodeEntity.class.getDeclaredField("elementRender");
            elementRenderId.setAccessible(true);
            elementRender.setAccessible(true);
            Short id = stream.readShort();
            if (id == 0) {
                elementRenderId.set(this,0);
                elementRender.set(this,null);
                //this.elementRenderId = 0;
                //this.elementRender = null;
            } else {
                if (id != elementRenderId.get(this)) {
                    elementRenderId.set(this,id);
                    //this.elementRenderId = id;
                    TransparentNodeDescriptor descriptor = (TransparentNodeDescriptor) igorTransparentNodeItem.getDescriptor(id);
                    //this.elementRender = (TransparentNodeElementRender)descriptor.RenderClass.getConstructor(TransparentNodeEntity.class, TransparentNodeDescriptor.class).newInstance(this, descriptor);
                    elementRender.set(this,
                            (TransparentNodeElementRender) descriptor.RenderClass.getConstructor(TransparentNodeEntity.class, TransparentNodeDescriptor.class).newInstance(this, descriptor)
                    );
                }
                ((TransparentNodeElementRender)elementRender.get(this)).networkUnserialize(stream);
            }

        } catch (IOException e) {

            e.printStackTrace();
        } catch (InstantiationException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (InvocationTargetException e) {

            e.printStackTrace();
        } catch (NoSuchMethodException e) {

            e.printStackTrace();
        } catch (SecurityException e) {

            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void serverPublishUnserialize_stage1(DataInputStream stream) {

        try {
            Field firstUnserialize = NodeBlockEntity.class.getDeclaredField("firstUnserialize");
            Field redstone = NodeBlockEntity.class.getDeclaredField("redstone");
            Field lastLight = NodeBlockEntity.class.getDeclaredField("lastLight");
            firstUnserialize.setAccessible(true);
            redstone.setAccessible(true);
            lastLight.setAccessible(true);


            int light = 0;
            try {
                if ((Boolean) firstUnserialize.get(this)) {
                    firstUnserialize.set(this,false);
                    Utils.notifyNeighbor(this);

                }
                Byte b = stream.readByte();
                light = b & 0xF;
                boolean newRedstone = (b & 0x10) != 0;
                if ((Boolean)redstone.get(this) != newRedstone) {
                    redstone.set(this,newRedstone);
                    worldObj.notifyBlockChange(xCoord, yCoord, zCoord, getBlockType());
                } else {
                    redstone.set(this,newRedstone);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (((Integer)lastLight.get(this)) != light) {
                lastLight.set(this,light);
                worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
            }

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getNodeUuid() {

        return igorTransparentNodeBlock.getNodeUuid();
    }
}
