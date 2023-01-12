package mod.crend.dynamiccrosshair.config;

public enum CrosshairMode {
	Disabled,
	Simple,
	Advanced;

	@Override
	public String toString() {
		return "text.dynamiccrosshair.option.mode." + name();
	}
}
