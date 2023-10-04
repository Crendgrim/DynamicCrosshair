package mod.crend.dynamiccrosshair.config;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import net.minecraft.util.Identifier;

public record CrosshairStyle(Config.CrosshairStyleSettings settings) {
	public static final Identifier EMPTY = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/empty");
	public static final Identifier DEFAULT = new Identifier("hud/crosshair");
	public static final Identifier CROSS_OPEN = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/cross-open");
	public static final Identifier CROSS_OPEN_DIAGONAL = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/cross-open-diagonal");
	public static final Identifier CIRCLE = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/circle");
	public static final Identifier SQUARE = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/square");
	public static final Identifier DIAMOND = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/diamond");
	public static final Identifier CARET = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/caret");
	public static final Identifier DOT = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/dot");
	public static final Identifier CROSS_DIAGONAL_SMALL = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/cross-diagonal-small");
	public static final Identifier BRACKETS = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/brackets");
	public static final Identifier BRACKETS_BOTTOM = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/brackets-bottom");
	public static final Identifier BRACKETS_TOP = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/brackets-top");
	public static final Identifier BRACKETS_ROUND = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/brackets-round");
	public static final Identifier LINES = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/lines");
	public static final Identifier LINE_BOTTOM = new Identifier(DynamicCrosshair.MOD_ID, "crosshairs/line-bottom");

	public CrosshairConfigStyle getStyle() {
		return settings.style;
	}

	public CrosshairColor getColor() {
		return new CrosshairColor(settings.color.crosshairColor, settings.color.customColor.getRGB(), settings.color.forceColor);
	}
}
