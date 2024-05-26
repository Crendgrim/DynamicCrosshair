package mod.crend.dynamiccrosshair.config;

import mod.crend.dynamiccrosshair.registry.DynamicCrosshairStyles;
import mod.crend.dynamiccrosshair.style.CrosshairStyle;
import mod.crend.libbamboo.type.BlockOrTag;
import mod.crend.libbamboo.type.ItemOrTag;
import mod.crend.libbamboo.opt.ConfigStore;

import java.util.Collections;
import java.util.List;

public class ConfigHandler {
    public static final ConfigStore<Config> CONFIG_STORE = new ConfigStore<>(Config.class, new ConfigUpdater());

    public ConfigHandler() {
        CONFIG_STORE.config();
    }

    public boolean isDynamicCrosshair() { return CONFIG_STORE.config().dynamicCrosshair; }
    public boolean isDisableDebugCrosshair() { return CONFIG_STORE.config().disableDebugCrosshair; }
    public boolean isThirdPersonCrosshair() { return CONFIG_STORE.config().thirdPersonCrosshair; }
    public boolean isHideWithScreen() { return CONFIG_STORE.config().hideWithScreen; }
    public boolean isHideWithMap() { return CONFIG_STORE.config().hideWithMap; }
    public boolean isFixCenteredCrosshair() { return CONFIG_STORE.config().fixCenteredCrosshair; }
    public boolean dynamicCrosshairOnBlock() { return CONFIG_STORE.config().crosshairConfig.onBlock; }
    public boolean dynamicCrosshairOnInteractableBlock() { return CONFIG_STORE.config().crosshairConfig.onInteractableBlock; }
    public boolean dynamicCrosshairOnEntity() { return CONFIG_STORE.config().crosshairConfig.onEntity; }
    public CrosshairPolicy dynamicCrosshairHoldingTool() { return CONFIG_STORE.config().crosshairConfig.holdingTool; }
    public boolean dynamicCrosshairDisplayCorrectTool() { return CONFIG_STORE.config().crosshairConfig.displayCorrectTool; }
    public boolean dynamicCrosshairHoldingMeleeWeapon() { return CONFIG_STORE.config().crosshairConfig.holdingMeleeWeapon; }
    public boolean dynamicCrosshairMeleeWeaponOnEntity() { return CONFIG_STORE.config().crosshairConfig.meleeWeaponOnEntity; }
    public boolean dynamicCrosshairMeleeWeaponOnBreakableBlock() { return CONFIG_STORE.config().crosshairConfig.meleeWeaponOnBreakableBlock; }
    public UsableCrosshairPolicy dynamicCrosshairHoldingRangedWeapon() { return CONFIG_STORE.config().crosshairConfig.holdingRangedWeapon; }
    public UsableCrosshairPolicy dynamicCrosshairHoldingThrowable() { return CONFIG_STORE.config().crosshairConfig.holdingThrowable; }
    public boolean dynamicCrosshairHoldingShield() { return CONFIG_STORE.config().crosshairConfig.holdingShield; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingBlock() { return CONFIG_STORE.config().crosshairConfig.holdingBlock; }
    public UsableCrosshairPolicy dynamicCrosshairHoldingUsableItem() { return CONFIG_STORE.config().crosshairConfig.holdingUsableItem; }
    public boolean dynamicCrosshairForceHoldingSpyglass() { return CONFIG_STORE.config().crosshairConfig.forceHoldingSpyglass; }

    public CrosshairStyle getDefaultStyle() {
        return new CrosshairStyle(
                DynamicCrosshairStyles.DEFAULT,
                CONFIG_STORE.config().color.overrideColor ? CONFIG_STORE.config().color.customColor.getRGB() : 0xFFFFFFFF,
                CONFIG_STORE.config().color.enableBlend,
                false
        );
    }

    public boolean isDynamicCrosshairStyle() { return CONFIG_STORE.config().dynamicCrosshairStyle; }

    private CrosshairStyle configToCrosshairStyle(Config.CrosshairStyleSettings cfg) {
        return new CrosshairStyle(cfg.style, cfg.overrideColor ? cfg.customColor.getRGB() : getDefaultStyle().color(), cfg.enableBlend, cfg.isModifier);
    }
    public CrosshairStyle getCrosshairStyleRegular() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.regular); }
    public CrosshairStyle getCrosshairStyleOnBlock() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.onBlock); }
    public CrosshairStyle getCrosshairStyleOnEntity() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.onEntity); }
    public CrosshairStyle getCrosshairStyleHoldingTool() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.holdingTool); }
    public CrosshairStyle getCrosshairStyleHoldingMeleeWeapon() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.holdingMeleeWeapon); }
    public CrosshairStyle getCrosshairStyleHoldingRangedWeapon() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.holdingRangedWeapon); }
    public CrosshairStyle getCrosshairStyleHoldingThrowable() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.holdingThrowable); }
    public CrosshairStyle getCrosshairStyleHoldingBlock() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.holdingBlock); }
    public CrosshairStyle getCrosshairStyleInteractable() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.interact); }
    public CrosshairStyle getCrosshairStyleUsableItem() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.useItem); }
    public CrosshairStyle getCrosshairStyleShield() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairStyle.shield); }
    public CrosshairStyle getCrosshairModifierCorrectTool() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairModifiers.modCorrectTool); }
    public CrosshairStyle getCrosshairModifierIncorrectTool() { return configToCrosshairStyle(CONFIG_STORE.config().crosshairModifiers.modIncorrectTool); }

    public boolean isTweaksEnabled() {
        return CONFIG_STORE.config().enableTweaks;
    }

    public List<ItemOrTag> getAdditionalTools() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        return CONFIG_STORE.config().additionalTools;
    }
    public List<ItemOrTag> getAdditionalMeleeWeapons() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        return CONFIG_STORE.config().additionalMeleeWeapons;
    }
    public List<ItemOrTag> getAdditionalRangedWeapons() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        return CONFIG_STORE.config().additionalRangedWeapons;
    }
    public List<ItemOrTag> getAdditionalThrowables() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        return CONFIG_STORE.config().additionalThrowables;
    }
    public List<ItemOrTag> getAdditionalUsableItems() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        return CONFIG_STORE.config().additionalUsableItems;
    }

    public List<BlockOrTag> getAdditionalInteractableBlocks() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        return CONFIG_STORE.config().additionalInteractableBlocks;
    }

}