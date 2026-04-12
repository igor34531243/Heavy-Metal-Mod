package com.steve1.igortweakseaaddon.misc;

import mods.eln.misc.Coordonate;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class IgorLoopedSound implements ITickableSound {

    public String sample;
    public Coordonate coord;
    public AttenuationType attentuationType;
    public boolean active = true;

    public IgorLoopedSound(@NotNull String sample, @NotNull Coordonate coord, @NotNull AttenuationType attentuationType) {
        this.sample=sample;
        this.coord=coord;
        this.attentuationType=attentuationType;
    }

    public IgorLoopedSound(@NotNull String sample, @NotNull Coordonate coord) {
        this(sample, coord, ISound.AttenuationType.LINEAR);
    }

    @Override
    public boolean isDonePlaying() {
        return !active;
    }

    @Override
    @NotNull
    public AttenuationType getAttenuationType() {
        return attentuationType;
    }

    @Override
    public @NotNull ResourceLocation getPositionedSoundLocation() {
        return new ResourceLocation(sample);
    }

    @Override
    public float getXPosF() {
        return coord.x + 0.5f;
    }

    @Override
    public float getYPosF() {
        return coord.y + 0.5f;
    }

    @Override
    public float getZPosF() {
        return coord.z + 0.5f;
    }

    @Override
    public boolean canRepeat() {
        return true;
    }

    @Override
    public float getPitch() {
        return 1;
    }

    @Override
    public float getVolume() {
        return 1;
    }

    @Override
    public int getRepeatDelay() {
        return 0;
    }

    @Override
    public void update() {

    }
}
