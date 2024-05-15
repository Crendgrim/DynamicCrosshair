package mod.crend.dynamiccrosshair.api;

public record Crosshair(
        CrosshairVariant variant,
        ModifierUse modifierUse,
        ModifierHit modifierHit,
        InteractionMode interactionMode,
        boolean changed
){
    public static final Crosshair NONE = new Crosshair();
    public static final Crosshair REGULAR = new Crosshair(CrosshairVariant.Regular, InteractionMode.NONE);
    public static final Crosshair REGULAR_FIXED = new Crosshair(CrosshairVariant.Regular, InteractionMode.BOTH);
    public static final Crosshair HOLDING_BLOCK = new Crosshair(CrosshairVariant.HoldingBlock, InteractionMode.RIGHT_CLICK);
    public static final Crosshair MELEE_WEAPON = new Crosshair(CrosshairVariant.HoldingMeleeWeapon, InteractionMode.LEFT_CLICK);
    public static final Crosshair RANGED_WEAPON = new Crosshair(CrosshairVariant.HoldingRangedWeapon, InteractionMode.RIGHT_CLICK);
    public static final Crosshair THROWABLE = new Crosshair(CrosshairVariant.HoldingThrowable, InteractionMode.RIGHT_CLICK);
    public static final Crosshair TOOL = new Crosshair(CrosshairVariant.HoldingTool, InteractionMode.LEFT_CLICK);
    public static final Crosshair CORRECT_TOOL = new Crosshair(CrosshairVariant.HoldingTool, ModifierHit.CORRECT_TOOL);
    public static final Crosshair INCORRECT_TOOL = new Crosshair(CrosshairVariant.HoldingTool, ModifierHit.INCORRECT_TOOL);
    public static final Crosshair USABLE = new Crosshair(ModifierUse.USE_ITEM);
    public static final Crosshair INTERACTABLE = new Crosshair(ModifierUse.INTERACTABLE);
    public static final Crosshair SHIELD = new Crosshair(ModifierUse.SHIELD);

    public Crosshair() {
        this(CrosshairVariant.NONE, ModifierUse.NONE, ModifierHit.NONE, InteractionMode.NONE, false);
    }
    public Crosshair(CrosshairVariant variant, InteractionMode interactionMode) {
        this(variant, ModifierUse.NONE, ModifierHit.NONE, interactionMode, true);
    }
    public Crosshair(ModifierUse modifierUse) {
        this(CrosshairVariant.NONE, modifierUse, ModifierHit.NONE, InteractionMode.RIGHT_CLICK, true);
    }
    public Crosshair(ModifierHit modifierHit) {
        this(CrosshairVariant.NONE, ModifierUse.NONE, modifierHit, InteractionMode.LEFT_CLICK, true);
    }
    public Crosshair(CrosshairVariant variant, ModifierUse modifierUse) {
        this(variant, modifierUse, ModifierHit.NONE,  InteractionMode.RIGHT_CLICK, true);
    }
    public Crosshair(CrosshairVariant variant, ModifierHit modifierHit) {
        this(variant, ModifierUse.NONE, modifierHit, InteractionMode.LEFT_CLICK, true);
    }
    public Crosshair(CrosshairVariant variant, ModifierUse modifierUse, ModifierHit modifierHit) {
        this(variant, modifierUse, modifierHit, InteractionMode.BOTH, true);
    }

    public static Crosshair of(InteractionType interactionType) {
        CrosshairVariant variant = CrosshairVariant.NONE;
        ModifierUse modifierUse = ModifierUse.NONE;
        ModifierHit modifierHit = ModifierHit.NONE;
        boolean changed = true;
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
            case EMPTY, NO_ACTION -> changed = false;
        }
        return new Crosshair(variant, modifierUse, modifierHit, interactionType.interactionMode, changed);
    }


    public boolean hasStyle() {
        return variant != CrosshairVariant.NONE;
    }
    public boolean hasModifierUse() {
        return modifierUse != ModifierUse.NONE;
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

    public Crosshair withModifier(ModifierUse modifier) {
        return new Crosshair(variant, modifier, modifierHit, interactionMode.add(InteractionMode.RIGHT_CLICK), true);
    }
    public Crosshair withModifier(ModifierHit modifier) {
        return new Crosshair(variant, modifierUse, modifier, interactionMode.add(InteractionMode.LEFT_CLICK), true);
    }

    public Crosshair withVariant(CrosshairVariant variant) {
        return new Crosshair(variant, modifierUse, modifierHit, interactionMode, true);
    }

    public Crosshair updateFrom(Crosshair other) {
        if (other == null) return this;
        return switch (interactionMode) {
            case LEFT_CLICK ->
                    switch (other.interactionMode) {
                        case LEFT_CLICK -> {
                            if (this.modifierHit == ModifierHit.NONE && other.modifierHit != ModifierHit.NONE) {
                                yield new Crosshair(variant, modifierUse, other.modifierHit, InteractionMode.LEFT_CLICK, true);
                            }
                            yield this;
                        }
                        case RIGHT_CLICK ->
								new Crosshair(variant, other.modifierUse, modifierHit, InteractionMode.BOTH, true);
                        case BOTH -> {
                            if (this.modifierHit == ModifierHit.NONE && other.modifierHit != ModifierHit.NONE) {
                                yield new Crosshair(variant, other.modifierUse, other.modifierHit, InteractionMode.BOTH, true);
                            }
                            yield new Crosshair(variant, other.modifierUse, modifierHit, InteractionMode.BOTH, true);
                        }
                        case NONE -> this;
                    };
            case RIGHT_CLICK -> {
                if (this.hasStyle()) yield this;
                if (this.modifierHit == ModifierHit.NONE && other.modifierHit != ModifierHit.NONE) {
                    yield new Crosshair(other.variant, modifierUse, other.modifierHit, other.interactionMode.add(InteractionMode.RIGHT_CLICK), true);
                }
                yield new Crosshair(other.variant, modifierUse, modifierHit, other.interactionMode.add(InteractionMode.RIGHT_CLICK), true);
            }
            case BOTH -> {
                if (this.modifierHit == ModifierHit.NONE && other.modifierHit != ModifierHit.NONE) {
                    yield new Crosshair(variant, modifierUse, other.modifierHit, InteractionMode.BOTH, true);
                }
                yield this;
            }
            case NONE -> other;
        };
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
                ", changed=" + changed +
                '}';
    }
}
