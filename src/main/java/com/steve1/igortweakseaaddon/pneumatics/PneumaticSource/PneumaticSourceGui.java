package com.steve1.igortweakseaaddon.pneumatics.PneumaticSource;

import mods.eln.gui.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;

import static mods.eln.i18n.I18N.tr;

public class PneumaticSourceGui extends GuiScreenEln {

    PneumaticSourceRender render;
    GuiTextFieldEln pressure;

    public PneumaticSourceGui(PneumaticSourceRender render) {
        this.render = render;
    }

    @Override
    protected GuiHelper newHelper() {
        return new GuiHelper(this, 50 + 12, 12 + 12);
    }

    @Override
    public void initGui() {
        super.initGui();

        pressure = newGuiTextField(6, 6, 50);
        pressure.setText((float) render.pressure);
        pressure.setObserver(this);
        pressure.setComment(new String[]{tr("Output pressure(Pa)")});
    }

    @Override
    public void textFieldNewValue(GuiTextFieldEln textField, String value) {
        float newPressure;

        try {
            newPressure = NumberFormat.getInstance().parse(pressure.getText()).floatValue();
        } catch (ParseException e) {
            return;
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(bos);

            render.preparePacketForServer(stream);

            stream.writeByte(PneumaticSourceRender.setPressureId);
            stream.writeFloat(newPressure);

            render.sendPacketToServer(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
