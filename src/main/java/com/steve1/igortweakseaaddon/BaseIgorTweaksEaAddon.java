package com.steve1.igortweakseaaddon;
import com.steve1.igortweakseaaddon.GridFuse.GridFuseDescriptor;
import com.steve1.igortweakseaaddon.GridFuse.GridFuseItem;
import com.steve1.igortweakseaaddon.GridSensor.GridSensorDescriptor;
import com.steve1.igortweakseaaddon.GridSwitch.GridSwitchDescriptor;
import com.steve1.igortweakseaaddon.LogicPort.LogicPortDescriptor;
import com.steve1.igortweakseaaddon.misc.SmartGhostGroup;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import mods.eln.Eln;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.i18n.I18N;
import mods.eln.item.ElectricalFuseDescriptor;
import mods.eln.misc.Obj3D;
import mods.eln.misc.Obj3DFolder;
import mods.eln.misc.Utils;
import mods.eln.misc.VoltageLevelColor;
import mods.eln.sixnode.electricalcable.ElectricalCableDescriptor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static cpw.mods.fml.common.registry.GameRegistry.addRecipe;
import static mods.eln.Eln.*;
import static mods.eln.i18n.I18N.TR_NAME;

@Mod (modid = "igortweakseaaddon", name="Igor Tweaks Electrical Age addon", version = "1.0", dependencies = "required-after:Eln;")

public class BaseIgorTweaksEaAddon {
	public static CreativeTabs tabIgorTweaks;
	public static final Logger logger = LogManager.getLogger("MyElnAddon");

	public static GridFuseDescriptor fuseDescriptor;
	public static GridSwitchDescriptor switchDescriptor;
	public static GridSensorDescriptor sensorDescriptor;
	public static GridFuseItem fuseBlown;
	public static GridFuseItem fuseT1;
	public static GridFuseItem fuseT2;

