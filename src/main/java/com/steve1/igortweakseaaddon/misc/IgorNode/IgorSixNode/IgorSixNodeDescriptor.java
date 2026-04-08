package com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode;

import mods.eln.misc.Direction;
import mods.eln.node.six.SixNodeDescriptor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Constructor;

public class IgorSixNodeDescriptor extends SixNodeDescriptor {
    public IgorSixNodeDescriptor(String name, Class ElementClass, Class RenderClass) {
        super(name, ElementClass, RenderClass);
    }

    public boolean has_gui() {
        return false;
    }

    public GuiScreen make_gui(Direction side, EntityPlayer player, IgorSixNodeElementRender render) {
        return null;
    }
}
