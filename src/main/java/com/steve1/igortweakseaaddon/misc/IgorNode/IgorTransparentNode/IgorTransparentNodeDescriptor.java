package com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode;

import mods.eln.misc.Obj3D;
import mods.eln.misc.VoltageLevelColor;
import mods.eln.node.transparent.EntityMetaTag;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeElementRender;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;

public class IgorTransparentNodeDescriptor extends TransparentNodeDescriptor {
    public Obj3D model;
    public Obj3D.Obj3DPart model_main;
    public IgorTransparentNodeDescriptor(String name, Obj3D model, Class<? extends IgorTransparentNodeElement> ElementClass, Class<? extends IgorTransparentNodeElementRender> RenderClass, EntityMetaTag metattag) {
        super(name, ElementClass, RenderClass, metattag);
        this.model=model;
        if (model!=null) {
            model_main=get_main_part();
        } else {
            logger.error("Missing model for "+this.name);
        }
        voltageLevelColor=VoltageLevelColor.Neutral;
    }

    public Obj3D.Obj3DPart get_main_part() {
        return model.getPart("main");
    }

    public void draw_initial(TransparentNodeElementRender render) {
        if (model!=null) {
            model_main.draw();
            draw(render);
        }
    }

    public void draw(TransparentNodeElementRender render) {

    }
}
