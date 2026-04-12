package com.steve1.igortweakseaaddon.misc.StirlingEngine;

import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorSimpleShaft.IgorSimpleShaftRender;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.mechanical.ShaftRender;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.node.transparent.TransparentNodeEntity;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.thermalCableRender;

public class StirlingEngineRender extends IgorSimpleShaftRender {
    public StirlingEngineRender(TransparentNodeEntity entity, TransparentNodeDescriptor desc) {
        super(entity, desc);
    }

    @Override
    public CableRenderDescriptor getCableRender(Direction side, LRDU lrdu) {
        if (side==front || side==front.getInverse()) {
            if (lrdu==LRDU.Down) {
                return thermalCableRender;
            }
        }
        return null;
    }
}
