package mod.crend.dynamiccrosshairapi.interaction;

public enum InteractionType {
	USE_ITEM(InteractionMode.SECONDARY),
	EQUIP_ITEM(InteractionMode.SECONDARY),
	CONSUME_ITEM(InteractionMode.SECONDARY),
	THROW_ITEM(InteractionMode.SECONDARY),
	CHARGE_ITEM(InteractionMode.SECONDARY),
	SPYGLASS(InteractionMode.SECONDARY),

	TARGET_BLOCK(InteractionMode.PRIMARY),
	PLACE_BLOCK(InteractionMode.SECONDARY),
	INTERACT_WITH_BLOCK(InteractionMode.SECONDARY),
	USE_ITEM_ON_BLOCK(InteractionMode.SECONDARY),
	PLACE_ITEM_ON_BLOCK(InteractionMode.SECONDARY),
	TAKE_ITEM_FROM_BLOCK(InteractionMode.SECONDARY),
	FILL_ITEM_FROM_BLOCK(InteractionMode.SECONDARY),
	FILL_BLOCK_FROM_ITEM(InteractionMode.SECONDARY),
	USE_BLOCK(InteractionMode.SECONDARY),
	MOUNT_BLOCK(InteractionMode.SECONDARY),

	TARGET_ENTITY(InteractionMode.PRIMARY),
	PLACE_ENTITY(InteractionMode.SECONDARY),
	INTERACT_WITH_ENTITY(InteractionMode.SECONDARY),
	PICK_UP_ENTITY(InteractionMode.SECONDARY),
	USE_ITEM_ON_ENTITY(InteractionMode.SECONDARY),
	PLACE_ITEM_ON_ENTITY(InteractionMode.SECONDARY),
	TAKE_ITEM_FROM_ENTITY(InteractionMode.SECONDARY),
	FILL_ITEM_FROM_ENTITY(InteractionMode.SECONDARY),
	FILL_ENTITY_FROM_ITEM(InteractionMode.SECONDARY),
	MOUNT_ENTITY(InteractionMode.SECONDARY),

	MELEE_WEAPON(InteractionMode.PRIMARY),
	RANGED_WEAPON(InteractionMode.SECONDARY),
	RANGED_WEAPON_CHARGING(InteractionMode.SECONDARY),
	RANGED_WEAPON_CHARGED(InteractionMode.SECONDARY),
	USABLE_TOOL(InteractionMode.BOTH),
	TOOL(InteractionMode.PRIMARY),
	CORRECT_TOOL(InteractionMode.PRIMARY),
	INCORRECT_TOOL(InteractionMode.PRIMARY),
	SHIELD(InteractionMode.SECONDARY),

	/** Force a regular crosshair to be displayed */
	FORCE_REGULAR_CROSSHAIR(InteractionMode.BOTH),
	/** No interaction specified, use tags for evaluation */
	EMPTY(InteractionMode.NONE),
	/** No interaction, do not fall back to tags */
	NO_ACTION(InteractionMode.NONE);

	public final InteractionMode interactionMode;
	InteractionType(InteractionMode interactionMode) {
		this.interactionMode = interactionMode;
	}

	public InteractionType getPrimaryInteractionType() {
		return switch (interactionMode) {
			case PRIMARY -> this;
			case BOTH -> {
				if (this == USABLE_TOOL) yield TOOL;
				yield this;
			}
			case SECONDARY, NONE -> EMPTY;
		};
	}

	public InteractionType getSecondaryInteractionType() {
		return switch (interactionMode) {
			case SECONDARY -> this;
			case BOTH -> {
				if (this == USABLE_TOOL) yield USE_ITEM_ON_BLOCK;
				yield this;
			}
			case PRIMARY, NONE -> EMPTY;
		};
	}
}
