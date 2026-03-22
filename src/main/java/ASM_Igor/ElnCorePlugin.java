package ASM_Igor;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.TransformerExclusions({"ASM_igor"})
@IFMLLoadingPlugin.Name("Eln Patcher Core")
public class ElnCorePlugin implements IFMLLoadingPlugin {

    public static final boolean print_debug_igor_asm = false;

    @Override
    public String[] getASMTransformerClass() {
        if (print_debug_igor_asm) {
            System.out.println("started up node repalcement!!!");
        }
        return new String[]{ElnNodeTransformer.class.getName()};
    }

    @Override public String getModContainerClass() { return null; }
    @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> data) { }
    @Override public String getAccessTransformerClass() { return null; }
}