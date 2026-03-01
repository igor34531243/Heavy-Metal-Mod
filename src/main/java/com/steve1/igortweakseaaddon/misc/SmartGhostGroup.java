package com.steve1.igortweakseaaddon.misc;

import com.steve1.igortweakseaaddon.LogicPort.SlavePort;
import mods.eln.Eln;
import mods.eln.ghost.GhostBlock;
import mods.eln.ghost.GhostGroup;
import mods.eln.misc.Coordonate;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.NodeBlock;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeBlock;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.*;
import static mods.eln.Eln.transparentNodeBlock;
import static mods.eln.Eln.transparentNodeItem;

public class SmartGhostGroup extends GhostGroup {

    public SmartGhostGroup() {
    }

    class SmartGhostGroupElement {

        int x, y, z;
        Block block;
        int meta;
        Boolean is_port,is_input;
        String port_name;

        public SmartGhostGroupElement(int x, int y, int z, Block block, int meta) {
            this(x,y,z,block,meta,false,"",false);
        }

        public SmartGhostGroupElement(int x, int y, int z, Block block, int meta, Boolean is_port, String port_name, Boolean is_input) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.block = block;
            this.meta = meta;
            this.is_port=is_port;
            this.port_name=port_name;
            this.is_input=is_input;
            logger.info(this);
        }

