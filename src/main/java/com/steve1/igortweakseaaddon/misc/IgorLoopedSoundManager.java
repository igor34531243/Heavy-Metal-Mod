package com.steve1.igortweakseaaddon.misc;

import mods.eln.Eln;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityClientPlayerMP;

import java.util.HashSet;

public class IgorLoopedSoundManager {
    public float remaining = 0f;
    public HashSet<IgorLoopedSound> loops = new HashSet<>();

    public float updateInterval;

    public IgorLoopedSoundManager(float updateInterval) {
        this.updateInterval=updateInterval;
    }

    public IgorLoopedSoundManager() {
        this(0.5f);
    }

    public void add(IgorLoopedSound loop) {
        if (loop != null && loop.active) {
            loops.add(loop);
        }
    }

    public void dispose() {
        for (IgorLoopedSound loop : loops) {
            loop.active = false;
        }
    }

    public static double sqDistDelta(double cx, double cy, double cz, double px, double py, double pz) {
        return (cx - px) * (cx - px) + (cy - py) * (cy - py) + (cz - pz) * (cz - pz);
    }
    public void process(float deltaT) {
        remaining -= deltaT;
        if (remaining <= 0) {
            SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
            for (IgorLoopedSound loop : loops) {
                // add 0.5 to put the point in the center of the block making sounds
                double cx = loop.coord.x + 0.5;
                double cy = loop.coord.y + 0.5;
                double cz = loop.coord.z + 0.5;
                // get the player, and get the squared distance between the player and the block
                EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
                double distDeltaSquared = sqDistDelta(cx, cy, cz, player.posX, player.posY, player.posZ);
                // when comparing, compare distDeltaSquared to the square of the distance delta that you are trying to compare against.
                if (loop.getVolume() > 0 && loop.getPitch() > 0 && !soundHandler.isSoundPlaying(loop) && distDeltaSquared < Eln.maxSoundDistance * Eln.maxSoundDistance) {
                    try {
                        soundHandler.playSound(loop);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e);
                    }
                }
                if (distDeltaSquared >= Eln.maxSoundDistance * Eln.maxSoundDistance || loop.getVolume() == 0f || loop.getPitch() == 0f) {
                    try {
                        soundHandler.stopSound(loop);
                    }catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
            remaining = updateInterval;
        }
    }
}
