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
    public boolean isDisableDebugCrosshair() { return config.disableDebugCrosshair; }
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


    public CrosshairColor getColor() { return new CrosshairColor(config.color.crosshairColor, config.color.customColor, config.color.forceColor); }

    public boolean isDynamicCrosshairStyle() { return config.dynamicCrosshairStyle; }

    public CrosshairStyle getCrosshairStyleRegular() { return new CrosshairStyle(config.crosshairStyle.regular); }
    public CrosshairStyle getCrosshairStyleOnBlock() { return new CrosshairStyle(config.crosshairStyle.onBlock); }
    public CrosshairStyle getCrosshairStyleOnEntity() { return new CrosshairStyle(config.crosshairStyle.onEntity); }
    public CrosshairStyle getCrosshairStyleHoldingTool() { return new CrosshairStyle(config.crosshairStyle.holdingTool); }
    public CrosshairStyle getCrosshairStyleHoldingMeleeWeapon() { return new CrosshairStyle(config.crosshairStyle.holdingMeleeWeapon); }
    public CrosshairStyle getCrosshairStyleHoldingRangedWeapon() { return new CrosshairStyle(config.crosshairStyle.holdingRangedWeapon); }
    public CrosshairStyle getCrosshairStyleHoldingThrowable() { return new CrosshairStyle(config.crosshairStyle.holdingThrowable); }
    public CrosshairStyle getCrosshairStyleHoldingBlock() { return new CrosshairStyle(config.crosshairStyle.holdingBlock); }
    public CrosshairModifier getCrosshairModifierInteractable() { return new CrosshairModifier(config.crosshairModifiers.modInteractable); }
    public CrosshairModifier getCrosshairModifierCorrectTool() { return new CrosshairModifier(config.crosshairModifiers.modCorrectTool); }
    public CrosshairModifier getCrosshairModifierIncorrectTool() { return new CrosshairModifier(config.crosshairModifiers.modIncorrectTool); }
    public CrosshairModifier getCrosshairModifierUsableItem() { return new CrosshairModifier(config.crosshairModifiers.modUsableItem); }
    public CrosshairModifier getCrosshairModifierShield() { return new CrosshairModifier(config.crosshairModifiers.modShield); }

}