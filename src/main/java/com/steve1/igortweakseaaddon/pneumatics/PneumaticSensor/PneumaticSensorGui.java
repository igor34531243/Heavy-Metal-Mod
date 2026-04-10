package com.steve1.igortweakseaaddon.pneumatics.PneumaticSensor;

import com.steve1.igortweakseaaddon.grid.GridSensor.GridSensorElement;
import com.steve1.igortweakseaaddon.misc.IgorGuiContainerEln;
import mods.eln.gui.GuiButtonEln;
import mods.eln.gui.GuiTextFieldEln;
import mods.eln.gui.IGuiObject;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

import java.text.NumberFormat;
import java.text.ParseException;

import static com.steve1.igortweakseaaddon.pneumatics.PneumaticSensor.PneumaticSensorElement.*;
import static mods.eln.i18n.I18N.tr;

public class PneumaticSensorGui extends IgorGuiContainerEln {

    PneumaticSensorRender render;

    GuiButtonEln sensorModeButton;
    GuiButtonEln directionModeButton;
    GuiButtonEln valueModeButton;
    GuiButtonEln displayModeButton;

    GuiTextFieldEln lowValue;
    GuiTextFieldEln highValue;

    GuiButtonEln validate;

    public PneumaticSensorElement.SensorMode sensorMode= PneumaticSensorElement.SensorMode.pressure_mode;
    public PneumaticSensorElement.ValueMode valueMode= PneumaticSensorElement.ValueMode.plus_mode;
    public PneumaticSensorElement.DirectionMode directionMode= PneumaticSensorElement.DirectionMode.a_mode;
    public PneumaticSensorElement.DisplayMode displayMode= PneumaticSensorElement.DisplayMode.atmosphere_mode;

    public double low_value=0;
    public double high_value=100;

    public PneumaticSensorGui(EntityPlayer player, PneumaticSensorRender render) {
        super(player, render, 8 + 72 + 24 + 4);
        this.render=render;
    }

    @Override
    public void initGui() {
        super.initGui();

        sensorModeButton = newGuiButton(8, 8, 56, "1");
        directionModeButton = newGuiButton(8, 8 + 24, 56, "2");
        valueModeButton = newGuiButton(8, 8 + 48, 56, "3");
        displayModeButton = newGuiButton(8, 8 + 72, 56, "4");

        validate = newGuiButton(70 + 22,8 + 48 + 12, 50, "validate");
        validate.setComment(0,"Validate and");
        validate.setComment(1,"apply entered");
        validate.setComment(2,"values");

        lowValue = newGuiTextField(70,8+4  + 24 + 12, 98);
        lowValue.setText(String.valueOf(render.low_value));
        lowValue.setComment(("Measured value\ncorresponding\nto 0% output").split("\n"));

        highValue = newGuiTextField(70,8+4+ 12, 98);
        highValue.setText(String.valueOf(render.high_value));
        highValue.setComment(("Measured value\ncorresponding\nto 100% output").split("\n"));

        sync_with_render();
    }

