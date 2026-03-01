package com.steve1.igortweakseaaddon.GridSensor;

import com.steve1.igortweakseaaddon.IgorGrid.IgorGridElement;
import com.steve1.igortweakseaaddon.IgorGrid.IgorGridLogicElement;
import com.steve1.igortweakseaaddon.LogicPort.LogicPortMaster;
import com.steve1.igortweakseaaddon.LogicPort.SlavePort;
import mods.eln.Eln;
import mods.eln.gridnode.GridDescriptor;
import mods.eln.gridnode.GridElement;
import mods.eln.i18n.I18N;
import mods.eln.item.ConfigCopyToolDescriptor;
import mods.eln.item.IConfigurable;
import mods.eln.misc.*;
import mods.eln.node.AutoAcceptInventoryProxy;
import mods.eln.node.NodeBase;
import mods.eln.node.six.SixNode;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.six.SixNodeElementInventory;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.mna.component.Resistor;
import mods.eln.sim.nbt.NbtElectricalGateOutputProcess;
import mods.eln.sim.nbt.NbtElectricalLoad;
import mods.eln.sim.process.destruct.VoltageStateWatchDog;
import mods.eln.sim.process.destruct.WorldExplosion;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import mods.eln.sixnode.electricaldatalogger.DataLogs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static mods.eln.misc.Direction.XN;


public class GridSensorElement extends IgorGridLogicElement implements IConfigurable {

    public GridSensorDescriptor descriptor;
    public NbtElectricalLoad loadA = new NbtElectricalLoad("aLoad");
    public NbtElectricalLoad loadB = new NbtElectricalLoad("bLoad");
    public GridSensorProcess slowProcess = new GridSensorProcess(this);

    public Resistor resistor;

    static final byte dirNone = 0, dirAB = 1, dirBA = 2;
    byte dirType = dirNone;
    public static final byte powerType = 0, currantType = 1, voltageType = 2;
    int typeOfSensor = voltageType;
    float lowValue = 0, highValue = 50;

    public static final byte setTypeOfSensorId = 1;
    public static final byte setValueId = 2;
    public static final byte setDirType = 3;

    public GridSensorElement(TransparentNode node, TransparentNodeDescriptor descriptor) {
        super(node,descriptor,32);
        this.descriptor = (GridSensorDescriptor) descriptor;

        attach_grid_load(XN.left(),loadA);
        attach_grid_load(XN.right(),loadB);

        port_interface.register_connection("output1",false);

        WorldExplosion exp = new WorldExplosion(this).cableExplosion();

        resistor = new Resistor(loadA, loadB);
        electricalLoadList.add(loadA);
        electricalLoadList.add(loadB);
        electricalComponentList.add(resistor);

        electricalProcessList.add(slowProcess);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        typeOfSensor = nbt.getByte("typeOfSensor");
        lowValue = nbt.getFloat("lowValue");
        highValue = nbt.getFloat("highValue");
        dirType = nbt.getByte("dirType");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("typeOfSensor", (byte) typeOfSensor);
        nbt.setFloat("lowValue", lowValue);
        nbt.setFloat("highValue", highValue);
        nbt.setByte("dirType", dirType);
    }

    @Override
    public String multiMeterString(Direction side) {
        double outv=0;
        if (port_interface.safe_to_get("output1")) {
            outv=port_interface.get_port_u("output1");
        }
        return Utils.plotVolt("Uin:", loadA.getU()) + Utils.plotVolt("Uout:",outv );
    }

    @Override
    public String thermoMeterString(Direction side) {
        return "";
    }

    @Override
    public Map<String, String> getWaila() {
        Map<String, String> info = new HashMap<String, String>();
        double outv=0;
        if (port_interface.safe_to_get("output1")) {
            outv=port_interface.get_port_u("output1");
        }
        info.put(I18N.tr("Output voltage"), Utils.plotVolt("", outv));
        if (Eln.wailaEasyMode) {
            switch (typeOfSensor) {
                case voltageType:
                    info.put(I18N.tr("Measured voltage"), Utils.plotVolt("", loadA.getU()));
                    break;

                case currantType:
                    info.put(I18N.tr("Measured current"), Utils.plotAmpere("", loadA.getI()));
                    break;

                case powerType:
                    info.put(I18N.tr("Measured power"), Utils.plotPower("", loadA.getU() * loadA.getI()));
                    break;
            }
        }
        return info;
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeByte(typeOfSensor);
            stream.writeFloat(lowValue);
            stream.writeFloat(highValue);
            stream.writeByte(dirType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        computeElectricalLoad();
        Eln.applySmallRs(loadA);
        Eln.applySmallRs(loadB);
    }

    public void computeElectricalLoad() {
        //if (!descriptor.voltageOnly)
        {
            cable.applyTo(resistor, 2);
        }
    }

    @Override
    public byte networkUnserialize(DataInputStream stream) {
        byte res =super.networkUnserialize(stream);
        try {
            switch (stream.readByte()) {
                case setTypeOfSensorId:
                    typeOfSensor = stream.readByte();
                    needPublish();
                    break;
                case setValueId:
                    lowValue = stream.readFloat();
                    highValue = stream.readFloat();
                    if (lowValue == highValue) highValue += 0.0001;
                    needPublish();
                    break;
                case setDirType:
                    dirType = stream.readByte();
                    needPublish();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public boolean hasGui() {
        return true;
    }

    @Override
    public void readConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        if(compound.hasKey("min"))
            lowValue = compound.getFloat("min");
        if(compound.hasKey("max"))
            highValue = compound.getFloat("max");
        if (lowValue == highValue) highValue += 0.0001;
        if(compound.hasKey("unit")) {
            switch (compound.getByte("unit")) {
                case DataLogs.powerType:
                    typeOfSensor = powerType;
                    break;
                case DataLogs.currentType:
                    typeOfSensor = currantType;
                    break;
                case DataLogs.voltageType:
                    typeOfSensor = voltageType;
                    break;
            }
        }
        if(compound.hasKey("dir")) {
            dirType = compound.getByte("dir");
        }
        reconnect();
    }

    @Override
    public void writeConfigTool(NBTTagCompound compound, EntityPlayer invoker) {
        compound.setFloat("min", lowValue);
        compound.setFloat("max", highValue);
        switch(typeOfSensor) {
            case powerType:
                compound.setByte("unit", DataLogs.powerType);
                break;
            case currantType:
                compound.setByte("unit", DataLogs.currentType);
                break;
            case voltageType:
                compound.setByte("unit", DataLogs.voltageType);
                break;
        }
        compound.setByte("dir", dirType);
    }
}
