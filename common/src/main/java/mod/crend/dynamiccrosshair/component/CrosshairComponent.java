package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.Crosshair;
import mod.crend.dynamiccrosshair.config.CrosshairModifier;
import mod.crend.dynamiccrosshair.config.CrosshairStyle;

import java.util.LinkedList;
import java.util.List;

public class CrosshairComponent {

    private final Crosshair crosshair;

    public CrosshairComponent(Crosshair crosshair) {
        this.crosshair = crosshair;
    }

    public boolean hasStyle() {
        return crosshair.hasStyle();
    }
    public boolean isLockedStyle() {
        return crosshair.isLockedStyle();
    }
    public boolean hasModifierUse() {
        return crosshair.hasModifierUse();
    }
    public boolean isLockedModifierUse() {
        return crosshair.isLockedModifierUse();
    }

    public CrosshairStyle getCrosshairStyle() {
        return switch (crosshair.getVariant()) {
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

    public boolean isChanged() {
        return crosshair.isChanged();
    }

    public List<CrosshairModifier> getModifiers() {
        List<CrosshairModifier> modifiers = new LinkedList<>();
        if (DynamicCrosshairMod.config.dynamicCrosshairDisplayCorrectTool()) {
            switch (crosshair.getModifierHit()) {
                case CORRECT_TOOL -> modifiers.add(DynamicCrosshairMod.config.getCrosshairModifierCorrectTool());
                case INCORRECT_TOOL -> modifiers.add(DynamicCrosshairMod.config.getCrosshairModifierIncorrectTool());
            }
        }
        switch (crosshair.getModifierUse()) {
            case USE_ITEM -> modifiers.add(DynamicCrosshairMod.config.getCrosshairModifierUsableItem());
            case INTERACTABLE -> modifiers.add(DynamicCrosshairMod.config.getCrosshairModifierInteractable());
            case SHIELD -> modifiers.add(DynamicCrosshairMod.config.getCrosshairModifierShield());
        }
        return modifiers;
    }
}
