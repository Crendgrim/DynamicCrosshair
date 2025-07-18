package mod.crend.dynamiccrosshairapi.registry;

import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.VersionUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class DynamicCrosshairEntityTags {
	public static final TagKey<EntityType<?>> INTERACTABLE = of("interactable");
	public static final TagKey<EntityType<?>> ALWAYS_INTERACTABLE = of("always_interactable");

	private static TagKey<EntityType<?>> of(String path) {
		return TagKey.of(RegistryKeys.ENTITY_TYPE, VersionUtils.getIdentifier(DynamicCrosshair.MOD_ID, path));
	}
}
