package com.steve1.igortweakseaaddon.GridFuse;

import com.steve1.igortweakseaaddon.IgorGrid.IgorGridElement;
import mods.eln.gridnode.GridDescriptor;
import mods.eln.gridnode.GridElement;
import mods.eln.item.GenericItemUsingDamageDescriptorUpgrade;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.IProcess;
import mods.eln.sim.ThermalLoad;
import mods.eln.sim.mna.component.Resistor;
import mods.eln.sim.nbt.NbtElectricalLoad;
import mods.eln.sim.nbt.NbtThermalLoad;
import mods.eln.sim.process.destruct.VoltageStateWatchDog;
import mods.eln.sim.process.destruct.WorldExplosion;
import mods.eln.sim.process.heater.ElectricalLoadHeatThermalLoad;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.fuseBlown;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static mods.eln.misc.Direction.XN;

public class GridFuseElement extends IgorGridElement {

    public double temperature = 0.0;
    public GridFuseDescriptor desc;
    public NbtElectricalLoad loadA = new NbtElectricalLoad("loadA");
    public NbtElectricalLoad loadB = new NbtElectricalLoad("loadB");
    public Resistor fuseResistor = new Resistor(loadA, loadB);
    public GridFuseItem installedFuse = null;
    public double melting_progress=0;
    public double last_sent=0;

    public NbtThermalLoad thermalLoad = new NbtThermalLoad("thermal_laod");
    public ElectricalLoadHeatThermalLoad heater = new ElectricalLoadHeatThermalLoad(loadA,thermalLoad);
    public VoltageStateWatchDog voltageStateWatchDog = new VoltageStateWatchDog();

    World world;

    public double x = node.coordonate.x;
    public double y = node.coordonate.y;
    public double z = node.coordonate.z;

    public GridFuseElement(@NotNull TransparentNode transparentNode, @NotNull TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor, 32);
        this.desc = (GridFuseDescriptor)descriptor;

        attach_grid_load(XN,loadA);
        attach_grid_load(XN.getInverse(),loadB);

        cable.applyTo(loadA);
        cable.applyTo(loadB);
        cable.applyTo(thermalLoad);
        thermalLoad.C/=15;
        thermalLoad.Rp/=0.75;
        // 0.75 gets temps higher than in regular poles so if this triggers too much set to 0.8

        thermalLoadList.add(thermalLoad);
        electricalProcessList.add(heater);
        voltageStateWatchDog.set(loadA);
        voltageStateWatchDog.set(new WorldExplosion(this).cableExplosion());
        voltageStateWatchDog.setUNominal(cable.electricalNominalVoltage);
        voltageStateWatchDog.setUMaxMin(cable.electricalMaximalVoltage);
        slowProcessList.add(voltageStateWatchDog);

        electricalLoadList.add(loadA);
        electricalLoadList.add(loadB);
        electricalComponentList.add(fuseResistor);

        electricalComponentList.add(new Resistor(loadA, null).pullDown());
        electricalComponentList.add(new Resistor(loadB, null).pullDown());

        update_voltage_watchdog();
        refreshSwitchResistor();

        IProcess physicsProcess = new IProcess() {
            @Override
            public void process(double timeStep) {

                if (!legit_fuse()) {
                    temperature = 0.0;
                    melting_progress= 0;
                    return;
                }

                temperature = thermalLoad.getT();

                if (temperature < 0.0) temperature = 0.0;

                melting_progress=temperature/(cable.thermalWarmLimit*0.8);

                if (temperature > cable.thermalWarmLimit*0.8) { // need 0.8 here exactly, same value as regular fuses
                    burnOut();
                }

                double delta_melt=Math.abs(last_sent-melting_progress);

                if (delta_melt>0.05 || ((last_sent<0.7) ^ (melting_progress<0.7)) && delta_melt>0.01) {
                    last_sent=melting_progress;
                    needPublish();
                }
            }
        };