	public static LogicPortDescriptor logicPortDescriptor;
	public static Obj3D testcube;

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		testcube=obj.getObj("TestCube");
		loadAllElnAddonModels();
		register_logic_port();
		register_fuses();
		register_grid_devices();
		register_recipes();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		// empty for now could be used later
	}


	public void register_recipes() {
        Method findItemStackMethod = null;
		Method addRecipeMethod = null;
        try {
            findItemStackMethod = Eln.class.getDeclaredMethod("findItemStack", String.class);
			addRecipeMethod = Eln.class.getDeclaredMethod("addRecipe", ItemStack.class, Object[].class);

			findItemStackMethod.setAccessible(true);
			addRecipeMethod.setAccessible(true);
		} catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }


        try {
			Object vhv_cable=findItemStackMethod.invoke(instance,"Very High Voltage Cable");
			Object iron_plate="plateIron";
			Object item_rubber="itemRubber";
			Object alloy_plate=findItemStackMethod.invoke(instance,"Alloy Plate");
			Object lead_plate="plateLead";
			Object lead_ingot="ingotLead";
			Object cinnabar=findItemStackMethod.invoke(instance,"Cinnabar Dust");

			Object output = fuseDescriptor.newItemStack();//findItemStackMethod.invoke(instance,"Grid_High_Voltage_Fuse");

			if (output == null || vhv_cable == null || item_rubber == null || alloy_plate==null || lead_plate==null) {
				throw new RuntimeException("Один из предметов ELN не найден! Проверьте названия строк.");
			}

			addRecipeMethod.invoke(instance, output,
					new Object[]{
							"RPR",
							"PCP",
							"PCP",
							'C', vhv_cable,
							'P', iron_plate,
							'R', item_rubber
					}
            );

			output = fuseT1.newItemStack();

			addRecipeMethod.invoke(instance, output,
					new Object[]{
							"RPR",
							"RAR",
							"RPR",
							'A', alloy_plate,
							'P', lead_plate,
							'R', item_rubber
					}
			);

			output = fuseT2.newItemStack();

			addRecipeMethod.invoke(instance, output,
					new Object[]{
							"RIR",
							"RCR",
							"RIR",
							'C', cinnabar,
							'I', lead_ingot,
							'R', item_rubber
					}
			);

        } catch (Exception e) {
			e.printStackTrace();
        }
    }

	public void register_fuses() {

		int id=98;
		int subId;

		subId=15;

		fuseT1 = new GridFuseItem("Fuse_For_T1_Power_Lines", VVU*4 , false);

		fuseT1.setDefaultIcon("fusefort1powerlines");

		sharedItem.addElement(subId + (id << 6), fuseT1);

		subId=16;

		fuseT2 = new GridFuseItem("Fuse_For_T2_Power_Lines", VVU*16 , false);

		fuseT2.setDefaultIcon("fusefort2powerlines");

		sharedItem.addElement(subId + (id << 6), fuseT2);

		subId=17;

		fuseBlown = new GridFuseItem("Fuse_Blown_For_Power_Lines", VVU*16 , true);

		fuseBlown.setDefaultIcon("fuseblownforpowerlines");

		sharedItem.addElement(subId + (id << 6), fuseBlown);

	}

	public void register_logic_port() {
		int id=130;
		int subId=20;

		logicPortDescriptor = new LogicPortDescriptor(
				"Logic Port",
				subId + (id << 6)
		);

		logicPortDescriptor.setDefaultIcon("logicport");

		transparentNodeItem.addDescriptor(subId + (id << 6), logicPortDescriptor);
	}

	public void register_grid_devices() {
		int id = 123;
		int subId;
		SmartGhostGroup g;

		subId = 9;

		fuseDescriptor = new GridFuseDescriptor(
				"Grid High Voltage Fuse",
				obj.getObj("GridBreaker")
		);

		g = new SmartGhostGroup();
		g.addRectangle(0,0,0,7,0,0);
		fuseDescriptor.setGhostGroup(g);

		fuseDescriptor.setDefaultIcon("gridhighvoltagefuse");

		transparentNodeItem.addDescriptor(subId + (id << 6), fuseDescriptor);

		subId = 10;

		switchDescriptor = new GridSwitchDescriptor(
				"Grid High Voltage Switch",
				obj.getObj("GridSwitch")
		);

		g = new SmartGhostGroup();
		g.addRectangle(0,2,0,2,-1,3);
		g.replaceElement(0, 0, 1, "input1", true);

		switchDescriptor.setGhostGroup(g);

		switchDescriptor.setDefaultIcon("gridswitch");

		transparentNodeItem.addDescriptor(subId + (id << 6), switchDescriptor);

		subId = 11;

		sensorDescriptor = new GridSensorDescriptor(
				"Grid Electrical Probe",
				obj.getObj("GridFuse")
		);

		g = new SmartGhostGroup();
		g.addRectangle(0,1,0,3,0,1);
		g.replaceElement(0, 0, 1, "output1", false);

		sensorDescriptor.setGhostGroup(g);

		sensorDescriptor.setDefaultIcon("gridsensor");

		transparentNodeItem.addDescriptor(subId + (id << 6), sensorDescriptor);
	}

	public void loadAllElnAddonModels() {
		// this is my magic bypass of eln model loading method
		// most of this is very unstable
		// so please dont touch this unless ABSOLUTELY needed to
		// (this could cause accidental separation of your head from the body)
		try {
			Method loadObj = Obj3DFolder.class.getDeclaredMethod(
					"loadObj",
					String.class
			);
			loadObj.setAccessible(true);
			Method loadModelsRecursive = Obj3DFolder.class.getDeclaredMethod(
					"loadModelsRecursive",
					File.class,
					Integer.class
			);
			loadModelsRecursive.setAccessible(true);
			CodeSource codeSource = BaseIgorTweaksEaAddon.class.getProtectionDomain().getCodeSource();
			if (codeSource != null) {
				String jarFilePath = codeSource.getLocation().getPath();
				if (jarFilePath.contains("!")) {
					jarFilePath = jarFilePath.substring(5, jarFilePath.indexOf("!"));
					JarFile jarFile = new JarFile(URLDecoder.decode(jarFilePath, "UTF-8"));
					Enumeration<JarEntry> entries = jarFile.entries();
					int modelCount = 0;
					while (entries.hasMoreElements()) {
						String filename = entries.nextElement().getName();
						if (filename.startsWith("assets/eln/model/") && filename.toLowerCase().endsWith(".obj")) {
							filename = filename.substring(filename.indexOf("/model/") + 7, filename.length());
							Utils.println(String.format("Loading model %03d '%s'", ++modelCount, filename));
							loadObj.invoke(instance.obj,filename);
						}
					}
				} else {
					Integer modelCount = 0;
					File modelFolder = new File(mods.eln.Eln.class.getResource("/assets/eln/model").toURI());
					if (modelFolder.isDirectory()) {
						loadModelsRecursive.invoke(instance.obj,modelFolder,modelCount);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
