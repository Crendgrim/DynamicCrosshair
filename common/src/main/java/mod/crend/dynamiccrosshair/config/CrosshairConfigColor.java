package mod.crend.dynamiccrosshair.config;

public enum CrosshairConfigColor {
	Unchanged(0xFFFFFFFF),
	Red(0xFFFF0000),
	Yellow(0xFFAAAA00),
	Green(0xFF00FF00),
	Cyan(0xFF00AAAA),
	Blue(0xFF0000FF),
	Purple(0xFFAA00AA),
	Custom(1);

	public final int color;

	CrosshairConfigColor(int color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "text.dynamiccrosshair.color." + name();
	}
}
