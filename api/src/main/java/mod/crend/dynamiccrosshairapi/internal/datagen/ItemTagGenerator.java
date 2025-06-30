//? if fabric {
package mod.crend.dynamiccrosshairapi.internal.datagen;

import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairItemTags;
import mod.crend.libbamboo.versioned.VersionedTagProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//? if <1.20.6 {
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
//?} else {
/*import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
 *///?}
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

class ItemTagGenerator extends VersionedTagProvider.ItemTagProvider {
	public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup arg) {
		versionedTagBuilder(DynamicCrosshairItemTags.TOOLS)
				//? if <1.20.6 {
				.addOptionalTag(ConventionalItemTags.SHEARS)
				//?} else if <1.21 {
				/*.addOptionalTag(ConventionalItemTags.MINING_TOOLS)
				.addOptionalTag(ConventionalItemTags.SHEARS_TOOLS)
				*///?} else {
				/*.addOptionalTag(ConventionalItemTags.MINING_TOOL_TOOLS)
				.addOptionalTag(ConventionalItemTags.SHEAR_TOOLS)
				*///?}
				.addOptionalTag(ItemTags.PICKAXES)
				.addOptionalTag(ItemTags.SHOVELS)
				.addOptionalTag(ItemTags.AXES)
				.addOptionalTag(ItemTags.HOES)
				.add(Items.FLINT_AND_STEEL)
		;

		versionedTagBuilder(DynamicCrosshairItemTags.THROWABLES)
				//? if >=1.21
				/*.addOptionalTag(ConventionalItemTags.ENDER_PEARLS)*/
				.add(Items.EGG)
				.add(Items.SNOWBALL)
				.add(Items.SPLASH_POTION)
				.add(Items.LINGERING_POTION)
				.add(Items.EXPERIENCE_BOTTLE)
				.add(Items.ENDER_PEARL)
				//? if >=1.21
				/*.add(Items.WIND_CHARGE)*/
		;

		versionedTagBuilder(DynamicCrosshairItemTags.SHIELDS)
				.add(Items.SHIELD)
				//? if <1.20.6 {
				.addOptionalTag(ConventionalItemTags.SHIELDS)
				//?} else if <1.21 {
				/*.addOptionalTag(ConventionalItemTags.SHIELDS_TOOLS)
				*///?} else {
				/*.addOptionalTag(ConventionalItemTags.SHIELD_TOOLS)
				*///?}
		;

		versionedTagBuilder(DynamicCrosshairItemTags.MELEE_WEAPONS)
				.addOptionalTag(ItemTags.SWORDS)
				//? if >=1.21 {
				/*.addOptionalTag(ConventionalItemTags.MELEE_WEAPON_TOOLS)
				*///?} else if >=1.20.6 {
				/*.addOptionalTag(ConventionalItemTags.MELEE_WEAPONS_TOOLS)
				*///?}
				//? if >=1.20.5
				/*.add(Items.MACE)*/
				.add(Items.TRIDENT)
		;

		versionedTagBuilder(DynamicCrosshairItemTags.RANGED_WEAPONS)
				.add(Items.FISHING_ROD)
				.add(Items.BOW)
				.add(Items.CROSSBOW)
				.add(Items.TRIDENT)
				//? if <1.20.6 {
				.addOptionalTag(ConventionalItemTags.SPEARS)
				.addOptionalTag(ConventionalItemTags.BOWS)
				//?} else if <1.21 {
				/*.addOptionalTag(ConventionalItemTags.SPEARS_TOOLS)
				.addOptionalTag(ConventionalItemTags.BOWS_TOOLS)
				.addOptionalTag(ConventionalItemTags.CROSSBOWS_TOOLS)
				.addOptionalTag(ConventionalItemTags.FISHING_RODS_TOOLS)
				.addOptionalTag(ConventionalItemTags.RANGED_WEAPONS_TOOLS)
				*///?} else {
				/*.addOptionalTag(ConventionalItemTags.SPEAR_TOOLS)
				.addOptionalTag(ConventionalItemTags.BOW_TOOLS)
				.addOptionalTag(ConventionalItemTags.CROSSBOW_TOOLS)
				.addOptionalTag(ConventionalItemTags.FISHING_ROD_TOOLS)
				.addOptionalTag(ConventionalItemTags.RANGED_WEAPON_TOOLS)
				 *///?}
		;

		// NOTE BlockItem is missing here
		versionedTagBuilder(DynamicCrosshairItemTags.BLOCKS)
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

		versionedTagBuilder(DynamicCrosshairItemTags.ALWAYS_USABLE)
				.add(Items.POTION)
				.addOptionalTag(ConventionalItemTags.POTIONS)
				.add(Items.HONEY_BOTTLE)
				.add(Items.GOLDEN_APPLE)
				.add(Items.ENCHANTED_GOLDEN_APPLE)
				.add(Items.MILK_BUCKET)
				.addOptionalTag(ConventionalItemTags.MILK_BUCKETS)
				//? if >=1.21
				/*.add(Items.OMINOUS_BOTTLE)*/
				.add(Items.GOAT_HORN)
				.add(Items.WRITABLE_BOOK)
				.add(Items.WRITTEN_BOOK)
		;

		versionedTagBuilder(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_BLOCK)
				.add(Items.BRUSH)
		;

		versionedTagBuilder(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_ENTITY)
		;

		versionedTagBuilder(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_MISS)
		;

		versionedTagBuilder(DynamicCrosshairItemTags.USABLE)
				.addOptionalTag(ConventionalItemTags.FOODS)
				//? if >=1.20.6
				/*.addOptionalTag(ConventionalItemTags.ARMORS)*/
				.add(Items.ELYTRA)
				.add(Items.FIREWORK_ROCKET)
				// TODO spawn eggs
				.add(Items.FIRE_CHARGE)
				//? if >=1.21
				/*.addOptionalTag(ConventionalItemTags.MUSIC_DISCS)*/
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
//?}
