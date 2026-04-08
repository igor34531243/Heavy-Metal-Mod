package com.steve1.igortweakseaaddon.pneumatics.PneumaticOneWayValve;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeDescriptor;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElementRender;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.IgorSixNodeWithInventoryDescriptor;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.WithPipeInventory.IgorSixNodeWithPipeInventoryDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import mods.eln.gui.ISlotSkin;
import mods.eln.misc.BasicContainer;
import mods.eln.misc.Direction;
import mods.eln.misc.Obj3D;
import mods.eln.misc.VoltageLevelColor;
import mods.eln.node.six.SixNodeItemSlot;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import static mods.eln.i18n.I18N.tr;

public class PneumaticOneWayValveDescriptor extends IgorSixNodeWithPipeInventoryDescriptor {

    public Obj3D model;
    public Obj3D.Obj3DPart main;

    public PneumaticOneWayValveDescriptor(String name, Obj3D model) {
        super(name, PneumaticOneWayValveElement.class, PneumaticOneWayValveRender.class,0);
        this.voltageLevelColor=VoltageLevelColor.Neutral;
        this.model=model;
        this.main=model.getPart("main");
    }

    public void draw() {
        if (main!=null) {
            main.draw();
        }
    }

    @Override
    public boolean has_gui() {
        return true;
    }

    @Override
    public GuiScreen make_gui(Direction side, EntityPlayer player, IgorSixNodeElementRender render) {
        return new PneumaticOneWayValveGui(player, (PneumaticOneWayValveRender) render);
    }

    @Override
    public int get_inventory_size() {
        return 1;
    }

    @Override
    public BasicContainer make_container(EntityPlayer player, IInventory inventory) {
        return new BasicContainer(player, inventory, new Slot[]{
                new SixNodeItemSlot(inventory, pipeId, 20, 95, 1,
                        new Class[]{PneumaticPipeDescriptor.class},
                        ISlotSkin.SlotSkin.medium,
                        new String[]{tr("Air Pipe SLot"), tr("(sets pipe type)")})
        });
    }
}
