package com.steve1.igortweakseaaddon.grid.GridReactor;

import com.steve1.igortweakseaaddon.grid.IgorGrid.IgorGridRender;
import mods.eln.misc.Direction;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementInventory;
import mods.eln.node.transparent.TransparentNodeEntity;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import java.io.DataInputStream;

public class GridReactorRender extends IgorGridRender {
    public GridReactorDescriptor descriptor;

    TransparentNodeElementInventory inventory = new TransparentNodeElementInventory(2, 64, this);

    public GridReactorRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor_input) {
        super(tileEntity, descriptor_input);
        this.descriptor = (GridReactorDescriptor) descriptor_input;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {

        return new GridReactorGui(player, inventory, this);
    }
}
