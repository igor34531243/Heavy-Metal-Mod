package com.steve1.igortweakseaaddon.StirlingEngine;

import mods.eln.libs.kotlin.jvm.internal.Reflection;
import mods.eln.mechanical.SimpleShaftDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.EntityMetaTag;

public class StirlingEngineDescriptor extends SimpleShaftDescriptor {
    Obj3D model;
    public StirlingEngineDescriptor(String name, Obj3D model) {
        super(name, Reflection.getOrCreateKotlinClass(StirlingEngineElement.class), Reflection.getOrCreateKotlinClass(StirlingEngineRender.class), EntityMetaTag.Basic);
        this.model=model;
    }

    @Override
    public Obj3D getObj() {
        return model;
    }

    @Override
    public Obj3D.Obj3DPart[] getStatic() {
        return new Obj3D.Obj3DPart[] { model.getPart("static") };
    }

    @Override
    public Obj3D.Obj3DPart[] getRotating() {
        return new Obj3D.Obj3DPart[] { model.getPart("rotating") };
    }
}
