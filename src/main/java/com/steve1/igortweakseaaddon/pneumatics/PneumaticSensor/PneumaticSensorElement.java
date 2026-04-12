package com.steve1.igortweakseaaddon.pneumatics.PneumaticSensor;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeWithInventory.WithPipeInventory.IgorSixNodeWithPipeInventoryElement;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.PneumaticLoad;
import com.steve1.igortweakseaaddon.misc.PneumaticSim.Component.PressureWatchdog;
import mods.eln.Eln;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.NodeBase;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.nbt.NbtElectricalGateOutput;
import mods.eln.sim.nbt.NbtElectricalGateOutputProcess;
import mods.eln.sim.process.destruct.WorldExplosion;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;

public class PneumaticSensorElement extends IgorSixNodeWithPipeInventoryElement {

    public static final byte setSensorModeId=0;
    public static final byte setValueModeId=1;
    public static final byte setDirectionModeId=2;
    public static final byte setDisplayModeId=3;
    public static final byte setValuesId=4;

    public PressureWatchdog pressureWatchdogA=new PressureWatchdog();
    public PressureWatchdog pressureWatchdogB=new PressureWatchdog();
    public NBTPneumaticLoad loadA=new NBTPneumaticLoad("loadA");
    public NBTPneumaticLoad loadB=new NBTPneumaticLoad("loadB");
    public NBTPneumaticConnection pConnection=new NBTPneumaticConnection("pConnection");
    public NbtElectricalGateOutput gate = new NbtElectricalGateOutput("gate");
    public NbtElectricalGateOutputProcess gate_process=new NbtElectricalGateOutputProcess("gate_process",gate);
    public IProcess sensor_process;

    public SensorMode sensorMode=SensorMode.pressure_mode;
    public ValueMode valueMode=ValueMode.plus_mode;
    public DirectionMode directionMode=DirectionMode.a_mode;
    public DisplayMode displayMode=DisplayMode.pascal_mode;

    public double low_value_display=0;
    public double high_value_display=100;
    public double low_value=0;
    public double high_value=100;
    public double value_difference=100;
    public double current_value=0;

