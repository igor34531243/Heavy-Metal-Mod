package com.steve1.igortweakseaaddon.misc.IgorTransparentNode;

import mods.eln.ghost.GhostGroup;
import mods.eln.misc.Coordonate;
import mods.eln.misc.Direction;
import mods.eln.misc.Utils;
import mods.eln.node.NodeBlock;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeItem;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class IgorTransparentNodeItem extends TransparentNodeItem {
    public IgorTransparentNodeItem(Block b) {
        super(b);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
        if (world.isRemote) return false;
        TransparentNodeDescriptor descriptor = getDescriptor(stack);
        Direction direction = Direction.fromIntMinecraftSide(side).getInverse();
        Direction front = descriptor.getFrontFromPlace(direction, player);
        int[] v = new int[]{descriptor.getSpawnDeltaX(), descriptor.getSpawnDeltaY(), descriptor.getSpawnDeltaZ()};
        front.rotateFromXN(v);
        x += v[0];
        y += v[1];
        z += v[2];

        Block bb = world.getBlock(x, y, z);
        if (bb.isReplaceable(world, x, y, z)) ;
        //if(world.getBlock(x, y, z) != Blocks.air) return false;

        Coordonate coord = new Coordonate(x, y, z, world);


        String error;
        if ((error = descriptor.checkCanPlace(coord, front)) != null) {
            Utils.addChatMessage(player, error);
            return false;
        }

        GhostGroup ghostgroup = descriptor.getGhostGroup(front);
        if (ghostgroup != null) ghostgroup.plot(coord, coord, descriptor.getGhostGroupUuid());

        TransparentNode node = new IgorTransparentNode();
        node.onBlockPlacedBy(coord, front, player, stack);

        world.setBlock(x, y, z, Block.getBlockFromItem(this), node.getBlockMetadata(), 0x03);//caca1.5.1
        ((NodeBlock) Block.getBlockFromItem(this)).onBlockPlacedBy(world, x, y, z, direction, player, metadata);


        node.checkCanStay(true);

        return true;

    }
}