        @Override
        public String toString() {
            return "SmartGhostGroupElement: "+x+" "+y+" "+z+" "+block+" "+ meta +" "+is_port+" "+port_name+" "+is_input;
        }
    }

    ArrayList<SmartGhostGroupElement> nelementList = new ArrayList<SmartGhostGroupElement>();

    @Override
    public void addElement(int x, int y, int z) {
        if (x==0 && y ==0 && z==0) return;
        // 0 0 0  block has to be empty since it is the block that player palces
        // and it should already be present in the world by the time this is called
        // if we add 0 0 0 to there it will repalce the main multiblock block and everything will break very bad
        assert_if_present(x,y,z);
        nelementList.add(new SmartGhostGroupElement(x, y, z, Eln.ghostBlock, GhostBlock.tCube));
    }

    @Override
    public void addElement(int x, int y, int z, Block block, int meta) {
        if (x==0 && y ==0 && z==0) return;
        assert_if_present(x,y,z);
        nelementList.add(new SmartGhostGroupElement(x, y, z, block, meta));
    }

    public void addElement(int x, int y, int z, Block block, int meta, Boolean is_port, String port_name, Boolean is_input) {
        if (x==0 && y ==0 && z==0) return;
        assert_if_present(x,y,z);
        nelementList.add(new SmartGhostGroupElement(x, y, z, block, meta,is_port,port_name,is_input));
    }

    public void addElement(int x, int y, int z, String port_name, Boolean is_input) {
        if (x==0 && y ==0 && z==0) return;
        assert_if_present(x,y,z);
        addElement(x, y, z, transparentNodeBlock, logicPortDescriptor.damage_stored, true,port_name,is_input);
    }

    public void replaceElement(int x, int y, int z, Block block, int meta) {
        removeElement(x,y,z);
        addElement(x,y,z,block,meta);
    }

    public void replaceElement(int x, int y, int z, String port_name, Boolean is_input) {
        removeElement(x,y,z);
        addElement(x,y,z,port_name,is_input);
    }

    @Override
    public void removeElement(int x, int y, int z) {
        java.util.Iterator<SmartGhostGroupElement> i = nelementList.iterator();
        SmartGhostGroupElement g;

        while (i.hasNext()) {
            g = i.next();
            if (g.x == x && g.y == y && g.z == z) {
                i.remove();
            }
        }
    }

    public void assert_if_present(int x, int y, int z) {
        java.util.Iterator<SmartGhostGroupElement> i = nelementList.iterator();
        SmartGhostGroupElement g;

        while (i.hasNext()) {
            g = i.next();
            if (g.x == x && g.y == y && g.z == z) {
                throw new RuntimeException("Trying to add a block on top of already existing one in ghost group, use replaceElement instead");
            }
        }
    }

    @Override
    public void addRectangle(int x1, int x2, int y1, int y2, int z1, int z2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    addElement(x, y, z);
                }
            }
        }
    }

    @Override
    public boolean canBePloted(Coordonate c) {
        return canBePloted(c.world(), c.x, c.y, c.z);
    }

    @Override
    public boolean canBePloted(World world, int x, int y, int z) {
        for (SmartGhostGroupElement element : nelementList) {
            if (false == Eln.ghostManager.canCreateGhostAt(world, x + element.x, y + element.y, z + element.z))
                return false;
        }
        return true;
    }

    @Override
    public boolean plot(Coordonate coordonate, Coordonate observerCoordonate, int UUID) {
        if (canBePloted(coordonate.world(), coordonate.x, coordonate.y, coordonate.z) == false) return false;

        for (SmartGhostGroupElement element : nelementList) {
            Coordonate offsetCoordonate = coordonate.newWithOffset(element.x, element.y, element.z);
            if (element.block instanceof TransparentNodeBlock) {
                // this is my magic fix for electrical age not correctly handling
                // its own blocks in ghost group, dont touch this since it is very unstable
                // i will add ability to choose placement side later and maybe will add sixnode to this fix someday
                // for now you can only place transparentNodeBlocks through this group and only at fixed orientation
                // anything else(except for ghost blocks of course) will break badly so dont even try
                Eln.ghostManager.createGhost(offsetCoordonate, observerCoordonate, UUID, Blocks.stone, 0);
                World world=offsetCoordonate.world();
                EntityLivingBase player = null;
                int metadata=element.meta;
                int x=offsetCoordonate.x;
                int y=offsetCoordonate.y;
                int z=offsetCoordonate.z;
                ItemStack stack= new ItemStack(element.block,1,element.meta);
                if (world.isRemote) return false;
                TransparentNodeDescriptor descriptor = Eln.instance.transparentNodeItem.getDescriptor(element.meta);
                if (descriptor==null) {
                    throw new RuntimeException("descriptor fumbled here with meta: "+element.meta);
                }
                Direction front = Direction.XN;
                Direction direction=front;

                Block bb = world.getBlock(x, y, z);
                if (bb.isReplaceable(world, x, y, z)) ;

                Coordonate coord = new Coordonate(x, y, z, world);

                world.setBlockToAir(x, y, z);

                TransparentNode node = new TransparentNode();
                node.onBlockPlacedBy(coord, front, player, stack);

                world.setBlock(x, y, z, Block.getBlockFromItem(transparentNodeItem), element.meta, 0x03);//caca1.5.1
                ((NodeBlock) Block.getBlockFromItem(transparentNodeItem)).onBlockPlacedBy(world, x, y, z, direction, player, metadata);

                if (element.is_port) {
                    ((SlavePort)node.element).set_logic_values(element.port_name,element.is_input);
                }


            } else {
                Eln.ghostManager.createGhost(offsetCoordonate, observerCoordonate, UUID, element.block, element.meta);
            }
        }
        return true;
    }

    @Override
    public void erase(Coordonate observerCoordonate) {
        Eln.ghostManager.removeGhostAndBlockWithObserver(observerCoordonate);
    }

    @Override
    public void erase(Coordonate observerCoordonate, int uuid) {
        Eln.ghostManager.removeGhostAndBlockWithObserver(observerCoordonate, uuid);
    }

    @Override
    public void eraseGeo(Coordonate coordonate) {
        for (SmartGhostGroupElement element : nelementList) {
            Eln.ghostManager.removeGhostAndBlock(coordonate.newWithOffset(element.x, element.y, element.z));
        }
    }

    @Override
    public SmartGhostGroup newRotate(Direction dir) {
        SmartGhostGroup other = new SmartGhostGroup();
        for (SmartGhostGroupElement element : this.nelementList) {
            int x, y, z;
            switch (dir) {
                case XN:
                    x = element.x;
                    y = element.y;
                    z = element.z;
                    break;
                case XP:
                    x = -element.x;
                    y = element.y;
                    z = -element.z;
                    break;
                case ZN:
                    x = -element.z;
                    y = element.y;
                    z = element.x;
                    break;
                case ZP:
                    x = element.z;
                    y = element.y;
                    z = -element.x;
                    break;
                default:
                case YN:
                    x = -element.y;
                    y = element.x;
                    z = element.z;
                    break;
                case YP:
                    x = element.y;
                    y = -element.x;
                    z = element.z;
                    break;
            }
            other.addElement(x, y, z, element.block, element.meta, element.is_port, element.port_name,element.is_input);
        }

        return other;
    }

    @Override
    public SmartGhostGroup newRotate(Direction dir, LRDU front) {
        SmartGhostGroup g = newRotate(dir);
        return g;
    }

    @Override
    public int size() {
        return nelementList.size();
    }
}
