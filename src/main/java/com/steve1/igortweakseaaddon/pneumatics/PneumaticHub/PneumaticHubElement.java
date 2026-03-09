package com.steve1.igortweakseaaddon.pneumatics.PneumaticHub;

import com.steve1.igortweakseaaddon.misc.IgorTransparentNode.IgorTransparentNodeElement;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.transparent.TransparentNode;
import mods.eln.node.transparent.TransparentNodeDescriptor;
import mods.eln.sim.ElectricalLoad;
import mods.eln.sim.ThermalLoad;
import net.minecraft.entity.player.EntityPlayer;

public class PneumaticHubElement extends IgorTransparentNodeElement {
    public PneumaticHubElement(TransparentNode transparentNode, TransparentNodeDescriptor descriptor) {
        super(transparentNode, descriptor);
    }

    @Override
    public ElectricalLoad getElectricalLoad(Direction direction, LRDU lrdu) {
        return null;
    }

    @Override
    public ThermalLoad getThermalLoad(Direction direction, LRDU lrdu) {
        return null;
    }

    @Override
    public int getConnectionMask(Direction direction, LRDU lrdu) {
        return 0;
    }

    @Override
    public String multiMeterString(Direction direction) {
        return "";
    }

    @Override
    public String thermoMeterString(Direction direction) {
        return "";
    }

    @Override
    public void initialize() {

    }

    @Override
    public boolean onBlockActivated(EntityPlayer entityPlayer, Direction direction, float v, float v1, float v2) {
        return false;
    }
}
