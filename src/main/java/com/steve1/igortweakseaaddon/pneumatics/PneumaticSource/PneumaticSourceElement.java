package com.steve1.igortweakseaaddon.pneumatics.PneumaticSource;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeElement;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTConstantPressureLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticConnection;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;
import static com.steve1.igortweakseaaddon.pneumatics.PneumaticSource.PneumaticSourceRender.setPressureId;

public class PneumaticSourceElement extends IgorTransparentNodeElement {
    public NBTPneumaticLoad pneumatic_load;
    public NBTConstantPressureLoad constant_pressure_load;
    public NBTPneumaticConnection pneumatic_connection;
    public double constant_pressure;

    public PneumaticSourceElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
        constant_pressure= base_atmospheric_pressure;
        pneumatic_load=new NBTPneumaticLoad("pneumatic_load");
        creativePneumaticPipeDescriptor.apply_to(pneumatic_load);
        pneumaticLoadList.add(pneumatic_load);
        constant_pressure_load=new NBTConstantPressureLoad("const_load",constant_pressure);
        creativePneumaticPipeDescriptor.apply_to(constant_pressure_load);
        pneumaticLoadList.add(constant_pressure_load);
        pneumatic_connection=new NBTPneumaticConnection("pneumatic_connection",pneumatic_load,constant_pressure_load);
        creativePneumaticPipeDescriptor.apply_to(pneumatic_connection);
        pneumatic_load.set_pressure(base_atmospheric_pressure);
        pneumaticComponentList.add(pneumatic_connection);
    }

    public void set_new_pressure(double pressure) {
        constant_pressure=pressure;
        constant_pressure_load.set_constant_pressure(constant_pressure);
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeDouble(constant_pressure);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte networkUnserialize(DataInputStream stream) {
        byte pbyte=super.networkUnserialize(stream);
        if (pbyte==-128) {
            return pbyte;
        }
        try {
            switch (pbyte) {
                case setPressureId:
                    set_new_pressure(stream.readFloat());
                    needPublish();
                    return -128;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pbyte;
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction direction, LRDU lrdu) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction direction, LRDU lrdu) {
        return null;
    }

    @Override
    public PneumaticLoad getPneumaticLoad(Direction direction, LRDU lrdu) {
        return pneumatic_load;
    }

    @Override
    public int getConnectionMask(Direction direction, LRDU lrdu) {
        return pneumaticMask;
    }

    @Override
    public String multiMeterString(Direction direction) {
        return "Pressure: "+plot_pascals(pneumatic_load.get_pressure())+" "+plot_atmospheres(pneumatic_load.get_pressure())+", Flow: "+plot_speed(pneumatic_load.get_speed());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        set_new_pressure(nbt.getDouble("const_pressure"));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setDouble("const_pressure",constant_pressure);
    }

    @Override
    public String thermoMeterString(Direction direction) {
        return "";
    }

    @Override
    public boolean hasGui() {
        return true;
    }
}
