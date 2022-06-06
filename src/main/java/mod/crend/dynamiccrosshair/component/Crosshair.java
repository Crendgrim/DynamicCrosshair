package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.config.CrosshairModifier;
import mod.crend.dynamiccrosshair.config.CrosshairStyle;

import java.util.LinkedList;
import java.util.List;

public class Crosshair implements Cloneable {

    public enum Flag {
        FixedStyle,
        FixedModifierUse,
        FixedModifierHit,
        FixedAll
    }

    public static final Crosshair NONE = new Crosshair();
    public static final Crosshair REGULAR = new Crosshair(Style.Regular);
    public static final Crosshair HOLDING_BLOCK = new Crosshair(Style.HoldingBlock).setFlag(Flag.FixedAll);
    public static final Crosshair RANGED_WEAPON = new Crosshair(Style.HoldingRangedWeapon).setFlag(Flag.FixedAll);
    public static final Crosshair THROWABLE = new Crosshair(Style.HoldingThrowable).setFlag(Flag.FixedAll);
    public static final Crosshair TOOL = new Crosshair(Style.HoldingTool).setFlag(Flag.FixedStyle);
    public static final Crosshair CORRECT_TOOL = new Crosshair(Style.HoldingTool, ModifierHit.CORRECT_TOOL).setFlag(Flag.FixedStyle, Flag.FixedModifierHit);
    public static final Crosshair INCORRECT_TOOL = new Crosshair(Style.HoldingTool, ModifierHit.INCORRECT_TOOL).setFlag(Flag.FixedStyle, Flag.FixedModifierHit);
    public static final Crosshair USE_ITEM = new Crosshair(ModifierUse.USE_ITEM).setFlag(Flag.FixedModifierUse);
    public static final Crosshair INTERACTABLE = new Crosshair(ModifierUse.INTERACTABLE).setFlag(Flag.FixedModifierUse);

    private Style style = Style.NONE;
    private ModifierUse modifierUse = ModifierUse.NONE;
    private ModifierHit modifierHit = ModifierHit.NONE;
    private boolean lockStyle = false;
    private boolean lockModifierUse = false;
    private boolean lockModifierHit = false;

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

    void setStyle(Style style) {
        this.style = style;
        this.changed = true;
    }

    void setModifierHit(ModifierHit modifierHit) {
        this.modifierHit = modifierHit;
        this.changed = true;
    }

    void setModifierUse(ModifierUse modifierUse) {
        this.modifierUse = modifierUse;
        this.changed = true;
    }

    Crosshair setFlag(Flag flag) {
        switch (flag) {
            case FixedStyle -> lockStyle = true;
            case FixedModifierUse -> lockModifierUse = true;
            case FixedModifierHit -> lockModifierHit = true;
            case FixedAll -> {
                lockStyle = true;
                lockModifierUse = true;
                lockModifierHit = true;
            }
        }
        return this;
    }
    Crosshair setFlag(Flag... flags) {
        for (Flag flag : flags) {
            setFlag(flag);
        }
        return this;
    }

    public Crosshair withFlag(Flag flag) {
        return clone().setFlag(flag);
    }
    public Crosshair withFlag(Flag... flags) {
        return clone().setFlag(flags);
    }

    boolean updateFrom(Crosshair other) {
        if (other == null) return false;
        boolean ret = false;
        if (!this.lockStyle || other.style == Style.HoldingTool) {
            if (other.style != Style.NONE) {
                setStyle(other.style);
                ret = true;
            }
            this.lockStyle = other.lockStyle;
        }
        if (!this.lockModifierHit) {
            if (other.modifierHit != ModifierHit.NONE) {
                setModifierHit(other.modifierHit);
                ret = true;
            }
            this.lockModifierHit = other.lockModifierHit;
        }
        if (!this.lockModifierUse) {
            if (other.modifierUse != ModifierUse.NONE) {
                setModifierUse(other.modifierUse);
                ret = true;
            }
            this.lockModifierUse = other.lockModifierUse;
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
