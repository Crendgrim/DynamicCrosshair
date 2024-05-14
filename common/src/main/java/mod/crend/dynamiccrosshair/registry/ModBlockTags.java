package mod.crend.dynamiccrosshair.registry;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModBlockTags {
	public static final TagKey<Block> IS_INTERACTABLE = of("is_interactable");
	public static final TagKey<Block> IS_ALWAYS_INTERACTABLE = of("is_always_interactable");
	public static final TagKey<Block> IS_ALWAYS_INTERACTABLE_IN_CREATIVE_MODE = of("is_always_interactable_in_creative_mode");

	private static TagKey<Block> of(String path) {
		return TagKey.of(RegistryKeys.BLOCK, new Identifier(DynamicCrosshair.MOD_ID, path));
	}
}
