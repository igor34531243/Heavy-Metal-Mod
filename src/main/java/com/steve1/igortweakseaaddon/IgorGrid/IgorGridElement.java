package com.steve1.igortweakseaaddon.IgorGrid;

import com.steve1.igortweakseaaddon.LogicPort.LogicPortInterface;
import com.steve1.igortweakseaaddon.LogicPort.LogicPortMaster;
import com.steve1.igortweakseaaddon.LogicPort.SlavePort;
import mods.eln.Eln;
import mods.eln.gridnode.GridDescriptor;
import mods.eln.gridnode.GridElement;
import mods.eln.item.ElectricalFuseDescriptor;
import mods.eln.item.GenericItemUsingDamageDescriptorUpgrade;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class IgorGridElement extends GridElement {
    public static ElectricalCableDescriptor cable = Eln.instance.highVoltageCableDescriptor;
    public HashMap<Direction,ElectricalLoad> grid_loads=new HashMap<Direction,ElectricalLoad>();
    public HashMap<Direction,Vec3[]> cable_points;

    public IgorGridElement(@NotNull TransparentNode transparentNode, @NotNull TransparentNodeDescriptor descriptor, int connectRange) {
        super(transparentNode, descriptor, connectRange);
        cable_points=((IgorGridDescriptor)descriptor).cable_points;
    }

    public Direction get_local_dir(Direction base_direction, Direction global_direction) {
        switch (base_direction) {
            case XN: return global_direction;
            case ZP: return global_direction.left();
            case XP: return global_direction.getInverse();
            case ZN: return global_direction.right();
            default: return global_direction;
        }
    }

    @Override
    public Vec3 getCablePoint(Direction side, int i) {
        Direction local_side=get_local_dir(front,side);
        if (i >= 2 || i<0 || !cable_points.containsKey(local_side)) {
            return Vec3.createVectorHelper(0,0,0);
        }
        Vec3[] sidearr= cable_points.get(local_side);
        Vec3 vec=sidearr[i];
        return Vec3.createVectorHelper(vec.xCoord,vec.yCoord,vec.zCoord);
    }

    public void attach_grid_load(Direction side, ElectricalLoad load) {
        if (grid_loads.containsKey(side)) {
            logger.info("trying to attach gridload to the same side twice");
            return;
        }
        grid_loads.put(side,load);
    }

    @Override
    public @Nullable ElectricalLoad getGridElectricalLoad(@NotNull Direction side) {
        return grid_loads.get(get_local_dir(front,side));
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction side, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction side, LRDU lrdu) {
        return 0;
    }


    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        try {
            super.writeToNBT(nbt);
            this.front.writeToNBT(nbt, "front");
        } catch (Exception e) {;
            e.printStackTrace();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        try {
            super.readFromNBT(nbt);
            this.front = Direction.readFromNBT(nbt, "front");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
