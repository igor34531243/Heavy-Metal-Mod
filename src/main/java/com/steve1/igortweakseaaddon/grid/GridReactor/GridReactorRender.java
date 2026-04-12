package com.steve1.igortweakseaaddon.grid.GridReactor;

import com.steve1.igortweakseaaddon.misc.IgorGrid.IgorGridRender;
import mods.eln.misc.Direction;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementInventory;
import mods.eln.node.transparent.TransparentNodeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.IOException;

public class GridReactorRender extends IgorGridRender {

    public GridReactorDescriptor descriptor;
    public int x;
    public int y;
    public int z;
    public World world;
    public mainsHum mains;
    public coreSaturation core;

    public double current;
    public static double mcur = 4.7;
    TransparentNodeElementInventory inventory = new TransparentNodeElementInventory(2, 64, this);

    public GridReactorRender(TransparentNodeEntity tileEntity, TransparentNodeDescriptor descriptor_input) {
        super(tileEntity, descriptor_input);
        this.descriptor = (GridReactorDescriptor) descriptor_input;
        x = tileEntity.xCoord;
        y = tileEntity.yCoord;
        z = tileEntity.zCoord;
        world=tileEntity.getWorldObj();
        descriptor=(GridReactorDescriptor)descriptor_input;
    }

    @Override
    public GuiScreen newGuiDraw(Direction side, EntityPlayer player) {

        return new GridReactorGui(player, inventory, this);
    }

    public void networkUnserialize(DataInputStream stream) {
        super.networkUnserialize(stream);
        try{
            current = stream.readDouble();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void draw(){
        super.draw();
        playsound();
    }

    public void playsound(){
        if ((core == null || core.isDonePlaying()) && current >= mcur/2) {
            core = new coreSaturation();
            Minecraft.getMinecraft().getSoundHandler().playSound(core);
        }
        if ((mains == null || mains.isDonePlaying()) && current >= 0.1) {
            mains = new mainsHum();
            Minecraft.getMinecraft().getSoundHandler().playSound(mains);
        }


    }

    public class mainsHum implements ITickableSound{
        private final ResourceLocation location = new ResourceLocation("igortweakseaaddon:reactor_mains");
        private boolean done = false;

        @Override public void update() {
            if (current < 0.1){
                this.done = true;
            }
        }

        @Override
        public boolean isDonePlaying() {
            return done;
        }

        @Override
        public ResourceLocation getPositionedSoundLocation() {
            return location;
        }

        @Override
        public boolean canRepeat() {
            return true;
        }

        @Override
        public int getRepeatDelay() {
            return 0;
        }

        @Override
        public float getVolume() {
            return  0.6f*(1.0f - (float) Math.exp(-6* current / mcur));
        }

        @Override
        public float getPitch() {
            return 0.8f + (float) (current / mcur) * 0.2f;
        }

        @Override public float getXPosF() { return (float)x + 0.5f; }
        @Override public float getYPosF() { return (float)y + 0.5f; }
        @Override public float getZPosF() { return (float)z + 0.5f; }

        @Override public AttenuationType getAttenuationType() { return AttenuationType.LINEAR; }
    }

    public class coreSaturation implements ITickableSound{
        private final ResourceLocation location = new ResourceLocation("igortweakseaaddon:reactor_core");
        private boolean done = false;

        @Override public void update() {
            if (current < mcur/2){
                this.done = true;
            }
        }

        @Override
        public boolean isDonePlaying() {
            return done;
        }

        @Override
        public ResourceLocation getPositionedSoundLocation() {
            return location;
        }

        @Override
        public boolean canRepeat() {
            return true;
        }

        @Override
        public int getRepeatDelay() {
            return 0;
        }

        @Override
        public float getVolume() {
            return 0.8f * (float) clamp(1.0 - Math.exp(-12*(current - mcur /2)/ mcur));
        }

        @Override
        public float getPitch() {
            return 0.8f + (float) (current / mcur) * 0.2f;
        }

        @Override public float getXPosF() { return (float)x + 0.5f; }
        @Override public float getYPosF() { return (float)y + 0.5f; }
        @Override public float getZPosF() { return (float)z + 0.5f; }

        @Override public AttenuationType getAttenuationType() { return AttenuationType.LINEAR; }
    }

    public double clamp(double value){
        if (value < 0){
            return 0;
        }else{
            return value;
        }
    }
}
