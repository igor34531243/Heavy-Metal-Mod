package com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNodeElement;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.NBTPneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticLoad;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PressureWatchdog;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.NodeBase;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.process.destruct.WorldExplosion;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.pneumaticMask;
import static com.steve1.igortweakseaaddon.misc.igorUTILS.*;

public class PneumaticPipeElement extends IgorSixNodeElement {

    PneumaticPipeDescriptor descriptor;
    NBTPneumaticLoad pneumatic_load;
    PressureWatchdog pressure_watchdog;

    int color;
    int colorCare;

    public PneumaticPipeElement(SixNode sixNode, Direction side, SixNodeDescriptor descriptor) {
        super(sixNode, side, descriptor);
        this.descriptor= (PneumaticPipeDescriptor) descriptor;

        pneumatic_load= new NBTPneumaticLoad("pneumatic_load");
        ((PneumaticPipeDescriptor) descriptor).apply_to(pneumatic_load);
        pneumaticLoadList.add(pneumatic_load);

        pressure_watchdog=new PressureWatchdog();
        pressure_watchdog.set(new WorldExplosion(this).cableExplosion());
        pressure_watchdog.set(pneumatic_load);
        this.descriptor.apply_to(pressure_watchdog);

        slowProcessList.add(pressure_watchdog);

        color = 0;
        colorCare = 1;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        byte b = nbt.getByte("color");
        color = b & 0xF;
        colorCare = (b >> 4) & 1;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("color", (byte) (color | (colorCare << 4)));
    }

    @Override
    public PneumaticLoad getPneumaticLoad(Direction direction, LRDU lrdu) {
        return pneumatic_load;
    }

    @Override
    public ElectricalLoad getElectricalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(LRDU lrdu, int mask) {
        return null;
    }

    @Override
    public int getConnectionMask(LRDU lrdu) {
        return pneumaticMask | (descriptor.getMaskColor() << NodeBase.maskColorShift) | (descriptor.getMaskColorCare() << NodeBase.maskColorCareShift);
    }

    @Override
    public String multiMeterString() {
        return "Pressure: "+plot_pascals_atmospheres(pneumatic_load.get_pressure())+", Flow: "+plot_speed(pneumatic_load.get_speed());
    }

    @Override
    public String thermoMeterString() {
        return "";
    }
}
