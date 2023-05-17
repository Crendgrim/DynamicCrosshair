package mod.crend.dynamiccrosshair.forge;

import mod.crend.autoyacl.ConfigScreenFactory;
import mod.crend.autoyacl.YaclHelper;
import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.config.Config;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(DynamicCrosshair.MOD_ID)
public class DynamicCrosshairForge {
    public DynamicCrosshairForge() { }

    public static void init() {
        DynamicCrosshair.init();
        if (YaclHelper.HAS_YACL) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(
                            (minecraft, screen) -> ConfigScreenFactory.makeScreen(Config.class, screen)
                    ));
        }
    }

    public static void registerApi(DynamicCrosshairApi api) {
        if (ModList.get().isLoaded(api.getModId())) {
            DynamicCrosshair.registerApi(api);
        }
    }
}
