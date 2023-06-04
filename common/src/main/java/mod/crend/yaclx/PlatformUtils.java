package mod.crend.yaclx;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.Set;

@SuppressWarnings("unused")
public class PlatformUtils {
	@ExpectPlatform
	public static boolean isModLoaded(String modid) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Path resolveConfigFile(String configName) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Class<?> getModdedItemTagsClass() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Set<Identifier> getItemsFromTag(TagKey<Item> itemTagKey) {
		throw new AssertionError();
	}
}
