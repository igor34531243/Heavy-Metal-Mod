package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElementRender;
import mods.eln.misc.Direction;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElementInventory;
import mods.eln.node.six.SixNodeEntity;

public class IgorSixNodeWithInventoryElementRender extends IgorSixNodeElementRender {

    public SixNodeElementInventory inventory;
    public IgorSixNodeWithInventoryDescriptor igorSixNodeDescriptor;

    public IgorSixNodeWithInventoryElementRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor descriptor) {
        super(tileEntity, side, descriptor);
        igorSixNodeDescriptor=(IgorSixNodeWithInventoryDescriptor) descriptor;
        inventory=new SixNodeElementInventory(igorSixNodeDescriptor.get_inventory_size(),64,this);
    }
}
