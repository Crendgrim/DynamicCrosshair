package mod.crend.dynamiccrosshair.style;

import net.minecraft.util.Identifier;

public record CrosshairStyle(
		Identifier identifier,
		int color,
		boolean enableBlend,
		boolean coalesce
) {
}
