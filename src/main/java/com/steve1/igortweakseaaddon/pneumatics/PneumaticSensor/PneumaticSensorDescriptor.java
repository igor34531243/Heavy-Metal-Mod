package com.steve1.igortweakseaaddon.pneumatics.PneumaticSensor;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElementRender;
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

public class PneumaticSensorDescriptor extends IgorSixNodeWithPipeInventoryDescriptor {

    Obj3D model;
    Obj3D.Obj3DPart main;

    public PneumaticSensorDescriptor(String name, Obj3D model) {
        super(name, PneumaticSensorElement.class, PneumaticSensorRender.class, 0);
        voltageLevelColor=VoltageLevelColor.Neutral;
        this.model=model;
        main=model.getPart("main");
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
        return new PneumaticSensorGui(player, (PneumaticSensorRender) render);
    }

    @Override
    public int get_inventory_size() {
        return 1;
    }

    @Override
    public BasicContainer make_container(EntityPlayer player, IInventory inventory) {
        return new BasicContainer(player, inventory, new Slot[]{
                new SixNodeItemSlot(inventory, pipeId, 152, 8+2+72-12, 1,
                        new Class[]{PneumaticPipeDescriptor.class},
                        ISlotSkin.SlotSkin.medium,
                        new String[]{tr("Air Pipe SLot"), tr("(sets pipe type)")})
        });
    }
}