    public PneumaticSensorElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);

        pConnection.connect(loadA,loadB);
        pConnection.can_fall_asleep=false;

        pressureWatchdogA
                .set(loadA)
                .set(new WorldExplosion(this).cableExplosion());
        slowProcessList.add(pressureWatchdogA);

        pressureWatchdogB
                .set(loadB)
                .set(new WorldExplosion(this).cableExplosion());
        slowProcessList.add(pressureWatchdogB);

        pneumaticLoadList.add(loadA);
        pneumaticLoadList.add(loadB);
        pneumaticComponentList.add(pConnection);
        electricalLoadList.add(gate);
        electricalComponentList.add(gate_process);

        sensor_process= new IProcess() {
            @Override
            public void process(double time) {
                double measured;
                if (sensorMode==SensorMode.pressure_mode) {
                    if (directionMode==DirectionMode.a_mode) {
                        measured=loadA.pressure;
                    } else if (directionMode==DirectionMode.b_mode) {
                        measured=loadB.pressure;
                    } else if (directionMode==DirectionMode.a_to_b_mode) {
                        measured=loadB.pressure-loadA.pressure;
                    } else {
                        measured=loadA.pressure-loadB.pressure;
                    }
                } else {
                    if (directionMode==DirectionMode.a_mode) {
                        measured=loadA.get_speed();
                    } else if (directionMode==DirectionMode.b_mode) {
                        measured=loadB.get_speed();
                    } else if (directionMode==DirectionMode.a_to_b_mode) {
                        measured=pConnection.speed;
                    } else {
                        measured=-pConnection.speed;
                    }
                }
                if (valueMode==ValueMode.minus_mode) {
                    measured= -measured;
                } else if (valueMode==ValueMode.abs_mode) {
                    measured=Math.abs(measured);
                }

                measured-=low_value;
                measured/=value_difference;
                if (measured<0) {
                    measured=0;
                } else if (measured>1) {
                    measured=1;
                }

                current_value=measured;

                gate_process.setUSafe(current_value* Eln.SVU);
            }
        };
        pneumaticProcessList.add(sensor_process);

        recalibrate_values();
    }

    @Override
    public void pipe_descriptor_changed() {
        pipe_descriptor.apply_to(loadA);
        pipe_descriptor.apply_to(loadB);
        pipe_descriptor.apply_to(pConnection);
        pipe_descriptor.apply_to(pressureWatchdogA);
        pipe_descriptor.apply_to(pressureWatchdogB);
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            sensorMode.put_in_stream(stream);
            valueMode.put_in_stream(stream);
            directionMode.put_in_stream(stream);
            displayMode.put_in_stream(stream);
            stream.writeDouble(low_value_display);
            stream.writeDouble(high_value_display);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte igorNetworkUnserialize(DataInputStream stream) {
        byte res=super.igorNetworkUnserialize(stream);
        if (res==-128) {
            return -128;
        }
        try {
            switch (res) {
                case setSensorModeId:
                    sensorMode=SensorMode.get_from_stream(stream);
                    if (sensorMode==SensorMode.pressure_mode) {
                        displayMode=DisplayMode.pascal_mode;
                    } else {
                        displayMode=DisplayMode.meters_per_s;
                    }
                    break;
                case setValueModeId:
                    valueMode=ValueMode.get_from_stream(stream);
                    break;
                case setDirectionModeId:
                    directionMode=DirectionMode.get_from_stream(stream);
                    break;
                case setDisplayModeId:
                    displayMode=DisplayMode.get_from_stream(stream);
                    break;
                case setValuesId:
                    low_value_display=stream.readDouble();
                    high_value_display=stream.readDouble();
                    break;
                default:
                    return res;
            }
            recalibrate_values();
            needPublish();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -128;
    }

    public void recalibrate_values() {
        low_value=low_value_display;
        high_value=high_value_display;
        if (sensorMode==SensorMode.pressure_mode) {
            if (displayMode==DisplayMode.atmosphere_mode) {
                low_value *= base_atmospheric_pressure;
                high_value *= base_atmospheric_pressure;
            } else if (displayMode==DisplayMode.kilo_pascal_mode) {
                low_value *= 1000;
                high_value *= 1000;
            } else if (displayMode==DisplayMode.mega_pascal_mode) {
                low_value *= 1e6;
                high_value *= 1e6;
            } else if (displayMode!=DisplayMode.pascal_mode) {
                logger.error("Hit unexpected display mode for sensor with pressure!");
                displayMode=DisplayMode.pascal_mode;
                needPublish();
            }
        } else {
            if (displayMode==DisplayMode.sm_per_s) {
                low_value *= 0.01;
                high_value *= 0.01;
            } else if (displayMode==DisplayMode.mm_per_s) {
                low_value *= 0.001;
                high_value *= 0.001;
            } else if (displayMode!=DisplayMode.meters_per_s) {
                logger.error("Hit unexpected display mode for sensor with speed!");
                displayMode=DisplayMode.meters_per_s;
                needPublish();
            }
        }
        value_difference=high_value-low_value;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        sensorMode=SensorMode.get_from_nbt(nbt,"sensorMode");
        valueMode=ValueMode.get_from_nbt(nbt,"valueMode");
        directionMode=DirectionMode.get_from_nbt(nbt,"directionMode");
        displayMode=DisplayMode.get_from_nbt(nbt,"displayMode");
        low_value_display=nbt.getDouble("low_value");
        high_value_display=nbt.getDouble("high_value");
        current_value=nbt.getDouble("current_value");
        recalibrate_values();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        sensorMode.put_in_nbt(nbt,"sensorMode");
        valueMode.put_in_nbt(nbt,"valueMode");
        directionMode.put_in_nbt(nbt,"directionMode");
        displayMode.put_in_nbt(nbt,"displayMode");
        nbt.setDouble("low_value",low_value_display);
        nbt.setDouble("high_value",high_value_display);
        nbt.setDouble("current_value",current_value);
    }

    @Override
    public PneumaticLoad getPneumaticLoad(LRDU lrdu, int mask) {
        if (has_item && lrdu==front.left()) {
            return loadA;
        }
        if (has_item && lrdu==front.right()) {
            return loadB;
        }
        return null;
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        if (lrdu==front.inverse()) {
            return gate;
        }
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        if (has_item && (lrdu==front.left() || lrdu==front.right())) {
            return pneumaticMask;
        }
        if (lrdu==front.inverse()) {
            return NodeBase.maskElectricalOutputGate;
        }
        return 0;
    }

    @Override
    public String multiMeterString() {
        return "§ePA: "+plot_pascals_atmospheres(loadA.get_pressure())+"§f, §a PB: "+plot_pascals_atmospheres(loadB.get_pressure())+"§f, Flow: "+plot_speed(Math.abs(pConnection.get_speed()));
    }

    @Override
    public String thermoMeterString() {
        return "";
    }

    public enum SensorMode {
        pressure_mode(0),
        speed_mode(1);

        private final byte value;

        SensorMode(int value) {
            this.value= (byte) value;
        }

        public int get_value() {
            return value;
        }

        public static SensorMode get_from_value(int value) {
            switch (value) {
                case 0: {
                    return pressure_mode;
                }
                case 1: {
                    return speed_mode;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + value);
            }
        }

        public SensorMode cycle() {
            switch (value) {
                case 0: {
                    return speed_mode;
                }
                case 1: {
                    return pressure_mode;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + value);
            }
        }

        public void put_in_stream(DataOutputStream stream) throws IOException {
            stream.writeByte(value);
        }

        public static SensorMode get_from_stream(DataInputStream stream) throws IOException {
            return get_from_value(stream.readByte());
        }

        public void put_in_nbt(NBTTagCompound nbt,String name) {
            nbt.setByte(name,value);
        }

        public static SensorMode get_from_nbt(NBTTagCompound nbt,String name) {
            return get_from_value(nbt.getByte(name));
        }
    }

    public enum ValueMode {
        plus_mode(0),
        minus_mode(1),
        abs_mode(2);

        private final byte value;

        ValueMode(int value) {
            this.value= (byte) value;
        }

        public int get_value() {
            return value;
        }

        public static ValueMode get_from_value(int value) {
            switch (value) {
                case 0: {
                    return plus_mode;
                }
                case 1: {
                    return minus_mode;
                }
                case 2: {
                    return abs_mode;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + value);
            }
        }

        public ValueMode cycle() {
            switch (value) {
                case 0: {
                    return minus_mode;
                }
                case 1: {
                    return abs_mode;
                }
                case 2: {
                    return plus_mode;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + value);
            }
        }

        public void put_in_stream(DataOutputStream stream) throws IOException {
            stream.writeByte(value);
        }

        public static ValueMode get_from_stream(DataInputStream stream) throws IOException {
            return get_from_value(stream.readByte());
        }

        public void put_in_nbt(NBTTagCompound nbt,String name) {
            nbt.setByte(name,value);
        }

        public static ValueMode get_from_nbt(NBTTagCompound nbt,String name) {
            return get_from_value(nbt.getByte(name));
        }
    }

    public enum DirectionMode {
        a_mode(0),
        b_mode(1),
        a_to_b_mode(2),
        b_to_a_mode(3);

        private final byte value;

        DirectionMode(int value) {
            this.value= (byte) value;
        }

        public int get_value() {
            return value;
        }

        public static DirectionMode get_from_value(int value) {
            switch (value) {
                case 0: {
                    return a_mode;
                }
                case 1: {
                    return b_mode;
                }
                case 2: {
                    return a_to_b_mode;
                }
                case 3: {
                    return b_to_a_mode;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + value);
            }
        }

        public DirectionMode cycle() {
            switch (value) {
                case 0: {
                    return b_mode;
                }
                case 1: {
                    return a_to_b_mode;
                }
                case 2: {
                    return b_to_a_mode;
                }
                case 3: {
                    return a_mode;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + value);
            }
        }

        public void put_in_stream(DataOutputStream stream) throws IOException {
            stream.writeByte(value);
        }

        public static DirectionMode get_from_stream(DataInputStream stream) throws IOException {
            return get_from_value(stream.readByte());
        }

        public void put_in_nbt(NBTTagCompound nbt,String name) {
            nbt.setByte(name,value);
        }

        public static DirectionMode get_from_nbt(NBTTagCompound nbt,String name) {
            return get_from_value(nbt.getByte(name));
        }
    }

    public enum DisplayMode {
        pascal_mode(0),
        kilo_pascal_mode(1),
        mega_pascal_mode(2),
        atmosphere_mode(3),
        meters_per_s(4),
        sm_per_s(5),
        mm_per_s(6);

        private final byte value;

        DisplayMode(int value) {
            this.value= (byte) value;
        }

        public int get_value() {
            return value;
        }

        public static DisplayMode get_from_value(int value) {
            switch (value) {
                case 0: {
                    return pascal_mode;
                }
                case 1: {
                    return kilo_pascal_mode;
                }
                case 2: {
                    return mega_pascal_mode;
                }
                case 3: {
                    return atmosphere_mode;
                }
                case 4: {
                    return meters_per_s;
                }
                case 5: {
                    return sm_per_s;
                }
                case 6: {
                    return mm_per_s;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + value);
            }
        }

        public DisplayMode cycle() {
            switch (value) {
                case 0: {
                    return kilo_pascal_mode;
                }
                case 1: {
                    return mega_pascal_mode;
                }
                case 2: {
                    return atmosphere_mode;
                }
                case 3: {
                    return pascal_mode;
                }
                case 4: {
                    return sm_per_s;
                }
                case 5: {
                    return mm_per_s;
                }
                case 6: {
                    return meters_per_s;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + value);
            }
        }

        public void put_in_stream(DataOutputStream stream) throws IOException {
            stream.writeByte(value);
        }

        public static DisplayMode get_from_stream(DataInputStream stream) throws IOException {
            return get_from_value(stream.readByte());
        }

        public void put_in_nbt(NBTTagCompound nbt,String name) {
            nbt.setByte(name,value);
        }

        public static DisplayMode get_from_nbt(NBTTagCompound nbt,String name) {
            return get_from_value(nbt.getByte(name));
        }
    }
}
