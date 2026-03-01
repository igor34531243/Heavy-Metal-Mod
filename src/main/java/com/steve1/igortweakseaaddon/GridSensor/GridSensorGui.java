package com.steve1.igortweakseaaddon.GridSensor;

import mods.eln.gui.*;
import mods.eln.misc.BasicContainer;
import mods.eln.misc.UtilsClient;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static mods.eln.i18n.I18N.tr;

public class GridSensorGui extends GuiContainerEln {

    GuiButton validate, voltageType, currentType, powerType, dirType;
    GuiTextFieldEln lowValue, highValue;
    GridSensorRender render;

    public GridSensorGui(EntityPlayer player, GridSensorRender render) {
        super(new GridSensorContainer(player));
        this.render = render;
    }

    @Override
    public void initGui() {
        super.initGui();

        voltageType = newGuiButton(8, 8, 50, tr("Voltage"));
        currentType = newGuiButton(8, 8 + 24, 50, tr("Current"));
        powerType = newGuiButton(8, 8 + 48, 50, tr("Power"));
        dirType = newGuiButton(8 + 50 + 4, 8 + 48, 50, "");

        int x = 0, y = -12;
        validate = newGuiButton(x + 8 + 50 + 4 + 50 + 4, y + (166 - 84) / 2 - 9, 50, tr("Validate"));

        lowValue = newGuiTextField(x + 8 + 50 + 4, y + (166 - 84) / 2 + 3, 50);
        lowValue.setText(render.lowValue);
        lowValue.setComment(tr("Measured value\ncorresponding\nto 0% output").split("\n"));

        highValue = newGuiTextField(x + 8 + 50 + 4, y + (166 - 84) / 2 - 13, 50);
        highValue.setText(render.highValue);
        highValue.setComment(tr("Measured value\ncorresponding\nto 100% output").split("\n"));
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object == validate) {
            float lowVoltage, highVoltage;

            try {
                lowVoltage = NumberFormat.getInstance().parse(lowValue.getText()).floatValue();
                highVoltage = NumberFormat.getInstance().parse(highValue.getText()).floatValue();
                render.clientSetFloat(GridSensorElement.setValueId, lowVoltage, highVoltage);
            } catch (ParseException e) {
            }
        } else if (object == currentType) {
            render.clientSetByte(GridSensorElement.setTypeOfSensorId, GridSensorElement.currantType);
        } else if (object == voltageType) {
            render.clientSetByte(GridSensorElement.setTypeOfSensorId, GridSensorElement.voltageType);
        } else if (object == powerType) {
            render.clientSetByte(GridSensorElement.setTypeOfSensorId, GridSensorElement.powerType);
        } else if (object == dirType) {
            render.dirType = (byte) ((render.dirType + 1) % 3);
            render.clientSetByte(GridSensorElement.setDirType, render.dirType);
        }
    }

    @Override
    public void preDraw(float f, int x, int y) {
        super.preDraw(f, x, y);
        switch (render.dirType) {
            case GridSensorElement.dirNone:
                dirType.displayString = "\u00a72\u25CF\u00a77 <=> \u00a71\u25CF";
                break;
            case GridSensorElement.dirAB:
                dirType.displayString = "\u00a72\u25CF\u00a77 => \u00a71\u25CF";
                break;
            case GridSensorElement.dirBA:
                dirType.displayString = "\u00a72\u25CF\u00a77 <= \u00a71\u25CF";
                break;
        }

        if (render.typeOfSensor == GridSensorElement.currantType) {
            powerType.enabled = true;
            currentType.enabled = false;
            voltageType.enabled = true;
        } else if (render.typeOfSensor == GridSensorElement.voltageType) {
            powerType.enabled = true;
            currentType.enabled = true;
            voltageType.enabled = false;
        } else if (render.typeOfSensor == GridSensorElement.powerType) {
            powerType.enabled = false;
            currentType.enabled = true;
            voltageType.enabled = true;
        }
    }

    @Override
    public GuiHelperContainer newHelper() {
        return new GuiHelperContainer(this, 176, 166 - 45, 8, 84 - 45);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float f,int mx,int my) {
        try {
            GL11.glColor4f(1f, 1f, 1f, 1f);
            preDraw(f, mx, my);
//            Method mouseMove = helper.getClass().getMethod("mouseMove",int.class,int.class);
//            Method draw = helper.getClass().getMethod("draw",int.class,int.class,float.class);
//            mouseMove.setAccessible(true);
//            draw.setAccessible(true);
//            mouseMove.invoke(helper,mx,my);
//            draw.invoke(helper,mx,my,f);
            //helper.mouseMove(mx, my);
            //helper.draw(mx, my, f);
//            Field slotSkin=this.getClass().getDeclaredField("slotSkin");
//            slotSkin.setAccessible(true);
//            UtilsClient.bindTexture((ResourceLocation) slotSkin.get(this));
            UtilsClient.bindTexture(new ResourceLocation("textures/gui/container/furnace.png"));
            GL11.glColor4f(1f, 1f, 1f, 1f);

            for (Object o : inventorySlots.inventorySlots) {
                Slot slot = (Slot) o;
                ISlotSkin.SlotSkin skin = ISlotSkin.SlotSkin.none;

                if (slot instanceof ISlotSkin) skin = ((ISlotSkin) slot).getSlotSkin();

                switch (skin) {
                    case medium:
                        drawTexturedModalRectEln(slot.xDisplayPosition - 1, slot.yDisplayPosition - 1, 55, 16, 73 - 55, 34 - 16);
                        break;
                    case big:
                        drawTexturedModalRectEln(slot.xDisplayPosition - 5, slot.yDisplayPosition - 5, 111, 30, 137 - 111, 56 - 30);
                        break;
                }
            }
            postDraw(f, mx, my);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("");
        }
    }
}
