package com.steve1.igortweakseaaddon.misc.IgorTransparentNode;

import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.Component.PneumaticConnection;
import mods.eln.misc.Direction;
import mods.eln.misc.LRDU;
import mods.eln.node.NodeBase;
import mods.eln.node.NodeConnection;

import java.util.ArrayList;
import java.util.List;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.pneumatic_simulator;

public class IgorNodeConnection extends NodeConnection {

    public List<PneumaticConnection> PC;

    public IgorNodeConnection(NodeBase N1, Direction dir1, LRDU lrdu1, NodeBase N2, Direction dir2, LRDU lrdu2) {
        super(N1, dir1, lrdu1, N2, dir2, lrdu2);
        this.PC = new ArrayList<PneumaticConnection>();
    }

    public void destroy() {
        super.destroy();
        for(PneumaticConnection pc : PC) pneumatic_simulator.removePneumaticComponent(pc);
    }

    public void addConnection(PneumaticConnection pc) { PC.add(pc); }
}
