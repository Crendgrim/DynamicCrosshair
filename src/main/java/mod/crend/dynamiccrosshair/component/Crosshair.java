package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.config.CrosshairModifier;
import mod.crend.dynamiccrosshair.config.CrosshairStyle;

import java.util.LinkedList;
import java.util.List;

public class Crosshair implements Cloneable {
    public static final Crosshair REGULAR = new Crosshair(Style.Regular);
    public static final Crosshair HOLDING_BLOCK = new Crosshair(Style.HoldingBlock);
    public static final Crosshair RANGED_WEAPON = new Crosshair(Style.HoldingRangedWeapon);
    public static final Crosshair THROWABLE = new Crosshair(Style.HoldingThrowable);
    public static final Crosshair TOOL = new Crosshair(Style.HoldingTool);
    public static final Crosshair CORRECT_TOOL = new Crosshair(Style.HoldingTool, ModifierHit.CORRECT_TOOL);
    public static final Crosshair INCORRECT_TOOL = new Crosshair(Style.HoldingTool, ModifierHit.INCORRECT_TOOL);
    public static final Crosshair USE_ITEM = new Crosshair(ModifierUse.USE_ITEM);
    public static final Crosshair INTERACTABLE = new Crosshair(ModifierUse.INTERACTABLE);

    private Style style = Style.NONE;
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
    public Crosshair(Style holdingBlock, ModifierUse modifierUse) {
        this.setStyle(holdingBlock);
        this.setModifierUse(modifierUse);
    }
    public Crosshair(Style holdingBlock, ModifierHit modifierHit) {
        this.setStyle(holdingBlock);
        this.setModifierHit(modifierHit);
    }

    public boolean hasStyle() {
        return style != Style.NONE;
    }
    public Style getStyle() {
        return style;
    }
    public CrosshairStyle getCrosshairStyle() {
        return switch (style) {
            case Regular, NONE -> DynamicCrosshair.config.getCrosshairStyleRegular();
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

    public boolean updateFrom(Crosshair other) {
        if (other == null) return false;
        boolean ret = false;
        if (other.style != Style.NONE) {
            setStyle(other.style);
            ret = true;
        }
        if (this.modifierHit == ModifierHit.NONE && other.modifierHit != ModifierHit.NONE) {
            setModifierHit(other.modifierHit);
            ret = true;
        }
        if (this.modifierUse == ModifierUse.NONE && other.modifierUse != ModifierUse.NONE) {
            setModifierUse(other.modifierUse);
            ret = true;
        }
        return ret;
    }
    public static Crosshair combine(Crosshair one, Crosshair other) {
        if (one == null) return other;
        Crosshair combined = one.clone();
        combined.updateFrom(other);
        return combined;
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

    @Override
    public Crosshair clone() {
        try {
            return (Crosshair) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
