package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeDescriptor;
import mods.eln.misc.BasicContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class IgorSixNodeWithInventoryDescriptor extends IgorSixNodeDescriptor {
    public IgorSixNodeWithInventoryDescriptor(String name, Class<? extends IgorSixNodeWithInventoryElement> ElementClass, Class<? extends IgorSixNodeWithInventoryElementRender> RenderClass) {
        super(name, ElementClass, RenderClass);
    }

    public int get_inventory_size() {
        return 0;
    }

    public BasicContainer make_container(EntityPlayer player, IInventory inventory) {
        return null;
    }
}
