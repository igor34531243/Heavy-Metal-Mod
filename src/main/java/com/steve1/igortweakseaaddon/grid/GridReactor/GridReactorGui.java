package com.steve1.igortweakseaaddon.grid.GridReactor;

import mods.eln.gui.GuiContainerEln;
import mods.eln.gui.GuiHelperContainer;
import mods.eln.gui.IGuiObject;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNodeElementInventory;
import mods.eln.transparentnode.powerinductor.PowerInductorContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import static mods.eln.i18n.I18N.tr;

public class GridReactorGui extends GuiContainerEln {

    public TransparentNodeElementInventory inventory;
    GridReactorRender render;

    public GridReactorGui(EntityPlayer player, IInventory inventory, GridReactorRender render) {
        super(new PowerInductorContainer(player, inventory));
        this.inventory = (TransparentNodeElementInventory) inventory;
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f, int mx, int my) {
        // ide marks this as invalid but it has be be like this
        // it builds fine with this line and breaks totaly without
        super.func_146976_a(f, mx, my);
    }


    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);

    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
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
