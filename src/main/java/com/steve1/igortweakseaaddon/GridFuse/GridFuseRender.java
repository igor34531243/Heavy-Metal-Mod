package com.steve1.igortweakseaaddon.GridFuse;

import com.steve1.igortweakseaaddon.IgorGrid.IgorGridDescriptor;
import com.steve1.igortweakseaaddon.IgorGrid.IgorGridRender;
import mods.eln.generic.GenericItemUsingDamageDescriptor;
import mods.eln.gridnode.GridRender;
import mods.eln.item.ElectricalFuseDescriptor;
import mods.eln.misc.Utils;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.io.DataInputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.fuseBlown;

@SideOnly(Side.CLIENT)
public class GridFuseRender extends IgorGridRender {

    public GridFuseItem installedFuseClient = null;
    public GridFuseDescriptor descriptor;

    public int x;
    public int y;
    public int z;
    public World world;
    public Double melting_progress=0.0;

    public float threshold_for_buzzing = 0.7f;
    public FuseHumSound buzzer;
    private boolean QuedSoundStart= false;
    private boolean QuedBurnFuseEffect = false;
    public boolean fuse_blown_processed=true;
    public GridFuseItem blown_fuse_instance = fuseBlown;

    public GridFuseRender(@NotNull TransparentNodeEntity tileEntity, @NotNull TransparentNodeDescriptor descriptor_input) {
        super(tileEntity, descriptor_input);
        x = tileEntity.xCoord;
        y = tileEntity.yCoord;
        z = tileEntity.zCoord;
        world=tileEntity.getWorldObj();
        descriptor=(GridFuseDescriptor)descriptor_input;
    }

    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try {
            ItemStack stack = Utils.unserialiseItemStack(stream);
            melting_progress = stream.readDouble();

            Object desc = GenericItemUsingDamageDescriptor.getDescriptor(stack);
            if (desc instanceof GridFuseItem) {
                if (desc == blown_fuse_instance) {
                    if (!fuse_blown_processed && installedFuseClient != blown_fuse_instance && installedFuseClient!=null) {
                        QuedBurnFuseEffect=true;
                    }
                    fuse_blown_processed = true;
                } else {
                    QuedSoundStart=true;
                    fuse_blown_processed=false;
                }
                this.installedFuseClient = (GridFuseItem) desc;
            } else {
                this.installedFuseClient = null;
                fuse_blown_processed=false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start_sound() {
        if ((buzzer==null || buzzer.isDonePlaying()) && melting_progress>=threshold_for_buzzing) {
            buzzer=new FuseHumSound();
            Minecraft.getMinecraft().getSoundHandler().playSound(buzzer);
        }
    }

    @Override
    public void draw() {
        super.draw();
        if (QuedBurnFuseEffect) {
            burn_fuse_effect();
            QuedBurnFuseEffect=false;
        }
        if (QuedSoundStart) {
            start_sound();
            QuedSoundStart=false;
        }
    }

    public void burn_fuse_effect() {
        if (world==null) {
            return;
        }
        Minecraft.getMinecraft().theWorld.playSound(x, y, z, "igortweakseaaddon:fuse_release", 7.0F, 0.8F, false);
        if (!world.isRemote) {
            return;
        }

        for (int i = 0; i < 70; i++) {

            double motionX = (world.rand.nextDouble() - 0.5) * 0.4;
            double motionY = (world.rand.nextDouble()); // Sparks fly up
            double motionZ = (world.rand.nextDouble() - 0.5) * 0.4;

            double posx= x+ (world.rand.nextDouble()-0.5)*3 +0.5;
            double posy= y+ (world.rand.nextDouble())*9;
            double posz= z+ (world.rand.nextDouble()-0.5)*3 +0.5;

            world.spawnParticle("crit", posx, posy, posz, motionX, motionY, motionZ);
            if (i % 3 == 0) {
                world.spawnParticle("largesmoke", posx, posy, posz, 0.0D, 0.1D, 0.0D);
            }
        }
        fuse_blown_processed=true;
    }

    public class FuseHumSound implements ITickableSound {
        private final ResourceLocation location = new ResourceLocation("igortweakseaaddon:fuse_buzzing");
        private boolean done = false;

        @Override public void update() {
            if (melting_progress < threshold_for_buzzing  || installedFuseClient == blown_fuse_instance) {
                this.done = true;
            }
        }

        @Override public boolean isDonePlaying() { return done; }
        @Override public ResourceLocation getPositionedSoundLocation() { return location; }
        @Override public boolean canRepeat() { return true; }
        @Override public int getRepeatDelay() { return 0; }
        @Override public float getVolume() { return (float) ((melting_progress - 0.7f) / 0.3f); }
        @Override public float getPitch() { return (float) (0.8f + (melting_progress * 0.4f)); }
        @Override public float getXPosF() { return (float)x + 0.5f; }
        @Override public float getYPosF() { return (float)y + 0.5f; }
        @Override public float getZPosF() { return (float)z + 0.5f; }
        @Override public AttenuationType getAttenuationType() { return AttenuationType.LINEAR; }
    }
}
