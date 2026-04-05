package com.steve1.igortweakseaaddon.grid.GridReactor;

import mods.eln.generic.GenericItemUsingDamageSlot;
import mods.eln.gui.ISlotSkin;
import mods.eln.item.CopperCableDescriptor;
import mods.eln.item.FerromagneticCoreDescriptor;
import mods.eln.misc.BasicContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import static mods.eln.i18n.I18N.tr;

public class GridReactorContainer extends BasicContainer {
    static final int cableId = 0;
    static final int coreId = 1;

    public GridReactorContainer(EntityPlayer player, IInventory inventory) {
        super(player, inventory, new Slot[]{
                new GenericItemUsingDamageSlot(inventory, cableId, 132, 8, 19, CopperCableDescriptor.class,
                        ISlotSkin.SlotSkin.medium,
                        new String[]{tr("Copper cable slot"), tr("(Increases inductance)")}),
                new GenericItemUsingDamageSlot(inventory, coreId, 132 + 20, 8, 1, FerromagneticCoreDescriptor.class,
                        ISlotSkin.SlotSkin.medium,
                        new String[]{tr("Ferromagnetic core slot")})});
    }


}
