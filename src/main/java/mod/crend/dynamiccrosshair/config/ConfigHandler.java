package mod.crend.dynamiccrosshair.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.client.gui.screen.Screen;

public class ConfigHandler {
    public static Screen getScreen(Screen parent) {
        return AutoConfig.getConfigScreen(Config.class, parent).get();
    }

    Config config;

    public ConfigHandler() {
        AutoConfig.register(Config.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(Config.class).getConfig();
    }

    public boolean isDynamicCrosshair() { return config.dynamicCrosshair; }
    public boolean isHideWithScreen() { return config.hideWithScreen; }
    public InteractableCrosshairPolicy dynamicCrosshairOnBlock() { return config.crosshairConfig.onBlock; }
    public boolean dynamicCrosshairOnEntity() { return config.crosshairConfig.onEntity; }
    public CrosshairPolicy dynamicCrosshairHoldingTool() { return config.crosshairConfig.holdingTool; }
    public boolean dynamicCrosshairHoldingMeleeWeapon() { return config.crosshairConfig.holdingMeleeWeapon; }
    public RangedCrosshairPolicy dynamicCrosshairHoldingRangedWeapon() { return config.crosshairConfig.holdingRangedWeapon; }
    public CrosshairPolicy dynamicCrosshairHoldingThrowable() { return config.crosshairConfig.holdingThrowable; }
    public boolean dynamicCrosshairHoldingShield() { return config.crosshairConfig.holdingShield; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingBlock() { return config.crosshairConfig.holdingBlock; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingUsableItem() { return config.crosshairConfig.holdingUsableItem; }


    public CrosshairColor getCrosshairColor() { return config.color.crosshairColor; }
    public int getCustomColor() { return config.color.customColor; }
    public boolean isForceColor() { return config.color.forceColor; }

    public boolean isDynamicCrosshairStyle() { return config.dynamicCrosshairStyle; }

    public CrosshairStyle getCrosshairStyleRegular() { return config.crosshairStyle.regular; }
    public CrosshairStyle getCrosshairStyleOnBlock() { return config.crosshairStyle.onBlock; }
    public CrosshairStyle getCrosshairStyleOnEntity() { return config.crosshairStyle.onEntity; }
    public CrosshairStyle getCrosshairStyleHoldingTool() { return config.crosshairStyle.holdingTool; }
    public CrosshairStyle getCrosshairStyleHoldingMeleeWeapon() { return config.crosshairStyle.holdingMeleeWeapon; }
    public CrosshairStyle getCrosshairStyleHoldingRangedWeapon() { return config.crosshairStyle.holdingRangedWeapon; }
    public CrosshairStyle getCrosshairStyleHoldingThrowable() { return config.crosshairStyle.holdingThrowable; }
    public CrosshairStyle getCrosshairStyleHoldingBlock() { return config.crosshairStyle.holdingBlock; }
    public CrosshairModifier getCrosshairModifierInteractable() { return config.crosshairModifiers.modInteractable; }
    public CrosshairModifier getCrosshairModifierCorrectTool() { return config.crosshairModifiers.modCorrectTool; }
    public CrosshairModifier getCrosshairModifierIncorrectTool() { return config.crosshairModifiers.modIncorrectTool; }
    public CrosshairModifier getCrosshairModifierUsableItem() { return config.crosshairModifiers.modUsableItem; }
    public CrosshairModifier getCrosshairModifierShield() { return config.crosshairModifiers.modShield; }

}