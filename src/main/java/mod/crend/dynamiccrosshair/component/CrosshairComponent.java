package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.style.CrosshairStyle;
import mod.crend.dynamiccrosshair.style.CrosshairStyledPart;
import mod.crend.dynamiccrosshairapi.crosshair.Crosshair;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairPart;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;

import java.util.ArrayList;
import java.util.List;

public class CrosshairComponent {

    public static CrosshairComponent FORCE_CROSSHAIR;
    public static void init() {
        FORCE_CROSSHAIR = new CrosshairComponent(new Crosshair(InteractionType.FORCE_REGULAR_CROSSHAIR));
    }
    static {
        init();
    }

    Crosshair crosshair;
    CrosshairStyle primaryStyle = null;
    CrosshairStyle secondaryStyle = null;
    CrosshairStyle hitModifier = null;

    List<CrosshairStyledPart> stylesWithBlend = new ArrayList<>();
    List<CrosshairStyledPart> stylesWithoutBlend = new ArrayList<>();

    public CrosshairComponent(Crosshair crosshair) {
        this.crosshair = crosshair;
        CrosshairVariant primary = CrosshairVariant.Regular;
        CrosshairVariant secondary = CrosshairVariant.NONE;
        ModifierHit modifierHit = ModifierHit.NONE;
        switch (crosshair.primaryInteraction()) {
            case TARGET_BLOCK -> primary = CrosshairVariant.OnBlock;
            case TARGET_ENTITY -> primary = CrosshairVariant.OnEntity;
            case MELEE_WEAPON -> primary = CrosshairVariant.HoldingMeleeWeapon;
            case TOOL -> primary = CrosshairVariant.HoldingTool;
            case CORRECT_TOOL -> { primary = CrosshairVariant.HoldingTool; modifierHit = ModifierHit.CORRECT_TOOL; }
            case INCORRECT_TOOL -> { primary = CrosshairVariant.HoldingTool; modifierHit = ModifierHit.INCORRECT_TOOL; }
        }
        if (crosshair.primaryInteraction() != InteractionType.FORCE_REGULAR_CROSSHAIR) {
            switch (crosshair.secondaryInteraction()) {
                case USE_ITEM,
                     EQUIP_ITEM,
                     CONSUME_ITEM,
                     CHARGE_ITEM,
                     USE_ITEM_ON_BLOCK,
                     PLACE_ITEM_ON_BLOCK,
                     FILL_ITEM_FROM_BLOCK,
                     FILL_BLOCK_FROM_ITEM,
                     USE_BLOCK,
                     USE_ITEM_ON_ENTITY,
                     PICK_UP_BLOCK,
                     PICK_UP_ENTITY,
                     PLACE_ITEM_ON_ENTITY,
                     FILL_ITEM_FROM_ENTITY,
                     FILL_ENTITY_FROM_ITEM -> secondary = CrosshairVariant.HoldingUsableItem;
                case SPYGLASS -> {
                    if (DynamicCrosshairMod.config.dynamicCrosshairForceHoldingSpyglass()) {
                        primary = CrosshairVariant.Regular;
                    } else {
                        secondary = CrosshairVariant.HoldingUsableItem;
                    }
                }
                case THROW_ITEM -> secondary = CrosshairVariant.HoldingThrowable;

                case PLACE_BLOCK,
                     PLACE_ENTITY -> secondary = CrosshairVariant.HoldingBlock;

                case INTERACT_WITH_BLOCK,
                     TAKE_ITEM_FROM_BLOCK,
                     MOUNT_BLOCK,
                     INTERACT_WITH_ENTITY,
                     TAKE_ITEM_FROM_ENTITY,
                     MOUNT_ENTITY,
                     PICK_UP_ITEM -> secondary = CrosshairVariant.CanInteract;

                case RANGED_WEAPON_CHARGING -> primary = CrosshairVariant.Regular;
                case RANGED_WEAPON, RANGED_WEAPON_CHARGED -> secondary = CrosshairVariant.HoldingRangedWeapon;
                case USABLE_TOOL -> {
                    primary = CrosshairVariant.HoldingTool;
                    secondary = CrosshairVariant.HoldingUsableItem;
                }
                case SHIELD -> secondary = CrosshairVariant.HoldingShield;

                case FORCE_REGULAR_CROSSHAIR -> modifierHit = ModifierHit.NONE;
            }
        }
        primaryStyle = getCrosshairStyle(primary);
        secondaryStyle = getCrosshairStyle(secondary);
        if (DynamicCrosshairMod.config.dynamicCrosshairDisplayCorrectTool()) {
            switch (modifierHit) {
                case CORRECT_TOOL -> hitModifier = DynamicCrosshairMod.config.getCrosshairModifierCorrectTool();
                case INCORRECT_TOOL ->
                        hitModifier = DynamicCrosshairMod.config.getCrosshairModifierIncorrectTool();
            }
        }
        if (primaryStyle != null) {
            if (secondaryStyle != null && !secondaryStyle.coalesce()) {
                primaryStyle = null;
            } else if (!primaryStyle.coalesce()) {
                secondaryStyle = null;
            }
        }

        if (primaryStyle != null) {
            if (primaryStyle.enableBlend()) {
                stylesWithBlend.add(new CrosshairStyledPart(CrosshairPart.PRIMARY, primaryStyle));
            } else {
                stylesWithoutBlend.add(new CrosshairStyledPart(CrosshairPart.PRIMARY, primaryStyle));
            }
        } else if (secondaryStyle == null) {
            CrosshairStyle forceCrosshair = FORCE_CROSSHAIR.primaryStyle;
            if (forceCrosshair.enableBlend()) {
                stylesWithBlend.add(new CrosshairStyledPart(CrosshairPart.PRIMARY, forceCrosshair));
            } else {
                stylesWithoutBlend.add(new CrosshairStyledPart(CrosshairPart.PRIMARY, forceCrosshair));
            }
        }
        if (secondaryStyle != null) {
            if (secondaryStyle.enableBlend()) {
                stylesWithBlend.add(new CrosshairStyledPart(CrosshairPart.SECONDARY, secondaryStyle));
            } else {
                stylesWithoutBlend.add(new CrosshairStyledPart(CrosshairPart.SECONDARY, secondaryStyle));
            }
        }
        for (CrosshairStyle modifier : getModifiers()) {
            if (modifier.enableBlend()) {
                stylesWithBlend.add(new CrosshairStyledPart(CrosshairPart.MODIFIER, modifier));
            } else {
                stylesWithoutBlend.add(new CrosshairStyledPart(CrosshairPart.MODIFIER, modifier));
            }
        }
    }

