package mod.crend.dynamiccrosshair.config;

public record CrosshairColor(CrosshairConfigColor color, int customColor, boolean forced) {
	public int getColor() {
		return (color == CrosshairConfigColor.Custom ? customColor : color.color);
	}
}
