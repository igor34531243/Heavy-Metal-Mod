package com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorSimpleShaft;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNodeElement;
import mods.eln.mechanical.ShaftElement;
import mods.eln.mechanical.ShaftNetwork;
import mods.eln.misc.Direction;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.process.destruct.ShaftSpeedWatchdog;
import mods.eln.sim.process.destruct.WorldExplosion;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static mods.eln.mechanical.ShaftNetworkKt.createShaftWatchdog;

public abstract class IgorSimpleShaftElement extends IgorTransparentNodeElement implements ShaftElement {

    public ShaftSpeedWatchdog shaftWatchdog;

    public double shaft_mass=5;
    public ShaftNetwork shaft=new ShaftNetwork();
    public boolean is_destructing=false;

    public IgorSimpleShaftElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);

        shaftWatchdog=createShaftWatchdog(this);
        shaftWatchdog.set(new WorldExplosion(this).cableExplosion());

        slowProcessList.add(shaftWatchdog);
    }

    @Override
    public void initialize() {
        super.initialize();
        reconnect();
        double rads=shaft.getRads();
        shaft = new ShaftNetwork(this, Arrays.asList(getShaftConnectivity()).iterator());
        shaft.setRads(rads);
        Direction[] directions=getShaftConnectivity();
        for (Direction direction : directions) {
            shaft.connectShaft(this,direction);
        }
    }

    @Override
    public void networkSerialize(DataOutputStream stream) {
        super.networkSerialize(stream);
        try {
            stream.writeDouble(shaft.get_rads());
            node.lrduCubeMask.getTranslate(front.down()).serialize(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getShaftMass() {
        return shaft_mass;
    }

    @Override
    public @NotNull Direction[] getShaftConnectivity() {
        return new Direction[] { front.left(), front.right() };
    }

    @Override
    public @Nullable ShaftNetwork getShaft(@NotNull Direction dir) {
        return shaft;
    }

    public ShaftNetwork getShaft() {
        return shaft;
    }

    @Override
    public void setShaft(@NotNull Direction dir, @Nullable ShaftNetwork net) {
        shaft=net;
    }

    @Override
    public boolean isDestructing() {
        return is_destructing;
    }

    @Override
    public boolean isInternallyConnected(@NotNull Direction a, @NotNull Direction b) {
        return true;
    }

    @Override
    public void connectedOnSide(@NotNull Direction direction, @NotNull ShaftNetwork net) {

    }

    @Override
    public void disconnectedOnSide(@NotNull Direction direction, @Nullable ShaftNetwork net) {
        ;
    }

    @Override
    public void onBreakElement() {
        super.onBreakElement();
        is_destructing=true;
        Direction[] directions=getShaftConnectivity();
        for (Direction direction : directions) {
            shaft.disconnectShaft(this);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        shaft.readFromNBT(nbt,"shaft");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        shaft.writeToNBT(nbt,"shaft");
    }

    @Override
    public String multiMeterString(Direction side) {
        return Utils.plotER(shaft.getEnergy(), shaft.get_rads());
    }

    @Override
    public String thermoMeterString(Direction side) {
        return "";
    }
}
