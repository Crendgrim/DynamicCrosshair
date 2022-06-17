package mod.crend.dynamiccrosshair.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.crend.dynamiccrosshair.DynamicCrosshair;

@me.shedaniel.autoconfig.annotation.Config(name = DynamicCrosshair.MOD_ID)
public class Config implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    boolean dynamicCrosshair = true;
    boolean hideWithScreen = true;
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    CrosshairConfig crosshairConfig = new CrosshairConfig();

    static class CrosshairConfig {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        InteractableCrosshairPolicy onBlock = InteractableCrosshairPolicy.IfTargeting;
        boolean onEntity = true;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairPolicy holdingTool = CrosshairPolicy.Always;
        boolean holdingMeleeWeapon = true;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        RangedCrosshairPolicy holdingRangedWeapon = RangedCrosshairPolicy.IfRangedWeaponFullyDrawn;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairPolicy holdingThrowable = CrosshairPolicy.Always;
        boolean holdingShield = true;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        BlockCrosshairPolicy holdingBlock = BlockCrosshairPolicy.IfInteractable;
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        BlockCrosshairPolicy holdingUsableItem = BlockCrosshairPolicy.IfInteractable;
    }

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

    boolean dynamicCrosshairStyle = true;
    @ConfigEntry.Gui.CollapsibleObject
    CrosshairStyles crosshairStyle = new CrosshairStyles();
    @ConfigEntry.Gui.CollapsibleObject
    CrosshairModifiers crosshairModifiers = new CrosshairModifiers();
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
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairModifier modShield = CrosshairModifier.BRACKETS_BOTTOM;
    }

}
