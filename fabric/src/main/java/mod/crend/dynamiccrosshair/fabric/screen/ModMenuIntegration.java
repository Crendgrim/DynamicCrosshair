package mod.crend.dynamiccrosshair.fabric.screen;

import com.terraformersmc.modmenu.api.ModMenuApi;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public com.terraformersmc.modmenu.api.ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigHandler.CONFIG_STORE::makeScreen;
    }
}
