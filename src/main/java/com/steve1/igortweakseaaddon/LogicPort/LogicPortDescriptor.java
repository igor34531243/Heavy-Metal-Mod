package com.steve1.igortweakseaaddon.LogicPort;

import mods.eln.misc.UtilsClient;
import mods.eln.node.six.SixNodeDescriptor;
import mods.eln.node.transparent.EntityMetaTag;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class LogicPortDescriptor extends TransparentNodeDescriptor {
    public int damage_stored;
    public LogicPortDescriptor(String name, int damage_arg) {
        super(name, LogicPortElement.class, LogicPortRender.class, EntityMetaTag.Basic);
        damage_stored=damage_arg;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
        if (getIcon() == null)
            return;

        voltageLevelColor.drawIconBackground(type);

        String icon = getIcon().getIconName().substring(4);
        UtilsClient.drawIcon(type, new ResourceLocation("eln", "textures/blocks/" + icon + ".png"));
    }

    @Override
    public boolean mustHaveFloor() {
        return false;
    }

}
