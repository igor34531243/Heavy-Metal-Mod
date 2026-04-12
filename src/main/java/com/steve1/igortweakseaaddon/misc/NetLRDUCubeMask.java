package com.steve1.igortweakseaaddon.misc;

import mods.eln.misc.LRDUCubeMask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class NetLRDUCubeMask {
    public static void serialize(LRDUCubeMask cube_mask,DataOutputStream stream) throws IOException {
        int num=0;
        for (int ids=0; ids<6; ids++) {
            num|=((cube_mask.lrduMaskArray[ids].mask & 0xf) << (ids*4));
        }
        stream.writeInt(num);
    }

    public static LRDUCubeMask deserialize(DataInputStream stream) throws IOException {
        LRDUCubeMask cube_mask=new LRDUCubeMask();
        int num=0;
        num=stream.readInt();
        for (int ids=0; ids<6; ids++) {
            cube_mask.lrduMaskArray[ids].mask=(num >> (ids*4)) & 0xf;
        }
        return cube_mask;
    }
}
