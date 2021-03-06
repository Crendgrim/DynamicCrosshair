package mod.crend.dynamiccrosshair.config;

public record CrosshairStyle(Config.CrosshairStyleSettings settings) {
	public CrosshairConfigStyle getStyle() {
		return settings.style;
	}

	public CrosshairColor getColor() {
		return new CrosshairColor(settings.color.crosshairColor, settings.color.customColor, settings.color.forceColor);
	}
}
