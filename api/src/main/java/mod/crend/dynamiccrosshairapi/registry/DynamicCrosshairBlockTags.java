package mod.crend.dynamiccrosshairapi.registry;

import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.VersionUtils;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class DynamicCrosshairBlockTags {
	public static final TagKey<Block> INTERACTABLE = of("interactable");
	public static final TagKey<Block> ALWAYS_INTERACTABLE = of("always_interactable");
	public static final TagKey<Block> ALWAYS_INTERACTABLE_IN_CREATIVE_MODE = of("always_interactable_in_creative_mode");

	private static TagKey<Block> of(String path) {
		return TagKey.of(RegistryKeys.BLOCK, VersionUtils.getIdentifier(DynamicCrosshair.MOD_ID, path));
	}
}
