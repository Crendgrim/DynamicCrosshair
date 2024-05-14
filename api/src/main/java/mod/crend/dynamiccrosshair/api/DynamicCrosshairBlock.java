package mod.crend.dynamiccrosshair.api;

public interface DynamicCrosshairBlock {
	/**
	 * Computes the crosshair for the block it is called on
	 * @param context current evaluation context
	 * @return If {@link InteractionType#EMPTY} is returned, the crosshair will be decided based on the block's tags.
	 *         If it should resolve to "no crosshair in current context", return {@link InteractionType#NO_ACTION}
	 *         instead. In other cases, return the appropriate interaction type.
	 */
	InteractionType dynamiccrosshair$compute(CrosshairContext context);
}
