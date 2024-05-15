package mod.crend.dynamiccrosshair.api;

public record Crosshair(
        CrosshairVariant variant,
        ModifierUse modifierUse,
        ModifierHit modifierHit,
        boolean lockStyle,
        boolean lockModifierUse,
        boolean lockModifierHit,
        boolean changed
){

    public enum Flag {
        FixedStyle,
        FixedModifierUse,
        FixedModifierHit,
        FixedAll
    }

    public static final Crosshair NONE = new Crosshair();
    public static final Crosshair REGULAR = new Crosshair(CrosshairVariant.Regular);
    public static final Crosshair HOLDING_BLOCK = new Crosshair(CrosshairVariant.HoldingBlock).withFlag(Flag.FixedAll);
    public static final Crosshair MELEE_WEAPON = new Crosshair(CrosshairVariant.HoldingMeleeWeapon).withFlag(Flag.FixedStyle);
    public static final Crosshair RANGED_WEAPON = new Crosshair(CrosshairVariant.HoldingRangedWeapon).withFlag(Flag.FixedAll);
    public static final Crosshair THROWABLE = new Crosshair(CrosshairVariant.HoldingThrowable).withFlag(Flag.FixedAll);
    public static final Crosshair TOOL = new Crosshair(CrosshairVariant.HoldingTool).withFlag(Flag.FixedStyle);
    public static final Crosshair CORRECT_TOOL = new Crosshair(CrosshairVariant.HoldingTool, ModifierUse.NONE, ModifierHit.CORRECT_TOOL, true, false, true, true);
    public static final Crosshair INCORRECT_TOOL = new Crosshair(CrosshairVariant.HoldingTool, ModifierUse.NONE, ModifierHit.INCORRECT_TOOL, true, false, true, true);
    public static final Crosshair USABLE = new Crosshair(ModifierUse.USE_ITEM).withFlag(Flag.FixedModifierUse);
    public static final Crosshair INTERACTABLE = new Crosshair(ModifierUse.INTERACTABLE).withFlag(Flag.FixedModifierUse);
    public static final Crosshair SHIELD = new Crosshair(ModifierUse.SHIELD).withFlag(Flag.FixedModifierUse);

    public Crosshair() {
        this(CrosshairVariant.NONE, ModifierUse.NONE, ModifierHit.NONE, false, false, false, false);
    }
    public Crosshair(CrosshairVariant variant) {
        this(variant, ModifierUse.NONE, ModifierHit.NONE, false, false, false, true);
    }
    public Crosshair(ModifierUse modifierUse) {
        this(CrosshairVariant.NONE, modifierUse, ModifierHit.NONE, false, false, false, true);
    }
    public Crosshair(ModifierHit modifierHit) {
        this(CrosshairVariant.NONE, ModifierUse.NONE, modifierHit, false, false, false, true);
    }
    public Crosshair(CrosshairVariant variant, ModifierUse modifierUse) {
        this(variant, modifierUse, ModifierHit.NONE, false, false, false, true);
    }
    public Crosshair(CrosshairVariant variant, ModifierHit modifierHit) {
        this(variant, ModifierUse.NONE, modifierHit, false, false, false, true);
    }
    public Crosshair(CrosshairVariant variant, ModifierUse modifierUse, ModifierHit modifierHit) {
        this(variant, modifierUse, modifierHit, false, false, false, true);
    }

    public static Crosshair of(InteractionType interactionType) {
        CrosshairVariant variant = CrosshairVariant.NONE;
        ModifierUse modifierUse = ModifierUse.NONE;
        ModifierHit modifierHit = ModifierHit.NONE;
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
                        -> modifierUse = ModifierUse.USE_ITEM;
            case
                THROW_ITEM
                        -> variant = CrosshairVariant.HoldingThrowable;

            case
                PLACE_BLOCK,
                PLACE_ENTITY
                        -> variant = CrosshairVariant.HoldingBlock;

            case
                INTERACT_WITH_BLOCK,
                TAKE_ITEM_FROM_BLOCK,
                MOUNT_BLOCK,
                TAKE_ITEM_FROM_ENTITY,
                INTERACT_WITH_ENTITY,
                MOUNT_ENTITY
                        -> modifierUse = ModifierUse.INTERACTABLE;

            case MELEE_WEAPON -> variant = CrosshairVariant.HoldingMeleeWeapon;
            case RANGED_WEAPON -> variant = CrosshairVariant.HoldingRangedWeapon;
            case TOOL -> variant = CrosshairVariant.HoldingTool;
            case USABLE_TOOL -> { variant = CrosshairVariant.HoldingTool; modifierUse = ModifierUse.USE_ITEM; }
            case CORRECT_TOOL -> { variant = CrosshairVariant.HoldingTool; modifierHit = ModifierHit.CORRECT_TOOL; }
            case INCORRECT_TOOL -> { variant = CrosshairVariant.HoldingTool; modifierHit = ModifierHit.INCORRECT_TOOL; }
            case SHIELD -> modifierUse = ModifierUse.SHIELD;

            case FORCE_CROSSHAIR -> variant = CrosshairVariant.Regular;
            case EMPTY, NO_ACTION -> { }
        }
        return new Crosshair(variant, modifierUse, modifierHit);
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


    public ModifierHit getModifierHit() {
        return modifierHit;
    }

    public ModifierUse getModifierUse() {
        return modifierUse;
    }

    public boolean isChanged() {
        return changed;
    }

    public CrosshairVariant getVariant() {
        return this.variant;
    }

    public Crosshair withFlag(Flag flag) {
        return switch (flag) {
            case FixedStyle -> new Crosshair(variant, modifierUse, modifierHit, true, lockModifierUse, lockModifierHit, changed);
            case FixedModifierUse -> new Crosshair(variant, modifierUse, modifierHit, lockStyle, true, lockModifierHit, changed);
            case FixedModifierHit -> new Crosshair(variant, modifierUse, modifierHit, lockStyle, lockModifierUse, true, changed);
            case FixedAll -> new Crosshair(variant, modifierUse, modifierHit, true, true, true, changed);
        };
    }


    public Crosshair withModifier(ModifierUse modifier) {
        return new Crosshair(variant, modifier, modifierHit, lockStyle, lockModifierUse, lockModifierHit, changed);
    }
    public Crosshair withModifier(ModifierHit modifier) {
        return new Crosshair(variant, modifierUse, modifier, lockStyle, lockModifierUse, lockModifierHit, changed);
    }

    public Crosshair withVariant(CrosshairVariant variant) {
        return new Crosshair(variant, modifierUse, modifierHit, lockStyle, lockModifierUse, lockModifierHit, changed);
    }

    public Crosshair updateFrom(Crosshair other) {
        if (other == null) return this;
        CrosshairVariant newVariant = this.variant;
        ModifierUse newModifierUse = this.modifierUse;
        ModifierHit newModifierHit = this.modifierHit;
        boolean newLockStyle = this.lockStyle;
        boolean newLockModifierUse = this.lockModifierUse;
        boolean newLockModifierHit = this.lockModifierHit;
        if (!this.lockStyle || other.variant == CrosshairVariant.HoldingTool) {
            if (other.variant != CrosshairVariant.NONE) {
                newVariant = other.variant;
            }
            newLockStyle = other.lockStyle;
        }
        if (!this.lockModifierHit) {
            if (other.modifierHit != ModifierHit.NONE) {
                newModifierHit = other.modifierHit;
            }
            newLockModifierHit = other.lockModifierHit;
        }
        if (!this.lockModifierUse) {
            if (other.modifierUse != ModifierUse.NONE) {
                newModifierUse = other.modifierUse;
            }
            newLockModifierUse = other.lockModifierUse;
        }
        /* FIXME
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
         */
        return new Crosshair(newVariant, newModifierUse, newModifierHit, newLockStyle, newLockModifierUse, newLockModifierHit, true);
    }
    public static Crosshair combine(Crosshair one, Crosshair other) {
        if (one == null) return other;
        return one.updateFrom(other);
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
}
