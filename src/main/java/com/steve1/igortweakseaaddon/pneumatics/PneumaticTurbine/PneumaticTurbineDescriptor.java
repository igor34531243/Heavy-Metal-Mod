package com.steve1.igortweakseaaddon.pneumatics.PneumaticTurbine;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorSimpleShaft.IgorSimpleShaftDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import mods.eln.gui.ISlotSkin;
import mods.eln.misc.BasicContainer;
import mods.eln.misc.Obj3D;
import mods.eln.node.six.SixNodeItemSlot;
import mods.eln.node.transparent.EntityMetaTag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import static mods.eln.i18n.I18N.tr;

public class PneumaticTurbineDescriptor extends IgorSimpleShaftDescriptor {

    public static final int pipeIdTurbine=0;

    public double air_to_rot_cof;
    public double rot_to_air_cof;

    public PneumaticTurbineDescriptor(String name, Obj3D model, double air_to_rot_cof, double rot_to_air_cof) {
        super(name, model, PneumaticTurbineElement.class, PneumaticTurbineRender.class, EntityMetaTag.Basic);
        this.air_to_rot_cof=air_to_rot_cof;
        this.rot_to_air_cof=rot_to_air_cof;
    }

    public BasicContainer make_container(EntityPlayer player, IInventory inventory) {
        return new BasicContainer(player, inventory, new Slot[]{
                new SixNodeItemSlot(inventory, pipeIdTurbine, 80, 8, 1,
                        new Class[]{PneumaticPipeDescriptor.class},
                        ISlotSkin.SlotSkin.medium,
                        new String[]{tr("Air Pipe SLot"), tr("(sets pipe type)")})
        });
    }
}
