package com.steve1.igortweakseaaddon.misc.IgorTransparentNode;

import mods.eln.Eln;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElement;
import net.minecraft.item.ItemStack;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.igorTransparentNodeBlock;

public abstract class IgorTransparentNodeElement extends TransparentNodeElement {

    public IgorTransparentNodeElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
    }

    public ItemStack getDropItemStack() {
        ItemStack itemStack = new ItemStack(igorTransparentNodeBlock, 1, node.elementId);
        itemStack.setTagCompound(getItemStackNBT());
        return itemStack;
    }
}
