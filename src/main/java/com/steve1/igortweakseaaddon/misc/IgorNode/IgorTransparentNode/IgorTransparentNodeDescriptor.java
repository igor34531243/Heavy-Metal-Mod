package com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode;

import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.EntityMetaTag;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;

public class IgorTransparentNodeDescriptor extends TransparentNodeDescriptor {
    public Obj3D model;
    public String model_main_group="main";
    public IgorTransparentNodeDescriptor(String name, Obj3D model, Class<? extends IgorTransparentNodeElement> ElementClass, Class<? extends IgorTransparentNodeElementRender> RenderClass, EntityMetaTag metattag) {
        super(name, ElementClass, RenderClass, metattag);
        this.model=model;
    }

    public void draw_initial(TransparentNodeElementRender render) {
        if (model!=null) {
            model.draw(model_main_group);
            draw(render);
        }
    }

    public void draw(TransparentNodeElementRender render) {

    }
}
