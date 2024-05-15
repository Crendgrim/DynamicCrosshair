package mod.crend.dynamiccrosshair.api;

public enum InteractionType {
	USE_ITEM(InteractionMode.RIGHT_CLICK),
	EQUIP_ITEM(InteractionMode.RIGHT_CLICK),
	CONSUME_ITEM(InteractionMode.RIGHT_CLICK),
	THROW_ITEM(InteractionMode.RIGHT_CLICK),
	CHARGE_ITEM(InteractionMode.RIGHT_CLICK),

	PLACE_BLOCK(InteractionMode.RIGHT_CLICK),
	INTERACT_WITH_BLOCK(InteractionMode.RIGHT_CLICK),
	USE_ITEM_ON_BLOCK(InteractionMode.RIGHT_CLICK),
	PLACE_ITEM_ON_BLOCK(InteractionMode.RIGHT_CLICK),
	TAKE_ITEM_FROM_BLOCK(InteractionMode.RIGHT_CLICK),
	FILL_ITEM_FROM_BLOCK(InteractionMode.RIGHT_CLICK),
	FILL_BLOCK_FROM_ITEM(InteractionMode.RIGHT_CLICK),
	USE_BLOCK(InteractionMode.RIGHT_CLICK),
	MOUNT_BLOCK(InteractionMode.RIGHT_CLICK),

	PLACE_ENTITY(InteractionMode.RIGHT_CLICK),
	INTERACT_WITH_ENTITY(InteractionMode.RIGHT_CLICK),
	PICK_UP_ENTITY(InteractionMode.RIGHT_CLICK),
	USE_ITEM_ON_ENTITY(InteractionMode.RIGHT_CLICK),
	PLACE_ITEM_ON_ENTITY(InteractionMode.RIGHT_CLICK),
	TAKE_ITEM_FROM_ENTITY(InteractionMode.RIGHT_CLICK),
	FILL_ITEM_FROM_ENTITY(InteractionMode.RIGHT_CLICK),
	FILL_ENTITY_FROM_ITEM(InteractionMode.RIGHT_CLICK),
	MOUNT_ENTITY(InteractionMode.RIGHT_CLICK),

	MELEE_WEAPON(InteractionMode.LEFT_CLICK),
	RANGED_WEAPON(InteractionMode.RIGHT_CLICK),
	USABLE_TOOL(InteractionMode.BOTH),
	TOOL(InteractionMode.LEFT_CLICK),
	CORRECT_TOOL(InteractionMode.LEFT_CLICK),
	INCORRECT_TOOL(InteractionMode.LEFT_CLICK),
	SHIELD(InteractionMode.RIGHT_CLICK),

	/** Force a regular crosshair to be displayed */
	FORCE_CROSSHAIR(InteractionMode.LEFT_CLICK),
	/** No interaction specified, use tags for evaluation */
	EMPTY(InteractionMode.NONE),
	/** No interaction, do not fall back to tags */
	NO_ACTION(InteractionMode.NONE);

	public final InteractionMode interactionMode;
	InteractionType(InteractionMode interactionMode) {
		this.interactionMode = interactionMode;
	}
}
