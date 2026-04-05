package com.steve1.igortweakseaaddon.pneumatics.PneumaticOneWayValve;

import mods.eln.gui.*;
import net.minecraft.entity.player.EntityPlayer;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.plot_area;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.plot_pascals_atmospheres;
import static com.steve1.igortweakseaaddon.pneumatics.PneumaticOneWayValve.PneumaticOneWayValveElement.*;

public class PneumaticOneWayValveGui extends GuiScreenEln {

    PneumaticOneWayValveRender render;
    GuiVerticalTrackBar pressure;
    GuiButtonEln select_mode;
    GuiButtonEln select_side;
    GuiButtonEln select_open;
    GuiVerticalTrackBar to_open_area;
    GuiVerticalTrackBar to_close_area;

    boolean mode_is_p_diff=true;
    boolean side_is_yellow=true;
    boolean open_if_above=true;

    public PneumaticOneWayValveGui(EntityPlayer player, PneumaticOneWayValveRender render) {
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();

        pressure = newGuiVerticalTrackBar(6+80, 6 + 2, 20, 100);
        pressure.setStepIdMax((int) 100);
        pressure.setEnable(true);
        pressure.setRange(0f, 1f);

        select_mode=newGuiButton(6,6+22,76,"P difference");
        select_mode.setComment(0,"Selected to measure");
        select_mode.setComment(1,"pressure as difference");
        select_mode.setComment(2,"of pressure between sides");

        select_side=newGuiButton(6,6+22+22,76,"ΔP = P§e■ §f - P§a■");
        select_side.setComment(0,"Selected to measure");
        select_side.setComment(1,"pressure difference");
        select_side.setComment(2,"as ΔP = P§e■ §f - P§a■");

        select_open=newGuiButton(6,6+22+44,76,"open if above");
        select_open.setComment(0,"Selected to open");
        select_open.setComment(1,"valve if pressure is");
        select_open.setComment(2,"above the chosen");

        to_open_area=newGuiVerticalTrackBar(6+80+24, 6 + 2, 20, 100);
        to_open_area.setStepIdMax((int) 100);
        to_open_area.setEnable(true);
        to_open_area.setRange(0f, 1f);

        to_close_area= newGuiVerticalTrackBar(6+80+48, 6 + 2, 20, 100);
        to_close_area.setStepIdMax((int) 100);
        to_close_area.setEnable(true);
        to_close_area.setRange(0f, 1f);

        syncWithRender();
    }

    public void syncWithRender() {
        pressure.setValue((float) render.set_pressure/render.max_pressure);
        to_open_area.setValue((float)(render.to_open_area/render.max_area));
        to_close_area.setValue((float)(render.to_close_area/render.max_area));

        mode_is_p_diff=render.mode_is_p_diff;
        side_is_yellow=render.side_is_yellow;
        open_if_above=render.open_if_above;

        if (mode_is_p_diff) {
            select_mode.displayString="P difference";
            select_mode.setComment(0,"Selected to measure");
            select_mode.setComment(1,"pressure as difference");
            select_mode.setComment(2,"of pressure between sides");
            if (side_is_yellow) {
                select_side.displayString="ΔP = P§e■ §f - P§a■";
                select_side.setComment(0,"Selected to measure");
                select_side.setComment(1,"pressure difference");
                select_side.setComment(2,"as ΔP = P§e■ §f - P§a■");
            } else {
                select_side.displayString="ΔP = P§a■ §f - P§e■";
                select_side.setComment(0,"Selected to measure");
                select_side.setComment(1,"pressure as difference");
                select_side.setComment(2,"between P§a■§f and P§e■");
            }
        } else {
            select_mode.displayString="P from side";
            select_mode.setComment(0,"Selected to measure");
            select_mode.setComment(1,"pressure as pressure");
            select_mode.setComment(2,"of a chosen side");
            if (side_is_yellow) {
                select_side.displayString="P = P§e■";
                select_side.setComment(0,"Selected to measure");
                select_side.setComment(1,"pressure as pressure");
                select_side.setComment(2,"of §e■§f yellow side");
            } else {
                select_side.displayString="P = P§a■";
                select_side.setComment(0,"Selected to measure");
                select_side.setComment(1,"pressure as pressure");
                select_side.setComment(2,"of §a■§f green side");
            }
        }

        if (open_if_above) {
            select_open.displayString="open if above";
            select_open.setComment(0,"Selected to open");
            select_open.setComment(1,"valve if pressure is");
            select_open.setComment(2,"above the chosen");
        } else {
            select_open.displayString="open if below";
            select_open.setComment(0,"Selected to open");
            select_open.setComment(1,"valve if pressure is");
            select_open.setComment(2,"below the chosen");
        }

        render.hasChanges = false;
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object == pressure) {
            render.clientSetLong(setPressureId,(long)(render.max_pressure*pressure.getValue()));
        } else if (object==select_mode) {
            byte flags=0;
            flags+=1<<4;
            flags+= (byte) (mode_is_p_diff ? 0 : 1);
            render.clientSetByte(setFlagId, flags);
        } else if (object==select_side) {
            byte flags=0;
            flags+=2<<4;
            flags+= (byte) (side_is_yellow ? 0 : 1);
            render.clientSetByte(setFlagId, flags);
        } else if (object==select_open) {
            byte flags=0;
            flags+=3<<4;
            flags+= (byte) (open_if_above ? 0 : 1);
            render.clientSetByte(setFlagId, flags);
        } else if (object == to_open_area) {
            render.clientSetDouble(setToOpenAreaId,render.max_area*to_open_area.getValue());
        } else if (object == to_close_area) {
            render.clientSetDouble(setToCloseAreaId,render.max_area*to_close_area.getValue());
        }
    }

    @Override
    protected void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        if (render.hasChanges) syncWithRender();
        pressure.setComment(0, "Pressure is set to "+plot_pascals_atmospheres(pressure.getValue() * render.max_pressure));
        to_open_area.setComment(0, "Area when open is "+plot_area(to_open_area.getValue() * render.max_area));
        to_close_area.setComment(0, "Area when closed is "+plot_area(to_close_area.getValue() * render.max_area));
    }

    @Override
    protected GuiHelper newHelper() {
        return new GuiHelper(this, 12 + 20+80+48, 12 + 100 + 4);
    }
}