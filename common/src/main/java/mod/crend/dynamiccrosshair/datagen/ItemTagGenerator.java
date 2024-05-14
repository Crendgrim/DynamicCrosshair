package mod.crend.dynamiccrosshair.datagen;

import mod.crend.dynamiccrosshair.registry.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
	public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup arg) {
		getOrCreateTagBuilder(ModItemTags.TOOLS)
				.addOptionalTag(ConventionalItemTags.MINING_TOOLS)
				.addOptionalTag(ConventionalItemTags.SHEARS_TOOLS)
				.addOptionalTag(ItemTags.PICKAXES)
				.addOptionalTag(ItemTags.SHOVELS)
				.addOptionalTag(ItemTags.AXES)
				.addOptionalTag(ItemTags.HOES)
				.add(Items.FLINT_AND_STEEL)
		;

		getOrCreateTagBuilder(ModItemTags.THROWABLES)
				.add(Items.EGG)
				.add(Items.SNOWBALL)
				.add(Items.SPLASH_POTION)
				.add(Items.LINGERING_POTION)
				.add(Items.EXPERIENCE_BOTTLE)
				.add(Items.ENDER_PEARL)
		;

		getOrCreateTagBuilder(ModItemTags.SHIELDS)
				.add(Items.SHIELD)
				.addOptionalTag(ConventionalItemTags.SHIELDS_TOOLS)
		;

		getOrCreateTagBuilder(ModItemTags.MELEE_WEAPONS)
				.addOptionalTag(ItemTags.SWORDS)
				.addOptionalTag(ConventionalItemTags.MELEE_WEAPONS_TOOLS)
				.add(Items.MACE)
		;

		getOrCreateTagBuilder(ModItemTags.RANGED_WEAPONS)
				.add(Items.FISHING_ROD)
				.add(Items.BOW)
				.add(Items.CROSSBOW)
				.add(Items.TRIDENT)
				.addOptionalTag(ConventionalItemTags.SPEARS_TOOLS)
				.addOptionalTag(ConventionalItemTags.BOWS_TOOLS)
				.addOptionalTag(ConventionalItemTags.CROSSBOWS_TOOLS)
				.addOptionalTag(ConventionalItemTags.FISHING_RODS_TOOLS)
				.addOptionalTag(ConventionalItemTags.RANGED_WEAPONS_TOOLS)
		;

		// NOTE BlockItem is missing here
		getOrCreateTagBuilder(ModItemTags.BLOCKS)
				.add(Items.SWEET_BERRIES)
				.add(Items.GLOW_BERRIES)
				.add(Items.ARMOR_STAND)
				.add(Items.MINECART)
				.addOptionalTag(ItemTags.BOATS)
				.add(Items.END_CRYSTAL)
				.addOptionalTag(ConventionalItemTags.WATER_BUCKETS)
				.addOptionalTag(ConventionalItemTags.ENTITY_WATER_BUCKETS)
				.addOptionalTag(ConventionalItemTags.LAVA_BUCKETS)
		;

		getOrCreateTagBuilder(ModItemTags.ALWAYS_USABLE)
				.add(Items.POTION)
				.addOptionalTag(ConventionalItemTags.POTIONS)
				.add(Items.HONEY_BOTTLE)
				.add(Items.GOLDEN_APPLE)
				.add(Items.ENCHANTED_GOLDEN_APPLE)
				.add(Items.MILK_BUCKET)
				.addOptionalTag(ConventionalItemTags.MILK_BUCKETS)
				.add(Items.OMINOUS_BOTTLE)
				.add(Items.GOAT_HORN)
				.add(Items.WRITABLE_BOOK)
				.add(Items.WRITTEN_BOOK)
		;

		getOrCreateTagBuilder(ModItemTags.ALWAYS_USABLE_ON_BLOCK)
				.add(Items.BRUSH)
		;

		getOrCreateTagBuilder(ModItemTags.ALWAYS_USABLE_ON_ENTITY)
		;

		getOrCreateTagBuilder(ModItemTags.ALWAYS_USABLE_ON_MISS)
		;

		getOrCreateTagBuilder(ModItemTags.USABLE)
				.addOptionalTag(ConventionalItemTags.FOODS)
				.addOptionalTag(ConventionalItemTags.ARMORS)
				.add(Items.ELYTRA)
				.add(Items.FIREWORK_ROCKET)
				// TODO spawn eggs
				.add(Items.FIRE_CHARGE)
				.addOptionalTag(ItemTags.MUSIC_DISCS)
				.add(Items.HONEYCOMB)
				.add(Items.ENDER_EYE)
				.add(Items.GLASS_BOTTLE)
				.add(Items.BUCKET)
				.addOptionalTag(ConventionalItemTags.EMPTY_BUCKETS)
				.add(Items.BONE_MEAL)
				.add(Items.BUNDLE)
		;
	}
}
