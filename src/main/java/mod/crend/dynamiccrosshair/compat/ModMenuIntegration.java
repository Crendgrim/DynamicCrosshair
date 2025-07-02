//? if modmenu {
package mod.crend.dynamiccrosshair.compat;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import mod.crend.dynamiccrosshair.config.ConfigHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Entrypoint
@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigHandler.CONFIG_STORE::makeScreen;
    }
}
//?}