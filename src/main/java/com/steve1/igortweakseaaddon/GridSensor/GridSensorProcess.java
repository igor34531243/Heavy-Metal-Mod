package com.steve1.igortweakseaaddon.GridSensor;

import mods.eln.Eln;
import mods.eln.sim.IProcess;

public class GridSensorProcess implements IProcess {

    GridSensorElement sensor;

    public GridSensorProcess(GridSensorElement sensor) {
        this.sensor = sensor;
    }

    @Override
    public void process(double time) {
        if (sensor.typeOfSensor == sensor.voltageType) {
            setOutput(sensor.loadA.getU());
        } else if (sensor.typeOfSensor == sensor.currantType) {
            double output = 0;
            switch (sensor.dirType) {
                case GridSensorElement.dirNone:
                    output = Math.abs(sensor.resistor.getCurrent());
                    break;
                case GridSensorElement.dirAB:
                    output = (sensor.resistor.getCurrent());
                    break;
                case GridSensorElement.dirBA:
                    output = (-sensor.resistor.getCurrent());
                    break;
            }

            setOutput(output);
        } else if (sensor.typeOfSensor == sensor.powerType) {
            double output = 0;
            switch (sensor.dirType) {
                case GridSensorElement.dirNone:
                    output = Math.abs(sensor.resistor.getCurrent() * sensor.loadA.getU());
                    break;
                case GridSensorElement.dirAB:
                    output = (sensor.resistor.getCurrent() * sensor.loadA.getU());
                    break;
                case GridSensorElement.dirBA:
                    output = (-sensor.resistor.getCurrent() * sensor.loadA.getU());
                    break;
            }

            setOutput(output);
        }
    }

    void setOutput(double physical) {
        double U = (physical - sensor.lowValue) / (sensor.highValue - sensor.lowValue) * Eln.SVU;
        if (U > Eln.SVU) U = Eln.SVU;
        if (U < 0) U = 0;
        double outv=0;
        if (sensor.port_interface.safe_to_set("output1")) {
            sensor.port_interface.set_port_u("output1",U);
        }
    }
}
