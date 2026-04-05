package com.steve1.igortweakseaaddon.misc;

import mods.eln.gui.GuiContainerEln;
import net.minecraft.inventory.Container;

public abstract class IgorGuiContainerEln extends GuiContainerEln {

    public IgorGuiContainerEln(Container par1Container) {
        super(par1Container);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int mx, int my) {
        // ide marks this as invalid but it has be be like this
        // it builds fine with this line and breaks totaly without
        super.func_146976_a(f, mx, my);
    }
}
