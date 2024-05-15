package mod.crend.dynamiccrosshair.api;

public enum InteractionMode {
	LEFT_CLICK,
	RIGHT_CLICK,
	BOTH,
	NONE;

	public InteractionMode add(InteractionMode other) {
		return switch (this) {
			case LEFT_CLICK -> {
				if (other == RIGHT_CLICK || other == BOTH) yield BOTH;
				yield this;
			}
			case RIGHT_CLICK -> {
				if (other == LEFT_CLICK || other == BOTH) yield BOTH;
				yield this;
			}
			case BOTH -> BOTH;
			case NONE -> other;
		};
	}

	public boolean isLeftClick() {
		return this == LEFT_CLICK || this == BOTH;
	}

	public boolean isRightClick() {
		return this == RIGHT_CLICK || this == BOTH;
	}
}
