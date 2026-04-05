package com.steve1.igortweakseaaddon.grid.GridReactor;

import com.steve1.igortweakseaaddon.misc.IgorGuiContainerEln;
import mods.eln.gui.GuiContainerEln;
import mods.eln.gui.GuiHelperContainer;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNodeElementInventory;
import mods.eln.transparentnode.powerinductor.PowerInductorContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import static mods.eln.i18n.I18N.tr;

public class GridReactorGui extends IgorGuiContainerEln {

    public TransparentNodeElementInventory inventory;
    GridReactorRender render;

    public GridReactorGui(EntityPlayer player, IInventory inventory, GridReactorRender render) {
        super(new PowerInductorContainer(player, inventory));
        this.inventory = (TransparentNodeElementInventory) inventory;
        this.render = render;
    }

    @Override
    protected void postDraw(float f, int x, int y) {
        helper.drawString(8, 12, 0xFF000000, tr("Inductance: %1$H", Utils.plotValue(render.descriptor.getlValue(render.inventory))));
        super.postDraw(f, x, y);
    }

    @Override
    protected GuiHelperContainer newHelper() {

        return new GuiHelperContainer(this, 176, 166 - 54, 8, 84 - 54);
    }
}
