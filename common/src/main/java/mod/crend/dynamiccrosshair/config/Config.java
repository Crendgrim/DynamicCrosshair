package mod.crend.dynamiccrosshair.config;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import mod.crend.dynamiccrosshair.config.gui.CrosshairStyleController;
import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairStyles;
import mod.crend.libbamboo.render.ItemOrTagRenderer;
import mod.crend.libbamboo.type.BlockOrTag;
import mod.crend.libbamboo.type.ItemOrTag;
import mod.crend.libbamboo.auto.annotation.*;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@AutoYaclConfig(modid= DynamicCrosshair.MOD_ID, translationKey = "dynamiccrosshair.title", filename = "dynamiccrosshair.json5")
public class Config {
    @SerialEntry
    public boolean dynamicCrosshair = true;
    @SerialEntry
    public boolean disableDebugCrosshair = false;
    @SerialEntry
    public boolean thirdPersonCrosshair = false;
    @SerialEntry
    public boolean hideWithScreen = true;
    @SerialEntry
    public boolean hideWithMap = true;
    @SerialEntry
    public boolean fixCenteredCrosshair = false;
    @SerialEntry
    public CrosshairConfig crosshairConfig = new CrosshairConfig();

    public static class CrosshairConfig {
        @SerialEntry
        public boolean onBlock = true;
        @SerialEntry
        public boolean onInteractableBlock = true;
        @SerialEntry
        public boolean onEntity = true;
        @SerialEntry
        public CrosshairPolicy holdingTool = CrosshairPolicy.Always;
        @SerialEntry
        public boolean displayCorrectTool = true;
        @SerialEntry
        public boolean holdingMeleeWeapon = true;
        @SerialEntry
        public boolean meleeWeaponOnEntity = false;
        @SerialEntry
        public boolean meleeWeaponOnBreakableBlock = false;
        @SerialEntry
        public UsableCrosshairPolicy holdingRangedWeapon = UsableCrosshairPolicy.IfInteractable;
        @SerialEntry
        public UsableCrosshairPolicy holdingThrowable = UsableCrosshairPolicy.IfInteractable;
        @SerialEntry
        public boolean holdingShield = true;
        @SerialEntry
        public BlockCrosshairPolicy holdingBlock = BlockCrosshairPolicy.IfInteractable;
        @SerialEntry
        public UsableCrosshairPolicy holdingUsableItem = UsableCrosshairPolicy.IfInteractable;
        @SerialEntry
        public boolean forceHoldingSpyglass = false;
    }

    public static class CrosshairColorSettings {
        @SerialEntry
        @Translation(key="dynamiccrosshair.option.crosshairStyle.color.crosshairColor")
        public boolean overrideColor = false;
        @SerialEntry
        @Translation(key="dynamiccrosshair.option.crosshairStyle.color.customColor")
        @EnableIf(field = "overrideColor", value = EnableIf.BooleanPredicate.class)
        public Color customColor = new Color(0xFFAABBCC, true);
        @SerialEntry
        @Translation(key="dynamiccrosshair.option.crosshairStyle.color.enableBlend")
        public boolean enableBlend = true;
    }
    public static class CrosshairStyleSettings {
        @SerialEntry
        public Identifier style = DynamicCrosshairStyles.DEFAULT;
        @SerialEntry
        public boolean overrideColor = false;
        @SerialEntry
        public Color customColor = new Color(0xFFAABBCC, true);
        @SerialEntry
        public boolean enableBlend = true;
        @SerialEntry
        public boolean coalesce = true;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CrosshairStyleSettings that = (CrosshairStyleSettings) o;
            return overrideColor == that.overrideColor
                    && enableBlend == that.enableBlend
                    && coalesce == that.coalesce
                    && Objects.equals(style, that.style)
                    && Objects.equals(customColor, that.customColor);
        }

        @Override
        public int hashCode() {
            return Objects.hash(style, overrideColor, customColor, enableBlend, coalesce);
        }
    }

    @SerialEntry
    @TransitiveObject
    @Category(name = "style")
    public CrosshairColorSettings color = new CrosshairColorSettings();

    @SerialEntry
    public boolean dynamicCrosshairStyle = true;
    @SerialEntry
    @TransitiveObject
    @Category(name = "style")
    public CrosshairStyles crosshairStyle = new CrosshairStyles();
    @Category(name = "style")
    @SerialEntry
    @TransitiveObject
    public CrosshairModifiers crosshairModifiers = new CrosshairModifiers();
    public static class CrosshairStyles {
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings regular = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings onBlock = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings onEntity = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings holdingTool = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings holdingMeleeWeapon = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings holdingRangedWeapon = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings holdingThrowable = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings holdingBlock = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings interact = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings useItem = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings shield = new CrosshairStyleSettings();

        public CrosshairStyles() {
            regular.style = DynamicCrosshairStyles.CROSS_OPEN;
            onBlock.style = DynamicCrosshairStyles.CROSS_OPEN;
            onEntity.style = DynamicCrosshairStyles.CROSS_OPEN_DIAGONAL;
            holdingTool.style = DynamicCrosshairStyles.SQUARE;
            holdingMeleeWeapon.style = DynamicCrosshairStyles.CROSS_OPEN;
            holdingRangedWeapon.style = DynamicCrosshairStyles.CROSS_OPEN_DIAGONAL;
            holdingThrowable.style = DynamicCrosshairStyles.CIRCLE_LARGE;
            holdingBlock.style = DynamicCrosshairStyles.DIAMOND_LARGE;
            interact.style = DynamicCrosshairStyles.BRACKETS;
            useItem.style = DynamicCrosshairStyles.BRACKETS_ROUND;
            shield.style = DynamicCrosshairStyles.BRACKETS_BOTTOM;
            holdingRangedWeapon.coalesce = false;
        }
    }
    public static class CrosshairModifiers {
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings modCorrectTool = new CrosshairStyleSettings();
        @SerialEntry
        @CustomController(CrosshairStyleController.Factory.class)
        public CrosshairStyleSettings modIncorrectTool = new CrosshairStyleSettings();

        public CrosshairModifiers() {
            modCorrectTool.style = DynamicCrosshairStyles.DOT;
            modIncorrectTool.style = DynamicCrosshairStyles.CROSS_DIAGONAL_SMALL;
        }
    }

    @SerialEntry
    @Category(name="tweaks")
    public boolean enableTweaks = true;

    @SerialEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<ItemOrTag> additionalTools = Collections.emptyList();
    @SerialEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<ItemOrTag> additionalMeleeWeapons = Collections.emptyList();
    @SerialEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<ItemOrTag> additionalRangedWeapons = Collections.emptyList();
    @SerialEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<ItemOrTag> additionalThrowables = Collections.emptyList();
    @SerialEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfItemOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<ItemOrTag> additionalUsableItems = Collections.emptyList();

    @SerialEntry
    @Category(name="tweaks")
    @DescriptionImage(ItemOrTagRenderer.OfBlockOrTag.class)
    @EnableIf(field = "enableTweaks", value = EnableIf.BooleanPredicate.class)
    public List<BlockOrTag> additionalInteractableBlocks = Collections.emptyList();
}
