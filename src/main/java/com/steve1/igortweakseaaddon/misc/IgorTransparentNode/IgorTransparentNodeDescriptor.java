package com.steve1.igortweakseaaddon.misc.IgorTransparentNode;

import mods.eln.misc.Obj3D;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.igorTransparentMetatag;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class IgorTransparentNodeDescriptor extends TransparentNodeDescriptor {
    public Obj3D model;
    public String model_main_group="main";
    public IgorTransparentNodeDescriptor(String name, Obj3D model, Class<? extends IgorTransparentNodeElement> ElementClass, Class<? extends IgorTransparentNodeElementRender> RenderClass) {
        super(name, ElementClass, RenderClass, igorTransparentMetatag);
        this.model=model;
    }

    public void draw_initial(TransparentNodeElementRender render) {
        if (model!=null) {
            logger.info("drawing model: "+model_main_group);
            model.draw(model_main_group);
            draw(render);
        }
    }

    public void draw(TransparentNodeElementRender render) {

    }
}
