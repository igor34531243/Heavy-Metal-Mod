package com.steve1.igortweakseaaddon.grid.GridReactor;

import com.steve1.igortweakseaaddon.misc.IgorGrid.IgorGridDescriptor;
import mods.eln.Eln;
import mods.eln.item.FerromagneticCoreDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.misc.series.ISerie;
import mods.eln.sim.mna.misc.MnaConst;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.logger;
import static mods.eln.misc.Direction.XN;

public class GridReactorDescriptor extends IgorGridDescriptor {

    ISerie serie;

    public GridReactorDescriptor(String name, Obj3D obj, ISerie serie) {
        super(name, obj, GridReactorElement.class, GridReactorRender.class);
        add_cable_point(XN.left(),0,0,3,0);
        add_cable_point(XN.left(),1,1,3,0);
        add_cable_point(XN.right(),0,0,3,1);
        add_cable_point(XN.right(),1,1,3,1);

        //no clue wtf this is btw
        this.serie = serie;
        if(obj == null){
            logger.error("No grid reactor model given!");
        }
    }


    public double getlValue(int cableCount) {
        if (cableCount == 0) return 0;
        //logger.info(serie.getValue(cableCount - 1));
        return serie.getValue(cableCount - 1);
    }

    public double getlValue(IInventory inventory) {
        ItemStack core = inventory.getStackInSlot(GridReactorContainer.cableId);
        if (core == null) {
            return getlValue(0);
        }else {
            //logger.info(core.stackSize);
            return getlValue(core.stackSize);
        }
    }

    public double getRsValue(IInventory inventory) {
        ItemStack core = inventory.getStackInSlot(GridReactorContainer.coreId);

        if (core == null) return MnaConst.highImpedance;
        FerromagneticCoreDescriptor coreDescriptor = (FerromagneticCoreDescriptor) FerromagneticCoreDescriptor.getDescriptor(core);

        double coreFactor = coreDescriptor.cableMultiplicator;
        //logger.info(coreFactor);
        //logger.info(Eln.instance.lowVoltageCableDescriptor.electricalRs);
        return Eln.instance.lowVoltageCableDescriptor.electricalRs * coreFactor;
    }
}
