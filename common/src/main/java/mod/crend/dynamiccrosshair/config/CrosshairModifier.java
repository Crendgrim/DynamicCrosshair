package mod.crend.dynamiccrosshair.config;

public record CrosshairModifier(Config.CrosshairModifierSettings settings) {
	public CrosshairConfigModifier getStyle() {
		return settings.style;
	}

	public CrosshairColor getColor() {
		return new CrosshairColor(settings.color.crosshairColor, settings.color.customColor.getRGB(), settings.color.forceColor);
	}
}
