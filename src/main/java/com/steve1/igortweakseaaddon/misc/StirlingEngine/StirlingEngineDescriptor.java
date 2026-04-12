package com.steve1.igortweakseaaddon.misc.StirlingEngine;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorSimpleShaft.IgorSimpleShaftDescriptor;
import mods.eln.libs.kotlin.jvm.internal.Reflection;
import mods.eln.mechanical.SimpleShaftDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.EntityMetaTag;
import mods.eln.node.transparent.TransparentNodeElementRender;

public class StirlingEngineDescriptor extends IgorSimpleShaftDescriptor {
    public StirlingEngineDescriptor(String name, Obj3D model) {
        super(name, model, StirlingEngineElement.class, StirlingEngineRender.class, EntityMetaTag.Basic);
    }
}
