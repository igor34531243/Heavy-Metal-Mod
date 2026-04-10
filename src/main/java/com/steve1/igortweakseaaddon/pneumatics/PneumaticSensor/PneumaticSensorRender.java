package com.steve1.igortweakseaaddon.pneumatics.PneumaticSensor;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.WithPipeInventory.IgorSixNodeWithPipeInventoryElementRender;
import mods.eln.Eln;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeEntity;

import java.io.DataInputStream;
import java.io.IOException;

public class PneumaticSensorRender extends IgorSixNodeWithPipeInventoryElementRender {

    public PneumaticSensorDescriptor descriptor;

    public PneumaticSensorElement.SensorMode sensorMode= PneumaticSensorElement.SensorMode.pressure_mode;
    public PneumaticSensorElement.ValueMode valueMode= PneumaticSensorElement.ValueMode.plus_mode;
    public PneumaticSensorElement.DirectionMode directionMode= PneumaticSensorElement.DirectionMode.a_mode;
    public PneumaticSensorElement.DisplayMode displayMode= PneumaticSensorElement.DisplayMode.atmosphere_mode;

    public double low_value=0;
    public double high_value=100;

    public boolean has_changes=true;

    public PneumaticSensorRender(SixNodeEntity tileEntity, Direction side, SixNodeDescriptor got_descriptor) {
        super(tileEntity, side, got_descriptor);
        descriptor= (PneumaticSensorDescriptor) got_descriptor;
    }

    @Override
    public void publishUnserialize(DataInputStream stream) {
        super.publishUnserialize(stream);
        try {
            sensorMode=PneumaticSensorElement.SensorMode.get_from_stream(stream);
            valueMode=PneumaticSensorElement.ValueMode.get_from_stream(stream);
            directionMode=PneumaticSensorElement.DirectionMode.get_from_stream(stream);
            displayMode= PneumaticSensorElement.DisplayMode.get_from_stream(stream);
            low_value=stream.readDouble();
            high_value=stream.readDouble();
            has_changes=true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CableRenderDescriptor getCableRender(LRDU lrdu) {
        if (lrdu==front.left() || lrdu==front.right()) {
            return cable_render;
        }
        if (lrdu==front.inverse()) {
            return Eln.instance.stdCableRenderSignal;
        }
        return super.getCableRender(lrdu);
    }

    @Override
    public void draw() {
        super.draw();

        front.glRotateOnX();

        descriptor.draw();
    }
}
