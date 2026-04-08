package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElement;
import mods.eln.misc.BasicContainer;
import mods.eln.misc.Direction;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElementInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public abstract class IgorSixNodeWithInventoryElement extends IgorSixNodeElement {

    public IInventory inventory;
    public IgorSixNodeWithInventoryDescriptor igorSixNodeWithInventoryDescriptor;

    public IgorSixNodeWithInventoryElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);
        igorSixNodeWithInventoryDescriptor= (IgorSixNodeWithInventoryDescriptor) descriptor;
        inventory=new SixNodeElementInventory(igorSixNodeWithInventoryDescriptor.get_inventory_size(),64,this);
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    @Override
    public BasicContainer newContainer(Direction side, EntityPlayer player) {
        return igorSixNodeWithInventoryDescriptor.make_container(player,inventory);
    }
}