        electricalProcessList.add(physicsProcess);
    }

    public void update_voltage_watchdog() {
        if (installedFuse!=null) {
            voltageStateWatchDog.setUNominal(installedFuse.voltage_regular);
            voltageStateWatchDog.setUMaxMin(installedFuse.voltage_maximal);
        } else {
            voltageStateWatchDog.disable();
        }
    }

    public double getPredictedSteadyStateTemp() {
        double I = loadA.getI();
        return (I * I * loadA.getRs() * 2 * thermalLoad.Rp) / 20;
    }

    public boolean willItMelt() {
        double limit = cable.thermalWarmLimit * 0.8;
        return getPredictedSteadyStateTemp() > limit;
    }

    @Override
    public String multiMeterString(Direction side) {
        String str = Utils.plotVolt("  U in: ", loadA.getU()) +
                Utils.plotVolt("  U out:", loadB.getU());

        str +=Utils.plotAmpere("I: ", Math.abs(fuseResistor.getCurrent()));

        if (installedFuse == null) {
            str += EnumChatFormatting.RED + "No Fuse Installed"+ EnumChatFormatting.RESET;
        } else if (installedFuse == fuseBlown) {
            str += EnumChatFormatting.DARK_RED + "fuse is blown"+ EnumChatFormatting.RESET;
        } else {
            str += EnumChatFormatting.GREEN + "Fuse OK" + EnumChatFormatting.RESET;
        }

        return str;
    }

    @Override
    public String thermoMeterString(Direction side) {
        if (installedFuse!=null) {
            return "  "+ Utils.plotCelsius("T:",temperature) + "  " + Utils.plotCelsius("trip T:", cable.thermalWarmLimit * 0.8);
        } else {
            return "  "+ Utils.plotCelsius("T:",temperature);
        }
    }

    public void burnOut() {
        install_fuse(fuseBlown);
        melting_progress=0;
    }

    public boolean legit_fuse() {
        return !(installedFuse == fuseBlown) && installedFuse != null;
    }

    public void install_fuse(GridFuseItem new_fuse) {
        installedFuse=new_fuse;
        update_voltage_watchdog();
        refreshSwitchResistor();
        needPublish();
    }

    public void refreshSwitchResistor() {
        if (legit_fuse()) {
            cable.applyTo(fuseResistor);
        } else {
            fuseResistor.setR(Double.POSITIVE_INFINITY);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        try {
            super.readFromNBT(nbt);

            if (nbt.hasKey("installed_fuse")) {
                NBTTagCompound fuseCompound = nbt.getCompoundTag("installed_fuse");
                ItemStack fuseStack = ItemStack.loadItemStackFromNBT(fuseCompound);

                if (fuseStack != null) {
                    Object desc = GenericItemUsingDamageDescriptorUpgrade.getDescriptor(fuseStack);
                    if (desc instanceof GridFuseItem) {
                        install_fuse((GridFuseItem) desc);
                    }
                }
            } else {
                install_fuse(null);
            }

            if (nbt.hasKey("current_temperature")) {
                temperature = nbt.getDouble("current_temperature");
            } else {
                temperature=0;
            }

            refreshSwitchResistor();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        try {
            super.writeToNBT(nbt);
            if (this.installedFuse != null) {
                NBTTagCompound fuseCompound = new NBTTagCompound();
                ItemStack stack = this.installedFuse.newItemStack();
                if (stack != null) {
                    stack.writeToNBT(fuseCompound);
                    nbt.setTag("installed_fuse", fuseCompound);
                }
            }
            nbt.setDouble("current_temperature", temperature);
        } catch (Exception e) {;
            e.printStackTrace();
        }

    }

    @Override
    public boolean onBlockActivated(EntityPlayer player, Direction side, float vx, float vy, float vz) {
        Boolean res= super.onBlockActivated(player,side,vx,vy,vz);

        ItemStack itemInHand = player.getCurrentEquippedItem();
        GridFuseItem takenOutFuse = null;

        Object descriptorInHand = GenericItemUsingDamageDescriptorUpgrade.getDescriptor(itemInHand);

        if (itemInHand != null && descriptorInHand instanceof GridFuseItem) {
            takenOutFuse = this.installedFuse;
            install_fuse((GridFuseItem) descriptorInHand);
            itemInHand.stackSize--;
        }
        else if (itemInHand == null && this.installedFuse != null) {
            takenOutFuse = this.installedFuse;
            install_fuse(null);
        }

        if (takenOutFuse != null) {
            node.dropItem(takenOutFuse.newItemStack());
        }

        return res;
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            ItemStack stack = null;
            if (installedFuse != null) {
                stack = installedFuse.newItemStack();
            }
            Utils.serialiseItemStack(stream, stack);
            stream.writeDouble(melting_progress);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}