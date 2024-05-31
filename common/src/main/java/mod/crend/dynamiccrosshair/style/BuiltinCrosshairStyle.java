package mod.crend.dynamiccrosshair.style;

import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairStyles;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuiltinCrosshairStyle extends AbstractCrosshairStyle {

	public static final Map<Identifier, BuiltinCrosshairStyle> BUILTIN_STYLES = Stream.of(
					DynamicCrosshairStyles.DEFAULT,
					DynamicCrosshairStyles.CROSS_OPEN,
					DynamicCrosshairStyles.CROSS_OPEN_DIAGONAL,
					DynamicCrosshairStyles.CIRCLE,
					DynamicCrosshairStyles.CIRCLE_LARGE,
					DynamicCrosshairStyles.SQUARE,
					DynamicCrosshairStyles.SQUARE_LARGE,
					DynamicCrosshairStyles.DIAMOND,
					DynamicCrosshairStyles.DIAMOND_LARGE,
					DynamicCrosshairStyles.CARET,
					DynamicCrosshairStyles.DOT,
					DynamicCrosshairStyles.CROSS_DIAGONAL_SMALL,
					DynamicCrosshairStyles.BRACKETS,
					DynamicCrosshairStyles.BRACKETS_BOTTOM,
					DynamicCrosshairStyles.BRACKETS_TOP,
					DynamicCrosshairStyles.BRACKETS_ROUND,
					DynamicCrosshairStyles.LINES,
					DynamicCrosshairStyles.LINE_BOTTOM
			).collect(Collectors.toMap(Function.identity(), BuiltinCrosshairStyle::new, (v1, v2) -> v1, LinkedHashMap::new));

	public BuiltinCrosshairStyle(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean isCustom() {
		return false;
	}

	@Override
	public void draw(DrawContext context, int x, int y) {
		context.drawGuiTexture(identifier, x, y, 15, 15);
	}
}
