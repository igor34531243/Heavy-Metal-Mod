package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.WithPipeInventory;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.IgorSixNodeWithInventoryDescriptor;

public class IgorSixNodeWithPipeInventoryDescriptor extends IgorSixNodeWithInventoryDescriptor {

    public int pipeId;

    public IgorSixNodeWithPipeInventoryDescriptor(String name, Class<? extends IgorSixNodeWithPipeInventoryElement> ElementClass, Class<? extends IgorSixNodeWithPipeInventoryElementRender> RenderClass, int pipeId) {
        super(name, ElementClass, RenderClass);
        this.pipeId=pipeId;
    }
}
