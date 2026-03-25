package com.steve1.igortweakseaaddon;
import com.steve1.igortweakseaaddon.grid.GridFuse.GridFuseDescriptor;
import com.steve1.igortweakseaaddon.grid.GridFuse.GridFuseItem;
import com.steve1.igortweakseaaddon.grid.GridSensor.GridSensorDescriptor;
import com.steve1.igortweakseaaddon.grid.GridSwitch.GridSwitchDescriptor;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorSixNode.IgorSixNode;
import com.steve1.igortweakseaaddon.misc.IgorNode.IgorTransparentNode.IgorTransparentNode;
import com.steve1.igortweakseaaddon.misc.LogicPort.LogicPortDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticHub.PneumaticHubDescriptor;
import com.steve1.igortweakseaaddon.misc.StirlingEngine.StirlingEngineDescriptor;
import com.steve1.igortweakseaaddon.misc.WirelessAlarm.WirelessAlarmDescriptor;
import com.steve1.igortweakseaaddon.misc.SmartGhostGroup;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticOutlet.PneumaticOutletDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticPipe.PneumaticPipeDescriptor;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSim.PneumaticSimulator;
import com.steve1.igortweakseaaddon.pneumatics.PneumaticSource.PneumaticSourceDescriptor;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.eln.Eln;
import mods.eln.Other;
import mods.eln.cable.CableRenderDescriptor;
import mods.eln.i18n.I18N;
import mods.eln.misc.Obj3D;
import mods.eln.misc.Obj3DFolder;
import mods.eln.misc.Utils;
import mods.eln.misc.VoltageLevelColor;
import mods.eln.node.NodeManager;
import mods.eln.node.simple.SimpleNodeItem;
import mods.eln.node.transparent.*;
import mods.eln.simplenode.energyconverter.EnergyConverterElnToOtherBlock;
import mods.eln.simplenode.energyconverter.EnergyConverterElnToOtherDescriptor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static mods.eln.Eln.*;
import static mods.eln.i18n.I18N.TR_NAME;

@Mod (modid = "igortweakseaaddon", name="Heavy Metal (Electrical Age Addon)", version = "1.0", dependencies = "required-after:Eln;")
public class BaseIgorTweaksEaAddon {
	public static CreativeTabs tabIgorTweaks;
	public static final Logger logger = LogManager.getLogger("MyElnAddon");

	public static GridFuseDescriptor fuseDescriptor;
	public static GridSwitchDescriptor switchDescriptor;
	public static GridSensorDescriptor sensorDescriptor;
	public static GridFuseItem fuseBlown;
	public static GridFuseItem fuseT1;
	public static GridFuseItem fuseT2;
	public static EnergyConverterElnToOtherBlock elnToOtherBlockVVu;
	public static WirelessAlarmDescriptor wirelessStandardAlarm;
	public static WirelessAlarmDescriptor wirelessNuclearAlarm;
	public static StirlingEngineDescriptor stirlingEngineDescriptor;
	public static PneumaticHubDescriptor pneumaticHubDescriptor;
	public static PneumaticOutletDescriptor pneumaticOutletDescriptor;
	public static PneumaticSourceDescriptor pneumaticSourceDescriptor;
	public static PneumaticPipeDescriptor smallPneumaticPipeDescriptor;

	public static LogicPortDescriptor logicPortDescriptor;
	public static Obj3D testcube;

	public static final double base_air_resistance = 10;
	public static final double base_atmospheric_pressure = 101325;
	public static final double small_pneumatic_resistance = base_air_resistance;
	public static final double small_pneumatic_area = 0.0003;
	public static final double small_pneumatic_volume = small_pneumatic_area*1;
	public static final double small_pneumatic_max_pressure = base_atmospheric_pressure * 20;
	public static final double t2_pneumatic_resistance = base_air_resistance;
	public static final double t2_pneumatic_area = 0.0003;
	public static final double t2_pneumatic_volume = t2_pneumatic_area*1;
	public static final double t2_pneumatic_max_pressure = base_atmospheric_pressure * 200;

	// plastic  20atm    0.0005 1.5 x 1.5 0.999
	// copper   200 atm  0.001  2.0 x 2.0 0.995
	// iron     400 atm  0.003  3.0 x 3.0 0.99
	// tungsten 600 atm  0.005  4.0 x 4.0 0.98
	// alloy    1000 atm 0.01   5.0 x 5.0 0.97

	public static PneumaticSimulator pneumatic_simulator;
	public static int pneumaticMask = (1<<13);

	public static final int pneumatic_steps_per_tick=10;

