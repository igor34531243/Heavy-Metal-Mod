package com.steve1.igortweakseaaddon.misc.IgorTransparentNode;

import mods.eln.Eln;
import mods.eln.misc.Direction;
import mods.eln.misc.Utils;
import mods.eln.node.Node;
import mods.eln.node.NodeBase;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.igorTransparentNodeBlock;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.igorTransparentNodeItem;

public class IgorTransparentNode extends TransparentNode {
    @Override
    public void initializeFromThat(Direction side, EntityLivingBase entityLiving, ItemStack itemStack) {
        try {
            // Direction front = null;
            TransparentNodeDescriptor descriptor = igorTransparentNodeItem.getDescriptor(itemStack);
            /*
             * switch(descriptor.getFrontType()) { case BlockSide: front = side; break; case PlayerView: front = Utils.entityLivingViewDirection(entityLiving).getInverse(); break; case PlayerViewHorizontal: front = Utils.entityLivingHorizontalViewDirection(entityLiving).getInverse(); break;
             *
             * }
             */

            int metadata = itemStack.getItemDamage();
            elementId = metadata;
            element = (TransparentNodeElement) descriptor.ElementClass.getConstructor(TransparentNode.class, TransparentNodeDescriptor.class).newInstance(this, descriptor);
            element.initializeFromThat(side, entityLiving, itemStack.getTagCompound());
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
        }

        Utils.println("TN.iFT element = " + element + " elId = " + elementId);

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        readFromNBT_stage1(nbt.getCompoundTag("node"));

        elementId = nbt.getShort("eid");
        try {
            TransparentNodeDescriptor descriptor = igorTransparentNodeItem.getDescriptor(elementId);
            element = (TransparentNodeElement) descriptor.ElementClass.getConstructor(TransparentNode.class, TransparentNodeDescriptor.class).newInstance(this, descriptor);
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
        }
        element.readFromNBT(nbt.getCompoundTag("element"));

    }

    public void readFromNBT_stage1(NBTTagCompound nbt) {
        readFromNBT_stage2(nbt);
        try {
            //lastLight = nbt.getByte("lastLight");
            Field lastLight = Node.class.getDeclaredField("lastLight");
            lastLight.setAccessible(true);
            lastLight.set(this,nbt.getByte("lastLight"));

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public void readFromNBT_stage2(NBTTagCompound nbt) {
        try {
            Field initialized = NodeBase.class.getDeclaredField("initialized");
            initialized.setAccessible(true);

            coordonate.readFromNBT(nbt, "c");

            neighborOpaque = nbt.getByte("NBOpaque");
            neighborWrapable = nbt.getByte("NBWrap");

            initialized.set(this,true);
            //initialized = true;

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
