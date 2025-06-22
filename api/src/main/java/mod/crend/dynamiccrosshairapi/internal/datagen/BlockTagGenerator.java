//? if fabric {
package mod.crend.dynamiccrosshairapi.internal.datagen;

import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
//? if <1.20.6 {
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
//?} else {
/*import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
*///?}
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

//? if >1.21.5 {
/*import net.minecraft.data.tag.ProvidedTagBuilder;
*///?}

import java.util.concurrent.CompletableFuture;

class BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
	public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(output, completableFuture);
	}

	//? if <=1.21.5 {
	private FabricTagProvider<Block>.FabricTagBuilder makeTagBuilder(TagKey<Block> tagKey) {
		return getOrCreateTagBuilder(tagKey);
	}
	//?} else {
	/*private ProvidedTagBuilder<Block, Block> makeTagBuilder(TagKey<Block> tagKey) {
		return valueLookupBuilder(tagKey);
	}
	*///?}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup arg) {
		makeTagBuilder(DynamicCrosshairBlockTags.ALWAYS_INTERACTABLE)
				.addOptionalTag(ConventionalBlockTags.CHESTS)
				//? if >=1.20.6 {
				/*.addOptionalTag(ConventionalBlockTags.PLAYER_WORKSTATIONS_FURNACES)
				.addOptionalTag(ConventionalBlockTags.PLAYER_WORKSTATIONS_CRAFTING_TABLES)
				.addOptionalTag(ConventionalBlockTags.BARRELS)
				 *///?} else {
				.add(Blocks.BARREL)
				.add(Blocks.FURNACE)
				//?}
				.add(Blocks.BEACON)
				.add(Blocks.BELL)
				.add(Blocks.BREWING_STAND)
				.add(Blocks.DAYLIGHT_DETECTOR)
				.add(Blocks.DISPENSER)
				.add(Blocks.DROPPER)
				.add(Blocks.ENCHANTING_TABLE)
				.add(Blocks.HOPPER)
				.addOptionalTag(BlockTags.SHULKER_BOXES)
				.add(Blocks.SMOKER)
				.add(Blocks.BLAST_FURNACE)
				.add(Blocks.STONECUTTER)
				.add(Blocks.GRINDSTONE)
				.add(Blocks.CARTOGRAPHY_TABLE)
				.add(Blocks.LOOM)
				//? if >=1.20.3
				/*.add(Blocks.CRAFTER)*/
				.addOptionalTag(BlockTags.BEDS)
				.addOptionalTag(BlockTags.WOODEN_TRAPDOORS)
				.addOptionalTag(BlockTags.WOODEN_DOORS)
				//? if >=1.20.3 {
				/*.add(
						Blocks.COPPER_TRAPDOOR,
						Blocks.EXPOSED_COPPER_TRAPDOOR,
						Blocks.WEATHERED_COPPER_TRAPDOOR,
						Blocks.OXIDIZED_COPPER_TRAPDOOR,
						Blocks.WAXED_COPPER_TRAPDOOR,
						Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR,
						Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR,
						Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR
				)
				.add(
						Blocks.COPPER_DOOR,
						Blocks.EXPOSED_COPPER_DOOR,
						Blocks.WEATHERED_COPPER_DOOR,
						Blocks.OXIDIZED_COPPER_DOOR,
						Blocks.WAXED_COPPER_DOOR,
						Blocks.WAXED_EXPOSED_COPPER_DOOR,
						Blocks.WAXED_WEATHERED_COPPER_DOOR,
						Blocks.WAXED_OXIDIZED_COPPER_DOOR
				)
				*///?}
				.addOptionalTag(BlockTags.FENCE_GATES)
				.addOptionalTag(BlockTags.BUTTONS)
				.add(Blocks.NOTE_BLOCK)
				.add(Blocks.LEVER)
				.add(Blocks.COMPARATOR)
				.add(Blocks.REPEATER)
				.addOptionalTag(BlockTags.ANVIL)
				.add(Blocks.SMITHING_TABLE)
		;

		makeTagBuilder(DynamicCrosshairBlockTags.ALWAYS_INTERACTABLE_IN_CREATIVE_MODE)
				.add(Blocks.COMMAND_BLOCK)
				.add(Blocks.CHAIN_COMMAND_BLOCK)
				.add(Blocks.REPEATING_COMMAND_BLOCK)
				.add(Blocks.STRUCTURE_BLOCK)
				.add(Blocks.STRUCTURE_VOID)
		;

		makeTagBuilder(DynamicCrosshairBlockTags.INTERACTABLE)
				.addTag(DynamicCrosshairBlockTags.ALWAYS_INTERACTABLE)
				.addTag(DynamicCrosshairBlockTags.ALWAYS_INTERACTABLE_IN_CREATIVE_MODE)
				.add(Blocks.JUKEBOX)
				.add(Blocks.LECTERN)
				.add(Blocks.COMPOSTER)
				.add(Blocks.FLOWER_POT)
				.add(Blocks.CAKE)
				.add(Blocks.SWEET_BERRY_BUSH)
				.addOptionalTag(BlockTags.CANDLES)
				.addOptionalTag(BlockTags.CAMPFIRES)
				.add(Blocks.CHISELED_BOOKSHELF)
				.add(Blocks.DECORATED_POT)
				//? if >=1.20.5
				/*.add(Blocks.VAULT)*/
		;
	}
}
//?}