	@EventHandler
	public void preLoad(FMLPreInitializationEvent event)
	{
		initialize_mod();
		loadAllElnAddonModels();
		register_logic_port();
		register_fuses();
		register_grid_devices();
		register_recipes();
		register_energy_exporter();
		register_wireless_alarms();
		register_stirling_engine();
		register_pneumatics();
		register_pneumatic_pipes();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {

	}

	@EventHandler
	public void onServerStart(FMLServerAboutToStartEvent ev) {
		pneumatic_simulator.start();
	}

	@EventHandler
	public void onServerStopped(FMLServerStoppedEvent ev) {
		pneumatic_simulator.stop();
	}

	public void initialize_mod() {
		testcube=obj.getObj("TestCube");

		pneumatic_simulator=new PneumaticSimulator(0.05/pneumatic_steps_per_tick);

		NodeManager.registerUuid(sixNodeBlock.getNodeUuid(), IgorSixNode.class);

		NodeManager.registerUuid(transparentNodeBlock.getNodeUuid(), IgorTransparentNode.class);
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

			if (vhv_cable == null || alloy_plate==null || cinnabar==null) {
				throw new RuntimeException("One of the eln items for crafting is not found.");
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

	public void register_pneumatics() {
		int id = 133;
		int subId;

		subId = 0;

		pneumaticHubDescriptor = new PneumaticHubDescriptor("Pneumatic_Hub", obj.getObj("PneumaticHub"));

		pneumaticHubDescriptor.setDefaultIcon("pneumatichub");

		transparentNodeItem.addDescriptor(subId + (id << 6), pneumaticHubDescriptor);

		subId = 1;

		pneumaticOutletDescriptor = new PneumaticOutletDescriptor("Pneumatic_Outlet", obj.getObj("PneumaticOutlet"));

		pneumaticOutletDescriptor.setDefaultIcon("pneumaticoutlet");

		transparentNodeItem.addDescriptor(subId + (id << 6), pneumaticOutletDescriptor);

		subId = 2;

		pneumaticSourceDescriptor = new PneumaticSourceDescriptor("Pneumatic_Source", obj.getObj("PneumaticSource"));

		pneumaticSourceDescriptor.setDefaultIcon("pneumaticsource");

		transparentNodeItem.addDescriptor(subId + (id << 6), pneumaticSourceDescriptor);
	}

	public void register_pneumatic_pipes() {
		int id=134;
		int subId;

		subId = 0;

		CableRenderDescriptor cable_rend_desc;

		cable_rend_desc = new CableRenderDescriptor("eln",
				"sprites/cable.png", 1.5f, 1.5f);

		smallPneumaticPipeDescriptor = new PneumaticPipeDescriptor("Small Pneumatic Pipe", cable_rend_desc,0,1);

		smallPneumaticPipeDescriptor.set(small_pneumatic_resistance,small_pneumatic_area,small_pneumatic_volume,small_pneumatic_max_pressure);

		smallPneumaticPipeDescriptor.voltageLevelColor=VoltageLevelColor.Neutral;

		smallPneumaticPipeDescriptor.setDefaultIcon("smallpneumaticpipe");

		sixNodeItem.addDescriptor(subId + (id << 6), smallPneumaticPipeDescriptor);
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

	private void register_wireless_alarms() {
		int id =103;
		int subId, completId;
		String name;
		{
			subId = 2;
			name = TR_NAME(I18N.Type.NONE, "Wireless Nuclear Alarm");
			wirelessNuclearAlarm = new WirelessAlarmDescriptor(name,
					obj.getObj("alarmmedium"), 7, "eln:alarma", 11, 1f, wirelessTxRange);
			sixNodeItem.addDescriptor(subId + (id << 6), wirelessNuclearAlarm);
		}
		{
			subId = 3;
			name = TR_NAME(I18N.Type.NONE, "Wireless Standard Alarm");
			wirelessStandardAlarm = new WirelessAlarmDescriptor(name,
					obj.getObj("alarmmedium"), 7, "eln:smallalarm_critical",
					1.2, 2f, wirelessTxRange);
			sixNodeItem.addDescriptor(subId + (id << 6), wirelessStandardAlarm);
		}
	}

	public void register_stirling_engine() {
		int id =4;
		int subId, completId;
		String name;

		subId=22;


		name = TR_NAME(I18N.Type.NONE, "Stirling Engine");
		stirlingEngineDescriptor= new StirlingEngineDescriptor(name,obj.getObj("StirlingEngine"));

		stirlingEngineDescriptor.setDefaultIcon("stirlingengine");

		transparentNodeItem.addDescriptor(subId + (id << 6), stirlingEngineDescriptor);
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

	public void register_energy_exporter() {
		// another magic fix for energy converter, dont touch that
		ClassLoader loader = net.minecraft.launchwrapper.LaunchClassLoader.class.getClassLoader();
		try {
			//Class<?> block_clname= ReflectionHelper.getClass(loader,"net.minecraft.block.Block");
			Method registerBlock=GameRegistry.class.getDeclaredMethod(
					"registerBlock",
					net.minecraft.block.Block.class,
					Class.class,
					String.class);
			String blockName = TR_NAME(I18N.Type.TILE, "eln.EnergyConverterElnToOtherVVUBlock");
			EnergyConverterElnToOtherDescriptor.ElnDescriptor elnDesc = new EnergyConverterElnToOtherDescriptor.ElnDescriptor(VVU, Eln.instance.VVP());
			EnergyConverterElnToOtherDescriptor.Ic2Descriptor ic2Desc = new EnergyConverterElnToOtherDescriptor.Ic2Descriptor(512, 3);
			EnergyConverterElnToOtherDescriptor.OcDescriptor ocDesc = new EnergyConverterElnToOtherDescriptor.OcDescriptor(ic2Desc.outMax * Other.getElnToOcConversionRatio() / Other.getElnToIc2ConversionRatio());
			EnergyConverterElnToOtherDescriptor desc =
					new EnergyConverterElnToOtherDescriptor("EnergyConverterElnToOtherVVU", elnDesc, ic2Desc, ocDesc);
			elnToOtherBlockVVu = new EnergyConverterElnToOtherBlock(desc);
			elnToOtherBlockVVu.setCreativeTab(creativeTab).setBlockName(blockName);
			//GameRegistry.registerBlock(elnToOtherBlockVVu, SimpleNodeItem.class, blockName);
			registerBlock.invoke(null,elnToOtherBlockVVu, SimpleNodeItem.class, blockName);
		} catch (Exception e) {
			logger.error("failed to load energy exporter, here are logs:");
			e.printStackTrace();
		}
	}
}
