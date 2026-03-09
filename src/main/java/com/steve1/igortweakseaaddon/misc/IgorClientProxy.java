package com.steve1.igortweakseaaddon.misc;

import com.steve1.igortweakseaaddon.misc.IgorTransparentNode.IgorTransparentNodeEntity;
import com.steve1.igortweakseaaddon.misc.IgorTransparentNode.IgorTransparentNodeRender;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.steve1.igortweakseaaddon.BaseIgorTweaksEaAddon.igorTransparentNodeItem;

public class IgorClientProxy extends IgorCommonProxy{
    @Override
    public void registerRenderers() {
        try {

            Method bindTileEntitySpecialRenderer=ClientRegistry.class.getDeclaredMethod("bindTileEntitySpecialRenderer",Class.class, TileEntitySpecialRenderer.class);

            Method registerItemRenderer=MinecraftForgeClient.class.getDeclaredMethod("registerItemRenderer", Item.class, IItemRenderer.class);

            bindTileEntitySpecialRenderer.invoke(null,IgorTransparentNodeEntity.class, new IgorTransparentNodeRender());

            registerItemRenderer.invoke(null,igorTransparentNodeItem, igorTransparentNodeItem);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
