package mod.crend.dynamiccrosshairapi;

import net.minecraft.util.Identifier;

public class DynamicCrosshair {
	public static final String MOD_ID = "dynamiccrosshair";
	public static final String API_MOD_ID = "dynamiccrosshairapi";

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
