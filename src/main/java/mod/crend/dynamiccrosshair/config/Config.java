package mod.crend.dynamiccrosshair.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.CrosshairModifier;

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
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairPolicy holdingRangedWeapon = CrosshairPolicy.Always;
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
    public CrosshairPolicy dynamicCrosshairHoldingRangedWeapon() { return crosshairSettings.holdingRangedWeapon; }
    public CrosshairPolicy dynamicCrosshairHoldingThrowable() { return crosshairSettings.holdingThrowable; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingBlock() { return crosshairSettings.holdingBlock; }
    public BlockCrosshairPolicy dynamicCrosshairHoldingUsableItem() { return crosshairSettings.holdingUsableItem; }

    boolean dynamicCrosshairStyle = true;
    @ConfigEntry.Gui.CollapsibleObject
    CrosshairStyle crosshairStyle = new CrosshairStyle();
    @ConfigEntry.Gui.CollapsibleObject
    CrosshairModifiers crosshairModifiers = new CrosshairModifiers();
    public boolean isDynamicCrosshairStyle() { return dynamicCrosshairStyle; }
    static class CrosshairStyle {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair regular = Crosshair.CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair onBlock = Crosshair.CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair onEntity = Crosshair.DIAGONAL_CROSS;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair holdingTool = Crosshair.SQUARE;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair holdingRangedWeapon = Crosshair.CIRCLE;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair holdingThrowable = Crosshair.CIRCLE;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        Crosshair holdingBlock = Crosshair.DIAMOND;
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

    public Crosshair getCrosshairStyleRegular() { return crosshairStyle.regular; }
    public Crosshair getCrosshairStyleOnBlock() { return crosshairStyle.onBlock; }
    public Crosshair getCrosshairStyleOnEntity() { return crosshairStyle.onEntity; }
    public Crosshair getCrosshairStyleHoldingTool() { return crosshairStyle.holdingTool; }
    public Crosshair getCrosshairStyleHoldingRangedWeapon() { return crosshairStyle.holdingRangedWeapon; }
    public Crosshair getCrosshairStyleHoldingThrowable() { return crosshairStyle.holdingThrowable; }
    public Crosshair getCrosshairStyleHoldingBlock() { return crosshairStyle.holdingBlock; }
    public CrosshairModifier getCrosshairModifierInteractable() { return crosshairModifiers.modInteractable; }
    public CrosshairModifier getCrosshairModifierCorrectTool() { return crosshairModifiers.modCorrectTool; }
    public CrosshairModifier getCrosshairModifierIncorrectTool() { return crosshairModifiers.modIncorrectTool; }
    public CrosshairModifier getCrosshairModifierUsableItem() { return crosshairModifiers.modUsableItem; }
}
