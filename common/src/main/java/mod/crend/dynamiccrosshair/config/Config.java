package mod.crend.dynamiccrosshair.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import mod.crend.dynamiccrosshair.DynamicCrosshair;

@me.shedaniel.autoconfig.annotation.Config(name = DynamicCrosshair.MOD_ID)
public class Config implements ConfigData {
    @ConfigEntry.Gui.Tooltip
    boolean dynamicCrosshair = true;
    boolean disableDebugCrosshair = false;
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
        CrosshairConfigColor crosshairColor = CrosshairConfigColor.Unchanged;
        @ConfigEntry.ColorPicker(allowAlpha = true)
        int customColor = 0xFFAABBCC;
        @ConfigEntry.Gui.Tooltip
        boolean forceColor = false;
    }
    static class CrosshairStyleSettings {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairConfigStyle style = CrosshairConfigStyle.Cross;
        @ConfigEntry.Gui.TransitiveObject
        CrosshairColorSettings color = new CrosshairColorSettings();
    }
    static class CrosshairModifierSettings {
        @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
        CrosshairConfigModifier style;
        @ConfigEntry.Gui.TransitiveObject
        CrosshairColorSettings color = new CrosshairColorSettings();
    }

    @ConfigEntry.Category("style")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    CrosshairColorSettings color = new CrosshairColorSettings();

    @ConfigEntry.Category("style")
    boolean dynamicCrosshairStyle = true;
    @ConfigEntry.Category("style")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    CrosshairStyles crosshairStyle = new CrosshairStyles();
    @ConfigEntry.Category("style")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    CrosshairModifiers crosshairModifiers = new CrosshairModifiers();
    static class CrosshairStyles {
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairStyleSettings regular = new CrosshairStyleSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairStyleSettings onBlock = new CrosshairStyleSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairStyleSettings onEntity = new CrosshairStyleSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairStyleSettings holdingTool = new CrosshairStyleSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairStyleSettings holdingMeleeWeapon = new CrosshairStyleSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairStyleSettings holdingRangedWeapon = new CrosshairStyleSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairStyleSettings holdingThrowable = new CrosshairStyleSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairStyleSettings holdingBlock = new CrosshairStyleSettings();

        CrosshairStyles() {
            regular.style = CrosshairConfigStyle.Cross;
            onBlock.style = CrosshairConfigStyle.Cross;
            onEntity.style = CrosshairConfigStyle.DiagonalCross;
            holdingTool.style = CrosshairConfigStyle.Square;
            holdingMeleeWeapon.style = CrosshairConfigStyle.Cross;
            holdingRangedWeapon.style = CrosshairConfigStyle.DiagonalCross;
            holdingThrowable.style = CrosshairConfigStyle.Circle;
            holdingBlock.style = CrosshairConfigStyle.Diamond;
        }
    }
    static class CrosshairModifiers {
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairModifierSettings modInteractable = new CrosshairModifierSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairModifierSettings modUsableItem = new CrosshairModifierSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairModifierSettings modShield = new CrosshairModifierSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairModifierSettings modCorrectTool = new CrosshairModifierSettings();
        @ConfigEntry.Gui.CollapsibleObject
        CrosshairModifierSettings modIncorrectTool = new CrosshairModifierSettings();

        CrosshairModifiers() {
            modInteractable.style = CrosshairConfigModifier.Brackets;
            modCorrectTool.style = CrosshairConfigModifier.Dot;
            modIncorrectTool.style = CrosshairConfigModifier.DiagonalCross;
            modUsableItem.style = CrosshairConfigModifier.RoundBrackets;
            modShield.style = CrosshairConfigModifier.BracketsBottom;
        }
    }

    @ConfigEntry.Category("tweaks")
    @ConfigEntry.Gui.PrefixText
    boolean enableTweaks = false;

    @ConfigEntry.Category("tweaks")
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.RequiresRestart
    String additionalTools;
    @ConfigEntry.Category("tweaks")
    @ConfigEntry.Gui.RequiresRestart
    String additionalMeleeWeapons;
    @ConfigEntry.Category("tweaks")
    @ConfigEntry.Gui.RequiresRestart
    String additionalRangedWeapons;
    @ConfigEntry.Category("tweaks")
    @ConfigEntry.Gui.RequiresRestart
    String additionalThrowables;
    @ConfigEntry.Category("tweaks")
    @ConfigEntry.Gui.RequiresRestart
    String additionalUsableItems;
}
