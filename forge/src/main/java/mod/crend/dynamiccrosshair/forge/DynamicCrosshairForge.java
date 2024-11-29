package mod.crend.dynamiccrosshair.forge;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import mod.crend.libbamboo.forge.ConfigScreen;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
//? if >=1.21.1
/*import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;*/

@Mod(DynamicCrosshair.MOD_ID)
public class DynamicCrosshairForge {
    public DynamicCrosshairForge(/*? if >=1.21.1 {*//*FMLJavaModLoadingContext context*//*?}*/) {
        ConfigScreen.register(
                //? if <1.21.1 {
                ModLoadingContext.get(),
                //?} else
                /*context,*/
                () -> ConfigHandler.CONFIG_STORE
        );
    }

    public static void registerApi(DynamicCrosshairApi api) {
        if (ModList.get().isLoaded(api.getModId())) {
            DynamicCrosshairMod.registerApi(api);
        }
    }
}
