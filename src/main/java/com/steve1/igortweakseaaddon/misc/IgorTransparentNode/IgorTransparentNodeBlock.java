package com.steve1.igortweakseaaddon.misc.IgorTransparentNode;

import mods.eln.Eln;
import mods.eln.node.transparent.EntityMetaTag;
import mods.eln.node.transparent.TransparentNodeBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.igorTransparentNodeItem;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class IgorTransparentNodeBlock extends TransparentNodeBlock {
    public IgorTransparentNodeBlock(Material material, Class tileEntityClass) {
        super(material, tileEntityClass);
    }

    public void getSubBlocks(Item par1, CreativeTabs tab, List subItems) {
        igorTransparentNodeItem.getSubItems(par1, tab, subItems);
    }

    public String getNodeUuid() {
        return "it";
    }
}
