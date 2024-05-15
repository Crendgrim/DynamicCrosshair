package mod.crend.dynamiccrosshair.registry;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class DynamicCrosshairEntityTags {
	public static final TagKey<EntityType<?>> INTERACTABLE = of("interactable");
	public static final TagKey<EntityType<?>> ALWAYS_INTERACTABLE = of("always_interactable");

	private static TagKey<EntityType<?>> of(String path) {
		return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(DynamicCrosshair.MOD_ID, path));
	}
}
