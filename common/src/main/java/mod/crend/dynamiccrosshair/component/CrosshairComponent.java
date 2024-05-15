package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.Crosshair;
import mod.crend.dynamiccrosshair.api.InteractionType;
import mod.crend.dynamiccrosshair.config.CrosshairModifier;
import mod.crend.dynamiccrosshair.config.CrosshairStyle;

import java.util.LinkedList;
import java.util.List;

public class CrosshairComponent {

    public static final CrosshairComponent FORCE_CROSSHAIR = new CrosshairComponent(new Crosshair(InteractionType.FORCE_REGULAR_CROSSHAIR));

    CrosshairVariant variant = CrosshairVariant.NONE;
    ModifierUse modifierUse = ModifierUse.NONE;
    ModifierHit modifierHit = ModifierHit.NONE;

    public CrosshairComponent(Crosshair crosshair) {
        switch (crosshair.primaryInteraction()) {
            case TARGET_BLOCK -> variant = CrosshairVariant.OnBlock;
            case TARGET_ENTITY -> variant = CrosshairVariant.OnEntity;
            case MELEE_WEAPON -> variant = CrosshairVariant.HoldingMeleeWeapon;
            case TOOL -> variant = CrosshairVariant.HoldingTool;
            case CORRECT_TOOL -> { variant = CrosshairVariant.HoldingTool; modifierHit = ModifierHit.CORRECT_TOOL; }
            case INCORRECT_TOOL -> { variant = CrosshairVariant.HoldingTool; modifierHit = ModifierHit.INCORRECT_TOOL; }
            case FORCE_REGULAR_CROSSHAIR -> variant = CrosshairVariant.Regular;
        }
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
                 PICK_UP_ENTITY,
                 PLACE_ITEM_ON_ENTITY,
                 FILL_ITEM_FROM_ENTITY,
                 FILL_ENTITY_FROM_ITEM
                    -> modifierUse = ModifierUse.USE_ITEM;
            case SPYGLASS -> {
                if (DynamicCrosshairMod.config.dynamicCrosshairForceHoldingSpyglass()) {
                    variant = CrosshairVariant.Regular;
                } else {
                    modifierUse = ModifierUse.USE_ITEM;
                }
            }
            case THROW_ITEM
                    -> variant = CrosshairVariant.HoldingThrowable;

            case PLACE_BLOCK,
                 PLACE_ENTITY
                    -> variant = CrosshairVariant.HoldingBlock;

            case INTERACT_WITH_BLOCK,
                 TAKE_ITEM_FROM_BLOCK,
                 MOUNT_BLOCK,
                 TAKE_ITEM_FROM_ENTITY,
                 INTERACT_WITH_ENTITY,
                 MOUNT_ENTITY
                    -> modifierUse = ModifierUse.INTERACTABLE;

            case RANGED_WEAPON, RANGED_WEAPON_CHARGING -> variant = CrosshairVariant.Regular;
            case RANGED_WEAPON_CHARGED -> variant = CrosshairVariant.HoldingRangedWeapon;
            case USABLE_TOOL -> { variant = CrosshairVariant.HoldingTool; modifierUse = ModifierUse.USE_ITEM; }
            case SHIELD -> modifierUse = ModifierUse.SHIELD;

            case FORCE_REGULAR_CROSSHAIR -> modifierHit = ModifierHit.NONE;
        }
    }

    public boolean hasStyle() {
        return variant != CrosshairVariant.NONE;
    }

    public CrosshairStyle getCrosshairStyle() {
        return switch (variant) {
            case Regular, NONE -> DynamicCrosshairMod.config.getCrosshairStyleRegular();
            case OnBlock -> DynamicCrosshairMod.config.getCrosshairStyleOnBlock();
            case OnEntity -> DynamicCrosshairMod.config.getCrosshairStyleOnEntity();
            case HoldingBlock -> DynamicCrosshairMod.config.getCrosshairStyleHoldingBlock();
            case HoldingTool -> DynamicCrosshairMod.config.getCrosshairStyleHoldingTool();
            case HoldingMeleeWeapon -> DynamicCrosshairMod.config.getCrosshairStyleHoldingMeleeWeapon();
            case HoldingRangedWeapon -> DynamicCrosshairMod.config.getCrosshairStyleHoldingRangedWeapon();
            case HoldingThrowable -> DynamicCrosshairMod.config.getCrosshairStyleHoldingThrowable();
        };
    }

    public List<CrosshairModifier> getModifiers() {
        List<CrosshairModifier> modifiers = new LinkedList<>();
        if (DynamicCrosshairMod.config.dynamicCrosshairDisplayCorrectTool()) {
            switch (modifierHit) {
                case CORRECT_TOOL -> modifiers.add(DynamicCrosshairMod.config.getCrosshairModifierCorrectTool());
                case INCORRECT_TOOL -> modifiers.add(DynamicCrosshairMod.config.getCrosshairModifierIncorrectTool());
            }
        }
        switch (modifierUse) {
            case USE_ITEM -> modifiers.add(DynamicCrosshairMod.config.getCrosshairModifierUsableItem());
            case INTERACTABLE -> modifiers.add(DynamicCrosshairMod.config.getCrosshairModifierInteractable());
            case SHIELD -> modifiers.add(DynamicCrosshairMod.config.getCrosshairModifierShield());
        }
        return modifiers;
    }
}