    public void sync_with_render() {

        sensorMode=render.sensorMode;
        valueMode=render.valueMode;
        directionMode=render.directionMode;
        displayMode=render.displayMode;
        low_value=render.low_value;
        high_value=render.high_value;

        if (sensorMode== PneumaticSensorElement.SensorMode.pressure_mode) {
            sensorModeButton.displayString="pressure";
            sensorModeButton.setComment(0,"Sensor will measure");
            sensorModeButton.setComment(1,"     pressure");
            if (directionMode== PneumaticSensorElement.DirectionMode.a_mode) {
                directionModeButton.displayString="P=P§e■";
                directionModeButton.setComment(0,"Pressure will be");
                directionModeButton.setComment(1,"measured from");
                directionModeButton.setComment(2,"§e■ side");
            } else if (directionMode== PneumaticSensorElement.DirectionMode.b_mode) {
                directionModeButton.displayString="P=P§a■";
                directionModeButton.setComment(0,"Pressure will be");
                directionModeButton.setComment(1,"measured from");
                directionModeButton.setComment(2,"§a■ side");
            } else if (directionMode== PneumaticSensorElement.DirectionMode.a_to_b_mode) {
                directionModeButton.displayString="ΔP=P§e■§f-P§a■";
                directionModeButton.setComment(0,"Pressure will be");
                directionModeButton.setComment(1,"measured as difference");
                directionModeButton.setComment(2,"between §e■ and §a■ side");
            } else {
                directionModeButton.displayString="ΔP=P§a■§f-P§e■§f";
                directionModeButton.setComment(0,"Pressure will be");
                directionModeButton.setComment(1,"measured as difference");
                directionModeButton.setComment(2,"between §a■ and §e■ side");
            }
        } else {
            sensorModeButton.displayString="speed";
            sensorModeButton.setComment(0,"Sensor will measure");
            sensorModeButton.setComment(1,"     air speed");
            if (directionMode== PneumaticSensorElement.DirectionMode.a_mode) {
                directionModeButton.displayString="V=V§e■";
                directionModeButton.setComment(0,"Air speed will");
                directionModeButton.setComment(1,"be measured from");
                directionModeButton.setComment(2,"§e■ side");
            } else if (directionMode== PneumaticSensorElement.DirectionMode.b_mode) {
                directionModeButton.displayString="V=V§a■";
                directionModeButton.setComment(0,"Air speed will");
                directionModeButton.setComment(1,"be measured from");
                directionModeButton.setComment(2,"§a■ side");
            } else if (directionMode== PneumaticSensorElement.DirectionMode.a_to_b_mode) {
                directionModeButton.displayString="V=§e■§f->§a■";
                directionModeButton.setComment(0,"Air speed will");
                directionModeButton.setComment(1,"be measured from");
                directionModeButton.setComment(2,"§e■ to §a■ side");
            } else {
                directionModeButton.displayString="V=§a■§f->§e■§f";
                directionModeButton.setComment(0,"Air speed will");
                directionModeButton.setComment(1,"be measured from");
                directionModeButton.setComment(2,"§a■ to §e■ side");
            }
        }

        if (valueMode== PneumaticSensorElement.ValueMode.plus_mode) {
            valueModeButton.displayString="+";
            valueModeButton.setComment(0,"Output value will be");
            valueModeButton.setComment(1,"left unchanged");
        } else if (valueMode== PneumaticSensorElement.ValueMode.minus_mode) {
            valueModeButton.displayString="-";
            valueModeButton.setComment(0,"Output value will be");
            valueModeButton.setComment(1,"multiplied by -1");
        } else {
            valueModeButton.displayString="abs";
            valueModeButton.setComment(0,"Output value will be");
            valueModeButton.setComment(1,"an absolute value");
        }

        if (displayMode==DisplayMode.pascal_mode) {
            displayModeButton.displayString="Pa";
            displayModeButton.setComment(0,"Entered pressure");
            displayModeButton.setComment(1,"will be interpreted");
            displayModeButton.setComment(2,"in pascals");
        } else if (displayMode==DisplayMode.kilo_pascal_mode) {
            displayModeButton.displayString="KPa";
            displayModeButton.setComment(0,"Entered pressure");
            displayModeButton.setComment(1,"will be interpreted");
            displayModeButton.setComment(2,"in kilo pascals");
        } else if (displayMode==DisplayMode.mega_pascal_mode) {
            displayModeButton.displayString="MPa";
            displayModeButton.setComment(0,"Entered pressure");
            displayModeButton.setComment(1,"will be interpreted");
            displayModeButton.setComment(2,"in mega pascals");
        } else if (displayMode==DisplayMode.atmosphere_mode) {
            displayModeButton.displayString="ATM";
            displayModeButton.setComment(0,"Entered pressure");
            displayModeButton.setComment(1,"will be interpreted");
            displayModeButton.setComment(2,"in atmospheres");
        } else if (displayMode==DisplayMode.meters_per_s) {
            displayModeButton.displayString="M/S";
            displayModeButton.setComment(0,"Entered air speed");
            displayModeButton.setComment(1,"will be interpreted");
            displayModeButton.setComment(2,"in meters per second");
        } else if (displayMode==DisplayMode.sm_per_s) {
            displayModeButton.displayString="sm/S";
            displayModeButton.setComment(0,"Entered air speed");
            displayModeButton.setComment(1,"will be interpreted");
            displayModeButton.setComment(2,"in centimeters per second");
        } else {
            displayModeButton.displayString="mm/S";
            displayModeButton.setComment(0,"Entered air speed");
            displayModeButton.setComment(1,"will be interpreted");
            displayModeButton.setComment(2,"in millimeters per second");
        }

        render.has_changes=false;
    }

    @Override
    public void guiObjectEvent(IGuiObject object) {
        super.guiObjectEvent(object);
        if (object==sensorModeButton) {
            render.clientSetByte(setSensorModeId,(byte)(sensorMode.cycle().get_value()));
        } else if (object==valueModeButton) {
            render.clientSetByte(setValueModeId,(byte)(valueMode.cycle().get_value()));
        } else if (object==directionModeButton) {
            render.clientSetByte(setDirectionModeId,(byte)(directionMode.cycle().get_value()));
        } else if (object==displayModeButton) {
            render.clientSetByte(setDisplayModeId,(byte)(displayMode.cycle().get_value()));
        } else if (object==validate) {
            double lowVoltage, highVoltage;

            try {
                lowVoltage = NumberFormat.getInstance().parse(lowValue.getText()).doubleValue();
                highVoltage = NumberFormat.getInstance().parse(highValue.getText()).doubleValue();
                render.clientSetDouble(PneumaticSensorElement.setValuesId, lowVoltage, highVoltage);
                validate.displayString="§avalidate";
            } catch (ParseException e) {
                validate.displayString="§cvalidate";
            }
        }
    }

    @Override
    public void preDraw(float f, int x, int y) {
        super.preDraw(f,x,y);
        if (render.has_changes) {
            sync_with_render();
        }
    }
}
