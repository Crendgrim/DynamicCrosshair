package mod.crend.dynamiccrosshairapi.registry;

import mod.crend.dynamiccrosshairapi.DynamicCrosshair;
import mod.crend.dynamiccrosshairapi.VersionUtils;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class DynamicCrosshairItemTags {
	public static final TagKey<Item> TOOLS = of("tools");
	public static final TagKey<Item> THROWABLES = of("throwables");
	public static final TagKey<Item> SHIELDS = of("shields");
	public static final TagKey<Item> MELEE_WEAPONS = of("melee_weapons");
	public static final TagKey<Item> RANGED_WEAPONS = of("ranged_weapons");
	public static final TagKey<Item> BLOCKS = of("blocks");
	public static final TagKey<Item> USABLE = of("usable");
	public static final TagKey<Item> ALWAYS_USABLE = of("always_usable");
	public static final TagKey<Item> ALWAYS_USABLE_ON_BLOCK = of("always_usable_on_block");
	public static final TagKey<Item> ALWAYS_USABLE_ON_ENTITY = of("always_usable_on_entity");
	public static final TagKey<Item> ALWAYS_USABLE_ON_MISS = of("always_usable_on_miss");

	private static TagKey<Item> of(String path) {
		return TagKey.of(RegistryKeys.ITEM, VersionUtils.getIdentifier(DynamicCrosshair.MOD_ID, path));
	}
}
