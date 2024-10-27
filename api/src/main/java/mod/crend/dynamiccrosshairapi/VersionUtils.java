package mod.crend.dynamiccrosshairapi;

import net.minecraft.util.Identifier;

public class VersionUtils {
	public static Identifier getIdentifier(String identifier) {
		//? if <1.21 {
		return new Identifier(identifier);
		//?} else {
		/*return Identifier.of(identifier);
		 *///?}
	}

	public static Identifier getIdentifier(String namespace, String path) {
		//? if <1.21 {
		return new Identifier(namespace, path);
		//?} else {
		/*return Identifier.of(namespace, path);
		 *///?}
	}

	public static Identifier getVanillaIdentifier(String path) {
		//? if <1.21 {
		return new Identifier(Identifier.DEFAULT_NAMESPACE, path);
		//?} else {
		/*return Identifier.ofVanilla(path);
		 *///?}
	}
}
