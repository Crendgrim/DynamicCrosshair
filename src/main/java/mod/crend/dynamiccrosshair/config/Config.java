package mod.crend.dynamiccrosshair.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@me.shedaniel.autoconfig.annotation.Config(name = "dynamiccrosshair")
public class Config implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    boolean dynamicCrosshair = true;
    boolean hideWithScreen = true;
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    CrosshairSettings crosshairSettings = new CrosshairSettings();

    static class CrosshairSettings {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        InteractableCrosshairPolicy onBlock = InteractableCrosshairPolicy.IfTargeting;
        boolean onEntity = true;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairPolicy holdingTool = CrosshairPolicy.Always;
        boolean holdingMeleeWeapon = true;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        RangedCrosshairPolicy holdingRangedWeapon = RangedCrosshairPolicy.IfFullyDrawn;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairPolicy holdingThrowable = CrosshairPolicy.Always;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        BlockCrosshairPolicy holdingBlock = BlockCrosshairPolicy.IfInteractable;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        BlockCrosshairPolicy holdingUsableItem = BlockCrosshairPolicy.IfInteractable;
    }
    public boolean isDynamicCrosshair() { return dynamicCrosshair; }
    public boolean isHideWithScreen() { return hideWithScreen; }
    public InteractableCrosshairPolicy dynamicCrosshairOnBlock() { return crosshairSettings.onBlock; }
    public boolean dynamicCrosshairOnEntity() { return crosshairSettings.onEntity; }
    public CrosshairPolicy dynamicCrosshairHoldingTool() { return crosshairSettings.holdingTool; }
    public boolean dynamicCrosshairHoldingMeleeWeapon() { return crosshairSettings.holdingMeleeWeapon; }
    public RangedCrosshairPolicy dynamicCrosshairHoldingRangedWeapon() { return crosshairSettings.holdingRangedWeapon; }
    public CrosshairPolicy dynamicCrosshairHoldingThrowable() { return crosshairSettings.holdingThrowable; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingBlock() { return crosshairSettings.holdingBlock; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingUsableItem() { return crosshairSettings.holdingUsableItem; }

    static class CrosshairColorSettings {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairColor crosshairColor = CrosshairColor.Unchanged;
        @ConfigEntry.ColorPicker(allowAlpha = true)
        int customColor = 0xFFAABBCC;
        @ConfigEntry.Gui.Tooltip
        boolean forceColor = false;
    }
    @ConfigEntry.Gui.CollapsibleObject
    CrosshairColorSettings color = new CrosshairColorSettings();

    public CrosshairColor getCrosshairColor() { return color.crosshairColor; }
    public int getCustomColor() { return color.customColor; }
    public boolean isForceColor() { return color.forceColor; }

    boolean dynamicCrosshairStyle = true;
    @ConfigEntry.Gui.CollapsibleObject
    CrosshairStyles crosshairStyle = new CrosshairStyles();
    @ConfigEntry.Gui.CollapsibleObject
    CrosshairModifiers crosshairModifiers = new CrosshairModifiers();
    public boolean isDynamicCrosshairStyle() { return dynamicCrosshairStyle; }
    static class CrosshairStyles {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairStyle regular = CrosshairStyle.CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairStyle onBlock = CrosshairStyle.CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairStyle onEntity = CrosshairStyle.DIAGONAL_CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairStyle holdingTool = CrosshairStyle.SQUARE;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairStyle holdingMeleeWeapon = CrosshairStyle.CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairStyle holdingRangedWeapon = CrosshairStyle.DIAGONAL_CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairStyle holdingThrowable = CrosshairStyle.CIRCLE;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairStyle holdingBlock = CrosshairStyle.DIAMOND;
    }
    static class CrosshairModifiers {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairModifier modInteractable = CrosshairModifier.BRACKETS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairModifier modCorrectTool = CrosshairModifier.DOT;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairModifier modIncorrectTool = CrosshairModifier.DIAGONAL_CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairModifier modUsableItem = CrosshairModifier.ROUND_BRACKETS;
    }

    public CrosshairStyle getCrosshairStyleRegular() { return crosshairStyle.regular; }
    public CrosshairStyle getCrosshairStyleOnBlock() { return crosshairStyle.onBlock; }
    public CrosshairStyle getCrosshairStyleOnEntity() { return crosshairStyle.onEntity; }
    public CrosshairStyle getCrosshairStyleHoldingTool() { return crosshairStyle.holdingTool; }
    public CrosshairStyle getCrosshairStyleHoldingMeleeWeapon() { return crosshairStyle.holdingMeleeWeapon; }
    public CrosshairStyle getCrosshairStyleHoldingRangedWeapon() { return crosshairStyle.holdingRangedWeapon; }
    public CrosshairStyle getCrosshairStyleHoldingThrowable() { return crosshairStyle.holdingThrowable; }
    public CrosshairStyle getCrosshairStyleHoldingBlock() { return crosshairStyle.holdingBlock; }
    public CrosshairModifier getCrosshairModifierInteractable() { return crosshairModifiers.modInteractable; }
    public CrosshairModifier getCrosshairModifierCorrectTool() { return crosshairModifiers.modCorrectTool; }
    public CrosshairModifier getCrosshairModifierIncorrectTool() { return crosshairModifiers.modIncorrectTool; }
    public CrosshairModifier getCrosshairModifierUsableItem() { return crosshairModifiers.modUsableItem; }
}
