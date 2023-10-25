package mod.crend.dynamiccrosshair.config;

import mod.crend.autoyacl.ConfigStore;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigHandler {
    public static final ConfigStore<Config> CONFIG_STORE = new ConfigStore<>(Config.class);

    public ConfigHandler() {
        CONFIG_STORE.config();
    }

    public boolean isDynamicCrosshair() { return CONFIG_STORE.config().dynamicCrosshair != CrosshairMode.Disabled; }
    public boolean isDynamicCrosshairSimple() { return CONFIG_STORE.config().dynamicCrosshair == CrosshairMode.Simple; }
    public boolean isDisableDebugCrosshair() { return CONFIG_STORE.config().disableDebugCrosshair; }
    public boolean isThirdPersonCrosshair() { return CONFIG_STORE.config().thirdPersonCrosshair; }
    public boolean isHideWithScreen() { return CONFIG_STORE.config().hideWithScreen; }
    public InteractableCrosshairPolicy dynamicCrosshairOnBlock() { return CONFIG_STORE.config().crosshairConfig.onBlock; }
    public boolean dynamicCrosshairOnEntity() { return CONFIG_STORE.config().crosshairConfig.onEntity; }
    public CrosshairPolicy dynamicCrosshairHoldingTool() { return CONFIG_STORE.config().crosshairConfig.holdingTool; }
    public boolean dynamicCrosshairHoldingMeleeWeapon() { return CONFIG_STORE.config().crosshairConfig.holdingMeleeWeapon; }
    public boolean dynamicCrosshairMeleeWeaponOnEntity() { return CONFIG_STORE.config().crosshairConfig.meleeWeaponOnEntity; }
    public boolean dynamicCrosshairMeleeWeaponOnBreakableBlock() { return CONFIG_STORE.config().crosshairConfig.meleeWeaponOnBreakableBlock; }
    public UsableCrosshairPolicy dynamicCrosshairHoldingRangedWeapon() { return CONFIG_STORE.config().crosshairConfig.holdingRangedWeapon; }
    public UsableCrosshairPolicy dynamicCrosshairHoldingThrowable() { return CONFIG_STORE.config().crosshairConfig.holdingThrowable; }
    public boolean dynamicCrosshairHoldingShield() { return CONFIG_STORE.config().crosshairConfig.holdingShield; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingBlock() { return CONFIG_STORE.config().crosshairConfig.holdingBlock; }
    public UsableCrosshairPolicy dynamicCrosshairHoldingUsableItem() { return CONFIG_STORE.config().crosshairConfig.holdingUsableItem; }


    public CrosshairColor getColor() { return new CrosshairColor(CONFIG_STORE.config().color.crosshairColor, CONFIG_STORE.config().color.customColor.getRGB(), CONFIG_STORE.config().color.forceColor); }

    public boolean isDynamicCrosshairStyle() { return CONFIG_STORE.config().dynamicCrosshairStyle; }

    public CrosshairStyle getCrosshairStyleRegular() { return new CrosshairStyle(CONFIG_STORE.config().crosshairStyle.regular); }
    public CrosshairStyle getCrosshairStyleOnBlock() { return new CrosshairStyle(CONFIG_STORE.config().crosshairStyle.onBlock); }
    public CrosshairStyle getCrosshairStyleOnEntity() { return new CrosshairStyle(CONFIG_STORE.config().crosshairStyle.onEntity); }
    public CrosshairStyle getCrosshairStyleHoldingTool() { return new CrosshairStyle(CONFIG_STORE.config().crosshairStyle.holdingTool); }
    public CrosshairStyle getCrosshairStyleHoldingMeleeWeapon() { return new CrosshairStyle(CONFIG_STORE.config().crosshairStyle.holdingMeleeWeapon); }
    public CrosshairStyle getCrosshairStyleHoldingRangedWeapon() { return new CrosshairStyle(CONFIG_STORE.config().crosshairStyle.holdingRangedWeapon); }
    public CrosshairStyle getCrosshairStyleHoldingThrowable() { return new CrosshairStyle(CONFIG_STORE.config().crosshairStyle.holdingThrowable); }
    public CrosshairStyle getCrosshairStyleHoldingBlock() { return new CrosshairStyle(CONFIG_STORE.config().crosshairStyle.holdingBlock); }
    public CrosshairModifier getCrosshairModifierInteractable() { return new CrosshairModifier(CONFIG_STORE.config().crosshairModifiers.modInteractable); }
    public CrosshairModifier getCrosshairModifierCorrectTool() { return new CrosshairModifier(CONFIG_STORE.config().crosshairModifiers.modCorrectTool); }
    public CrosshairModifier getCrosshairModifierIncorrectTool() { return new CrosshairModifier(CONFIG_STORE.config().crosshairModifiers.modIncorrectTool); }
    public CrosshairModifier getCrosshairModifierUsableItem() { return new CrosshairModifier(CONFIG_STORE.config().crosshairModifiers.modUsableItem); }
    public CrosshairModifier getCrosshairModifierShield() { return new CrosshairModifier(CONFIG_STORE.config().crosshairModifiers.modShield); }

    List<Item> additionalTools = null;
    List<Item> additionalMeleeWeapons = null;
    List<Item> additionalRangedWeapons = null;
    List<Item> additionalThrowables = null;
    List<Item> additionalUsableItems = null;
    List<Block> additionalInteractableBlocks = null;

    public boolean isTweaksEnabled() {
        return CONFIG_STORE.config().enableTweaks;
    }

    private List<Item> buildAdditionalItemList(String configString) {
        return Arrays.stream(configString.split(";"))
                .filter(s -> !s.isBlank())
                .map(Identifier::tryParse)
                .filter(Objects::nonNull)
                .map(Registries.ITEM::get)
                .collect(Collectors.toList());
    }
    private List<Block> buildAdditionalBlockList(String configString) {
        return Arrays.stream(configString.split(";"))
                .filter(s -> !s.isBlank())
                .map(Identifier::tryParse)
                .filter(Objects::nonNull)
                .map(Registries.BLOCK::get)
                .collect(Collectors.toList());
    }
    public List<Item> getAdditionalTools() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalTools == null) {
            additionalTools = buildAdditionalItemList(CONFIG_STORE.config().additionalTools);
        }
        return additionalTools;
    }
    public List<Item> getAdditionalMeleeWeapons() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalMeleeWeapons == null) {
            additionalMeleeWeapons = buildAdditionalItemList(CONFIG_STORE.config().additionalMeleeWeapons);
        }
        return additionalMeleeWeapons;
    }
    public List<Item> getAdditionalRangedWeapons() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalRangedWeapons == null) {
            additionalRangedWeapons = buildAdditionalItemList(CONFIG_STORE.config().additionalRangedWeapons);
        }
        return additionalRangedWeapons;
    }
    public List<Item> getAdditionalThrowables() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalThrowables == null) {
            additionalThrowables = buildAdditionalItemList(CONFIG_STORE.config().additionalThrowables);
        }
        return additionalThrowables;
    }
    public List<Item> getAdditionalUsableItems() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalUsableItems == null) {
            additionalUsableItems = buildAdditionalItemList(CONFIG_STORE.config().additionalUsableItems);
        }
        return additionalUsableItems;
    }
    public List<Block> getAdditionalInteractableBlocks() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalInteractableBlocks == null) {
            additionalInteractableBlocks = buildAdditionalBlockList(CONFIG_STORE.config().additionalInteractableBlocks);
        }
        return additionalInteractableBlocks;
    }

}