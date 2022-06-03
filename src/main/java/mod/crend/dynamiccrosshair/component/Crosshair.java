package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.config.CrosshairModifier;
import mod.crend.dynamiccrosshair.config.CrosshairStyle;

import java.util.LinkedList;
import java.util.List;

public class Crosshair {
    private Style style = Style.Regular;
    private ModifierUse modifierUse = ModifierUse.NONE;
    private ModifierHit modifierHit = ModifierHit.NONE;

    boolean changed = false;

    public Crosshair() { }
    public Crosshair(Style style) {
        this.setStyle(style);
    }
    public Crosshair(ModifierUse modifierUse) {
        this.setModifierUse(modifierUse);
    }
    public Crosshair(ModifierHit modifierHit) {
        this.setModifierHit(modifierHit);
    }

    public Style getStyle() {
        return style;
    }
    public CrosshairStyle getCrosshairStyle() {
        return switch (style) {
            case Regular -> DynamicCrosshair.config.getCrosshairStyleRegular();
            case OnBlock -> DynamicCrosshair.config.getCrosshairStyleOnBlock();
            case OnEntity -> DynamicCrosshair.config.getCrosshairStyleOnEntity();
            case HoldingBlock -> DynamicCrosshair.config.getCrosshairStyleHoldingBlock();
            case HoldingTool -> DynamicCrosshair.config.getCrosshairStyleHoldingTool();
            case HoldingRangedWeapon -> DynamicCrosshair.config.getCrosshairStyleHoldingRangedWeapon();
            case HoldingThrowable -> DynamicCrosshair.config.getCrosshairStyleHoldingThrowable();
        };
    }

    public ModifierHit getModifierHit() {
        return modifierHit;
    }

    public ModifierUse getModifierUse() {
        return modifierUse;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setStyle(Style style) {
        this.style = style;
        this.changed = true;
    }

    public void setModifierHit(ModifierHit modifierHit) {
        this.modifierHit = modifierHit;
        this.changed = true;
    }

    public void setModifierUse(ModifierUse modifierUse) {
        this.modifierUse = modifierUse;
        this.changed = true;
    }

    public List<CrosshairModifier> getModifiers() {
        List<CrosshairModifier> modifiers = new LinkedList<>();
        switch (modifierHit) {
            case CORRECT_TOOL -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierCorrectTool());
            case INCORRECT_TOOL -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierIncorrectTool());
        }
        switch (modifierUse) {
            case USE_ITEM -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierUsableItem());
            case INTERACTABLE -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierInteractable());
        }
        return modifiers;
    }

}