    private CrosshairStyle getCrosshairStyle(CrosshairVariant variant) {
        return switch (variant) {
			case NONE -> null;
			case Regular -> DynamicCrosshairMod.config.getCrosshairStyleRegular();
            case OnBlock -> DynamicCrosshairMod.config.getCrosshairStyleOnBlock();
            case OnEntity -> DynamicCrosshairMod.config.getCrosshairStyleOnEntity();
            case HoldingBlock -> DynamicCrosshairMod.config.getCrosshairStyleHoldingBlock();
            case HoldingTool -> DynamicCrosshairMod.config.getCrosshairStyleHoldingTool();
            case HoldingMeleeWeapon -> DynamicCrosshairMod.config.getCrosshairStyleHoldingMeleeWeapon();
            case HoldingRangedWeapon -> DynamicCrosshairMod.config.getCrosshairStyleHoldingRangedWeapon();
            case HoldingThrowable -> DynamicCrosshairMod.config.getCrosshairStyleHoldingThrowable();
            case HoldingUsableItem -> DynamicCrosshairMod.config.getCrosshairStyleUsableItem();
            case HoldingShield -> DynamicCrosshairMod.config.getCrosshairStyleShield();
            case CanInteract -> DynamicCrosshairMod.config.getCrosshairStyleInteractable();
		};
    }

    public List<CrosshairStyle> getModifiers() {
        if (hitModifier == null) return List.of();
        return List.of(hitModifier);
    }

    public Crosshair getCrosshair() {
        return crosshair;
    }

    public List<CrosshairStyledPart> getStylesWithBlend() {
        return stylesWithBlend;
    }
    public List<CrosshairStyledPart> getStylesWithoutBlend() {
        return stylesWithoutBlend;
    }

    public String toString() {
        return "CrosshairComponent{"
                + "blend=["
                + String.join(", ", stylesWithBlend.stream().map(p -> p.part() + ":" + p.style()).toList())
                + "] noblend=["
                + String.join(", ", stylesWithoutBlend.stream().map(p -> p.part() + ":" + p.style()).toList())
                + "]}";
    }
}
