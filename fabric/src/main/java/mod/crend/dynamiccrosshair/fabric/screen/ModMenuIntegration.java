package mod.crend.dynamiccrosshair.fabric.screen;

import com.terraformersmc.modmenu.api.ModMenuApi;
import mod.crend.autoyacl.ConfigScreenFactory;
import mod.crend.dynamiccrosshair.config.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    public static Screen getScreen(Screen parent) {
        return ConfigScreenFactory.makeScreen(Config.class, parent);
    }

    @Override
    public com.terraformersmc.modmenu.api.ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuIntegration::getScreen;
    }
}
