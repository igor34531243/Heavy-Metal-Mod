package com.steve1.igortweakseaaddon.misc;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.IgorSixNodeWithInventoryElementRender;
import mods.eln.gui.GuiContainerEln;
import mods.eln.gui.GuiHelperContainer;
import mods.eln.misc.BasicContainer;
import net.minecraft.entity.player.EntityPlayer;

public class IgorGuiContainerEln extends GuiContainerEln {

    public int xsize;
    public int ysize;

    public IgorGuiContainerEln(EntityPlayer player, IgorSixNodeWithInventoryElementRender render, int xsize, int ysize) {
        super(render.igorSixNodeDescriptor.make_container(player, render.inventory));
        this.xsize=Math.min(xsize,176);
        this.ysize=ysize;
    }

    public IgorGuiContainerEln(EntityPlayer player, IgorSixNodeWithInventoryElementRender render, int ysize) {
        this(player,render,176,ysize);
    }

    public IgorGuiContainerEln(BasicContainer container, int xsize, int ysize) {
        super(container);
        this.xsize=Math.min(xsize,176);
        this.ysize=ysize;
    }

    public IgorGuiContainerEln(BasicContainer container, int ysize) {
        this(container,176,ysize);
    }

    public IgorGuiContainerEln(BasicContainer container) {
        super(container);
    }

    @Override
    protected GuiHelperContainer newHelper() {
        return new GuiHelperContainer(this, xsize, ysize + 82, 8, ysize);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int mx, int my) {
        // ide marks this as invalid but it has be be like this
        // it builds fine with this line and breaks totaly without
        super.func_146976_a(f, mx, my);
    }
}
