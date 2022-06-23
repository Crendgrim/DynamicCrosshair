package mod.crend.dynamiccrosshair.config;

import mod.crend.dynamiccrosshair.DynamicCrosshair;

public record CrosshairColor(CrosshairConfigColor color, int customColor, boolean forced) {
	public int getColor() {
		return switch (color) {
			case Unchanged -> getDefaultColor();
			case Custom -> customColor;
			default -> color.color;
		};
	}
	private static int getDefaultColor() {
		CrosshairColor defaultColor = DynamicCrosshair.config.getColor();
		return (defaultColor.color() == CrosshairConfigColor.Custom ? defaultColor.customColor : defaultColor.color.color);
	}
}
