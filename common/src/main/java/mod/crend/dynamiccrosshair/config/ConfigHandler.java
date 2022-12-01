package mod.crend.dynamiccrosshair.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigHandler {
    public static Screen getScreen(Screen parent) {
        return AutoConfig.getConfigScreen(Config.class, parent).get();
    }

    Config config;

    public ConfigHandler() {
        AutoConfig.register(Config.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(Config.class).getConfig();
    }

    public boolean isDynamicCrosshair() { return config.dynamicCrosshair != CrosshairMode.Disabled; }
    public boolean isDynamicCrosshairSimple() { return config.dynamicCrosshair == CrosshairMode.Simple; }
    public boolean isDisableDebugCrosshair() { return config.disableDebugCrosshair; }
    public boolean isThirdPersonCrosshair() { return config.thirdPersonCrosshair; }
    public boolean isHideWithScreen() { return config.hideWithScreen; }
    public InteractableCrosshairPolicy dynamicCrosshairOnBlock() { return config.crosshairConfig.onBlock; }
    public boolean dynamicCrosshairOnEntity() { return config.crosshairConfig.onEntity; }
    public CrosshairPolicy dynamicCrosshairHoldingTool() { return config.crosshairConfig.holdingTool; }
    public boolean dynamicCrosshairHoldingMeleeWeapon() { return config.crosshairConfig.holdingMeleeWeapon; }
    public UsableCrosshairPolicy dynamicCrosshairHoldingRangedWeapon() { return config.crosshairConfig.holdingRangedWeapon; }
    public UsableCrosshairPolicy dynamicCrosshairHoldingThrowable() { return config.crosshairConfig.holdingThrowable; }
    public boolean dynamicCrosshairHoldingShield() { return config.crosshairConfig.holdingShield; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingBlock() { return config.crosshairConfig.holdingBlock; }
    public UsableCrosshairPolicy dynamicCrosshairHoldingUsableItem() { return config.crosshairConfig.holdingUsableItem; }


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

    List<Item> additionalTools = null;
    List<Item> additionalMeleeWeapons = null;
    List<Item> additionalRangedWeapons = null;
    List<Item> additionalThrowables = null;
    List<Item> additionalUsableItems = null;

    public boolean isTweaksEnabled() {
        return config.enableTweaks;
    }
    private List<Item> buildAdditionalItemList(String configString) {
        return Arrays.stream(configString.split(";"))
                .filter(s -> !s.isBlank())
                .map(Identifier::tryParse)
                .filter(Objects::nonNull)
                .map(Registries.ITEM::get)
                .collect(Collectors.toList());
    }
    public List<Item> getAdditionalTools() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalTools == null) {
            additionalTools = buildAdditionalItemList(config.additionalTools);
        }
        return additionalTools;
    }
    public List<Item> getAdditionalMeleeWeapons() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalMeleeWeapons == null) {
            additionalMeleeWeapons = buildAdditionalItemList(config.additionalMeleeWeapons);
        }
        return additionalMeleeWeapons;
    }
    public List<Item> getAdditionalRangedWeapons() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalRangedWeapons == null) {
            additionalRangedWeapons = buildAdditionalItemList(config.additionalRangedWeapons);
        }
        return additionalRangedWeapons;
    }
    public List<Item> getAdditionalThrowables() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalThrowables == null) {
            additionalThrowables = buildAdditionalItemList(config.additionalThrowables);
        }
        return additionalThrowables;
    }
    public List<Item> getAdditionalUsableItems() {
        if (!isTweaksEnabled()) return Collections.emptyList();
        if (additionalUsableItems == null) {
            additionalUsableItems = buildAdditionalItemList(config.additionalUsableItems);
        }
        return additionalUsableItems;
    }

}