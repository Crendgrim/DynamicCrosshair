package mod.crend.dynamiccrosshair.config;

import dev.isxander.yacl3.config.ConfigEntry;
import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.render.CrosshairModifierRenderer;
import mod.crend.dynamiccrosshair.render.CrosshairStyleRenderer;
import mod.crend.yaclx.render.ItemOrTagRenderer;
import mod.crend.yaclx.type.ItemOrTag;
import mod.crend.yaclx.auto.annotation.*;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

@AutoYaclConfig(modid=DynamicCrosshair.MOD_ID, translationKey = "dynamiccrosshair.title", filename = "dynamiccrosshair.json5")
public class Config {
    @ConfigEntry
    public CrosshairMode dynamicCrosshair = CrosshairMode.Advanced;
    @ConfigEntry
    public boolean disableDebugCrosshair = false;
    @ConfigEntry
    public boolean thirdPersonCrosshair = false;
    @ConfigEntry
    public boolean hideWithScreen = true;
    @ConfigEntry
    public CrosshairConfig crosshairConfig = new CrosshairConfig();

    public static class CrosshairConfig {
        @ConfigEntry
        public InteractableCrosshairPolicy onBlock = InteractableCrosshairPolicy.IfTargeting;
        @ConfigEntry
        public boolean onEntity = true;
        @ConfigEntry
        public CrosshairPolicy holdingTool = CrosshairPolicy.Always;
        @ConfigEntry
        public boolean holdingMeleeWeapon = true;
        @ConfigEntry
        public boolean meleeWeaponOnEntity = false;
        @ConfigEntry
        public boolean meleeWeaponOnBreakableBlock = false;
        @ConfigEntry
        public UsableCrosshairPolicy holdingRangedWeapon = UsableCrosshairPolicy.IfInteractable;
        @ConfigEntry
        public UsableCrosshairPolicy holdingThrowable = UsableCrosshairPolicy.IfInteractable;
        @ConfigEntry
        public boolean holdingShield = true;
        @ConfigEntry
        public BlockCrosshairPolicy holdingBlock = BlockCrosshairPolicy.IfInteractable;
        @ConfigEntry
        public UsableCrosshairPolicy holdingUsableItem = UsableCrosshairPolicy.IfInteractable;
    }

    public static class CrosshairColorReader implements EnableIf.Predicate {
        @Override
        public boolean test(Object color) {
            return color == CrosshairConfigColor.Custom;
        }
    }
    public static class CrosshairColorSettings {
        @ConfigEntry
        @Translation(key="dynamiccrosshair.option.crosshairStyle.color.crosshairColor")
        public CrosshairConfigColor crosshairColor = CrosshairConfigColor.Unchanged;
        @ConfigEntry
        @Translation(key="dynamiccrosshair.option.crosshairStyle.color.customColor")
        @EnableIf(field="crosshairColor", value=CrosshairColorReader.class)
        public Color customColor = new Color(0xFFAABBCC, true);
        @ConfigEntry
        @Translation(key="dynamiccrosshair.option.crosshairStyle.color.forceColor")
        public boolean forceColor = false;
    }
    public static class CrosshairStyleSettings {
        @ConfigEntry
        @Translation(key="dynamiccrosshair.option.crosshairStyle.style")
        @Decorate(decorator = CrosshairStyleRenderer.class)
        public CrosshairConfigStyle style = CrosshairConfigStyle.Cross;
        @ConfigEntry
        @TransitiveObject
        public CrosshairColorSettings color = new CrosshairColorSettings();
    }
    public static class CrosshairModifierSettings {
        @ConfigEntry
        @Translation(key="dynamiccrosshair.option.crosshairStyle.style")
        @Decorate(decorator = CrosshairModifierRenderer.class)
        public CrosshairConfigModifier style;
        @ConfigEntry
        @TransitiveObject
        public CrosshairColorSettings color = new CrosshairColorSettings();
    }

    @ConfigEntry
    @TransitiveObject
    @Category(name = "style")
    public CrosshairColorSettings color = new CrosshairColorSettings();

    @ConfigEntry
    public boolean dynamicCrosshairStyle = true;
    @ConfigEntry
    @TransitiveObject
    @Category(name = "style")
    public CrosshairStyles crosshairStyle = new CrosshairStyles();
    @ConfigEntry
    @Category(name = "style")
    @TransitiveObject
    public CrosshairModifiers crosshairModifiers = new CrosshairModifiers();
    public static class CrosshairStyles {
        @ConfigEntry
        public CrosshairStyleSettings regular = new CrosshairStyleSettings();
        @ConfigEntry
        public CrosshairStyleSettings onBlock = new CrosshairStyleSettings();
        @ConfigEntry
        public CrosshairStyleSettings onEntity = new CrosshairStyleSettings();
        @ConfigEntry
        public CrosshairStyleSettings holdingTool = new CrosshairStyleSettings();
        @ConfigEntry
        public CrosshairStyleSettings holdingMeleeWeapon = new CrosshairStyleSettings();
        @ConfigEntry
        public CrosshairStyleSettings holdingRangedWeapon = new CrosshairStyleSettings();
        @ConfigEntry
        public CrosshairStyleSettings holdingThrowable = new CrosshairStyleSettings();
        @ConfigEntry
        public CrosshairStyleSettings holdingBlock = new CrosshairStyleSettings();

        public CrosshairStyles() {
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
    public static class CrosshairModifiers {
        @ConfigEntry
        public CrosshairModifierSettings modInteractable = new CrosshairModifierSettings();
        @ConfigEntry
        public CrosshairModifierSettings modUsableItem = new CrosshairModifierSettings();
        @ConfigEntry
        public CrosshairModifierSettings modShield = new CrosshairModifierSettings();
        @ConfigEntry
        public CrosshairModifierSettings modCorrectTool = new CrosshairModifierSettings();
        @ConfigEntry
        public CrosshairModifierSettings modIncorrectTool = new CrosshairModifierSettings();

        public CrosshairModifiers() {
            modInteractable.style = CrosshairConfigModifier.Brackets;
            modCorrectTool.style = CrosshairConfigModifier.Dot;
            modIncorrectTool.style = CrosshairConfigModifier.DiagonalCross;
            modUsableItem.style = CrosshairConfigModifier.RoundBrackets;
            modShield.style = CrosshairConfigModifier.BracketsBottom;
        }
    }

    @ConfigEntry
    @Category(name="tweaks")
    public boolean enableTweaks = false;

    @ConfigEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<ItemOrTag> additionalTools = Collections.emptyList();
    @ConfigEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<ItemOrTag> additionalMeleeWeapons = Collections.emptyList();
    @ConfigEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<ItemOrTag> additionalRangedWeapons = Collections.emptyList();
    @ConfigEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<ItemOrTag> additionalThrowables = Collections.emptyList();
    @ConfigEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<ItemOrTag> additionalUsableItems = Collections.emptyList();

}
