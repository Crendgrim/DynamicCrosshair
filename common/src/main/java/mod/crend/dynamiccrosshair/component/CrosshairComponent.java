package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.InteractionType;
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
    public static final Crosshair REGULAR = new Crosshair(CrosshairVariant.Regular);
    public static final Crosshair HOLDING_BLOCK = new Crosshair(CrosshairVariant.HoldingBlock).setFlag(Flag.FixedAll);
    public static final Crosshair MELEE_WEAPON = new Crosshair(CrosshairVariant.HoldingMeleeWeapon).setFlag(Flag.FixedStyle);
    public static final Crosshair RANGED_WEAPON = new Crosshair(CrosshairVariant.HoldingRangedWeapon).setFlag(Flag.FixedAll);
    public static final Crosshair THROWABLE = new Crosshair(CrosshairVariant.HoldingThrowable).setFlag(Flag.FixedAll);
    public static final Crosshair TOOL = new Crosshair(CrosshairVariant.HoldingTool).setFlag(Flag.FixedStyle);
    public static final Crosshair CORRECT_TOOL = new Crosshair(CrosshairVariant.HoldingTool, ModifierHit.CORRECT_TOOL).setFlag(Flag.FixedStyle, Flag.FixedModifierHit);
    public static final Crosshair INCORRECT_TOOL = new Crosshair(CrosshairVariant.HoldingTool, ModifierHit.INCORRECT_TOOL).setFlag(Flag.FixedStyle, Flag.FixedModifierHit);
    public static final Crosshair USABLE = new Crosshair(ModifierUse.USE_ITEM).setFlag(Flag.FixedModifierUse);
    public static final Crosshair INTERACTABLE = new Crosshair(ModifierUse.INTERACTABLE).setFlag(Flag.FixedModifierUse);
    public static final Crosshair SHIELD = new Crosshair(ModifierUse.SHIELD).setFlag(Flag.FixedModifierUse);

    private CrosshairVariant variant = CrosshairVariant.NONE;
    private ModifierUse modifierUse = ModifierUse.NONE;
    private ModifierHit modifierHit = ModifierHit.NONE;
    private boolean lockStyle = false;
    private boolean lockModifierUse = false;
    private boolean lockModifierHit = false;

    boolean changed = false;

    public Crosshair() { }
    public Crosshair(CrosshairVariant variant) {
        this.setVariant(variant);
    }
    public Crosshair(ModifierUse modifierUse) {
        this.setModifierUse(modifierUse);
    }
    public Crosshair(ModifierHit modifierHit) {
        this.setModifierHit(modifierHit);
    }
    public Crosshair(CrosshairVariant holdingBlock, ModifierUse modifierUse) {
        this.setVariant(holdingBlock);
        this.setModifierUse(modifierUse);
    }
    public Crosshair(CrosshairVariant holdingBlock, ModifierHit modifierHit) {
        this.setVariant(holdingBlock);
        this.setModifierHit(modifierHit);
    }
    public Crosshair(InteractionType interactionType) {
        switch (interactionType) {
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
                        -> this.setModifierUse(ModifierUse.USE_ITEM);
            case
                THROW_ITEM
                        -> this.setVariant(CrosshairVariant.HoldingThrowable);

            case
                PLACE_BLOCK,
                PLACE_ENTITY
                        -> this.setVariant(CrosshairVariant.HoldingBlock);

            case
                INTERACT_WITH_BLOCK,
                TAKE_ITEM_FROM_BLOCK,
                MOUNT_BLOCK,
                TAKE_ITEM_FROM_ENTITY,
                INTERACT_WITH_ENTITY,
                MOUNT_ENTITY
                        -> this.setModifierUse(ModifierUse.INTERACTABLE);

            case MELEE_WEAPON -> this.setVariant(CrosshairVariant.HoldingMeleeWeapon);
            case RANGED_WEAPON -> this.setVariant(CrosshairVariant.HoldingRangedWeapon);
            case TOOL -> this.setVariant(CrosshairVariant.HoldingTool);
            case USABLE_TOOL -> { this.setVariant(CrosshairVariant.HoldingTool); this.setModifierUse(ModifierUse.USE_ITEM); }
            case CORRECT_TOOL -> { this.setVariant(CrosshairVariant.HoldingTool); this.setModifierHit(ModifierHit.CORRECT_TOOL); }
            case INCORRECT_TOOL -> { this.setVariant(CrosshairVariant.HoldingTool); this.setModifierHit(ModifierHit.INCORRECT_TOOL); }
            case SHIELD -> this.setModifierUse(ModifierUse.SHIELD);

            case FORCE_CROSSHAIR -> this.setVariant(CrosshairVariant.Regular);
            case EMPTY, NO_ACTION -> { }
        }
    }


    public boolean hasStyle() {
        return variant != CrosshairVariant.NONE;
    }
    public boolean isLockedStyle() {
        return lockStyle;
    }
    public boolean hasModifierUse() {
        return modifierUse != ModifierUse.NONE;
    }
    public boolean isLockedModifierUse() {
        return lockModifierUse;
    }

    public CrosshairStyle getCrosshairStyle() {
        return switch (variant) {
            case Regular, NONE -> DynamicCrosshair.config.getCrosshairStyleRegular();
            case OnBlock -> DynamicCrosshair.config.getCrosshairStyleOnBlock();
            case OnEntity -> DynamicCrosshair.config.getCrosshairStyleOnEntity();
            case HoldingBlock -> DynamicCrosshair.config.getCrosshairStyleHoldingBlock();
            case HoldingTool -> DynamicCrosshair.config.getCrosshairStyleHoldingTool();
            case HoldingMeleeWeapon -> DynamicCrosshair.config.getCrosshairStyleHoldingMeleeWeapon();
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

    void setVariant(CrosshairVariant variant) {
        this.variant = variant;
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

    public Crosshair withModifier(ModifierUse modifier) {
        Crosshair crosshair = clone();
        crosshair.setModifierUse(modifier);
        return crosshair;
    }

    boolean updateFrom(Crosshair other) {
        if (other == null) return false;
        boolean ret = false;
        if (!this.lockStyle || other.variant == CrosshairVariant.HoldingTool) {
            if (other.variant != CrosshairVariant.NONE) {
                setVariant(other.variant);
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
        boolean modifierIsModifier = switch (modifierUse) {
            case SHIELD -> DynamicCrosshair.config.getCrosshairModifierShield().isModifier();
            case USE_ITEM -> DynamicCrosshair.config.getCrosshairModifierUsableItem().isModifier();
            case INTERACTABLE -> DynamicCrosshair.config.getCrosshairModifierInteractable().isModifier();
            case NONE -> switch (modifierHit) {
                case CORRECT_TOOL -> DynamicCrosshair.config.getCrosshairModifierCorrectTool().isModifier();
                case INCORRECT_TOOL -> DynamicCrosshair.config.getCrosshairModifierIncorrectTool().isModifier();
                case NONE -> true;
            };
        };
        if (!modifierIsModifier) {
            setVariant(CrosshairVariant.NONE);
            this.lockStyle = true;
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
        if (DynamicCrosshair.config.dynamicCrosshairDisplayCorrectTool()) {
            switch (modifierHit) {
                case CORRECT_TOOL -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierCorrectTool());
                case INCORRECT_TOOL -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierIncorrectTool());
            }
        }
        switch (modifierUse) {
            case USE_ITEM -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierUsableItem());
            case INTERACTABLE -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierInteractable());
            case SHIELD -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierShield());
        }
        return modifiers;
    }

    @Override
    public String toString() {
        return "Crosshair{" +
                "variant=" + variant +
                ", modifierUse=" + modifierUse +
                ", modifierHit=" + modifierHit +
                ", lockStyle=" + lockStyle +
                ", lockModifierUse=" + lockModifierUse +
                ", lockModifierHit=" + lockModifierHit +
                ", changed=" + changed +
                '}';
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
