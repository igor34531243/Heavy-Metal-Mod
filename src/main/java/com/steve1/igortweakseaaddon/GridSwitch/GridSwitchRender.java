package com.steve1.igortweakseaaddon.GridSwitch;

import com.steve1.igortweakseaaddon.IgorGrid.IgorGridRender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.eln.generic.GenericItemUsingDamageDescriptor;
import mods.eln.gridnode.GridRender;
import mods.eln.item.ElectricalFuseDescriptor;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GridSwitchRender extends IgorGridRender {

    public boolean is_open=false;

    public GridSwitchRender(@NotNull TransparentNodeEntity tileEntity, @NotNull TransparentNodeDescriptor descriptor_input) {
        super(tileEntity, descriptor_input);
    }

    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            is_open=stream.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
