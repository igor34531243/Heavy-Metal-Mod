package com.steve1.igortweakseaaddon.pneumatics.PneumaticValve;

import com.steve1.igortweakseaaddon.misc.IgorGuiContainerEln;
import mods.eln.gui.GuiButtonEln;
import mods.eln.gui.IGuiObject;
import net.minecraft.entity.player.EntityPlayer;

import static com.steve1.igortweakseaaddon.pneumatics.PneumaticValve.PneumaticValveElement.setPositionId;

public class PneumaticValveGui extends IgorGuiContainerEln {

    public PneumaticValveRender render;

    public GuiButtonEln select_position;

    public boolean selected_position=false;

    public PneumaticValveGui(EntityPlayer player, PneumaticValveRender got_render) {
        super(player, got_render, 30);
        render = got_render;
    }

    @Override
    public void initGui() {
        super.initGui();

        select_position=newGuiButton(6 + 32, 6,100,"Open By Default");
        select_position.setComment(0,"Selected to be fully");
        select_position.setComment(1,"opened when signal is 0");
        select_position.setComment(2,"and closed when signal is 1");

        sync_changes();
    }

    public void sync_changes() {
        selected_position=render.selected_position;

        if (selected_position) {
            select_position.displayString="Open By Default";
            select_position.setComment(0,"Selected to be fully");
            select_position.setComment(1,"opened when signal is 0");
            select_position.setComment(2,"and closed when signal is 1");
        } else {
            select_position.displayString="Closed By Default";
            select_position.setComment(0,"Selected to be fully");
            select_position.setComment(1,"closed when signal is 0");
            select_position.setComment(2,"and opened when signal is 1");
        }

        render.has_changes=false;
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object == select_position) {
            render.clientSetBoolean(setPositionId, !selected_position);
        }
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        if (render.has_changes) sync_changes();
    }
}
