package mod.crend.dynamiccrosshair.forge;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(DynamicCrosshair.MOD_ID)
public class DynamicCrosshairForge {

    public void registerConfigurationScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, screen) -> ConfigHandler.getScreen(screen)
                ));
    }

    public DynamicCrosshairForge() {
        DynamicCrosshair.init();
        registerConfigurationScreen();
    }
}
