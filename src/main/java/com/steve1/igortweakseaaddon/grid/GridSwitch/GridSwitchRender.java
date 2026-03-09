package com.steve1.igortweakseaaddon.grid.GridSwitch;

import com.steve1.igortweakseaaddon.grid.IgorGrid.IgorGridRender;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;
import org.jetbrains.annotations.NotNull;

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
