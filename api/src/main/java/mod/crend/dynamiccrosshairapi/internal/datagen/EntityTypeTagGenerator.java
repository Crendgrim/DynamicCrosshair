//? if fabric {
package mod.crend.dynamiccrosshairapi.internal.datagen;

import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairEntityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
//? if <1.20.6 {
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEntityTypeTags;
//?} else {
/*import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
 *///?}
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

//? if >1.21.5 {
/*import net.minecraft.data.tag.ProvidedTagBuilder;
*///?}

import java.util.concurrent.CompletableFuture;

class EntityTypeTagGenerator extends FabricTagProvider.EntityTypeTagProvider {
	public EntityTypeTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(output, completableFuture);
	}

	//? if <1.21.6 {
	private FabricTagProvider<EntityType<?>>.FabricTagBuilder makeTagBuilder(TagKey<EntityType<?>> tagKey) {
		return getOrCreateTagBuilder(tagKey);
	}
	//?} else {
	/*private ProvidedTagBuilder<EntityType<?>, EntityType<?>> makeTagBuilder(TagKey<EntityType<?>> tagKey) {
		return valueLookupBuilder(tagKey);
	}
	*///?}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup arg) {
		makeTagBuilder(DynamicCrosshairEntityTags.ALWAYS_INTERACTABLE)
				.add(EntityType.LEASH_KNOT)
				.add(EntityType.CHEST_MINECART)
				.add(EntityType.HOPPER_MINECART)
		;

		makeTagBuilder(DynamicCrosshairEntityTags.INTERACTABLE)
				.addTag(DynamicCrosshairEntityTags.ALWAYS_INTERACTABLE)
				.add(EntityType.ITEM_FRAME)
				.addOptionalTag(ConventionalEntityTypeTags.BOATS)
				.addOptionalTag(ConventionalEntityTypeTags.MINECARTS)
				.add(EntityType.CAT)
				.add(EntityType.WOLF)
				.add(EntityType.PARROT)
				.add(EntityType.HORSE)
				.add(EntityType.DONKEY)
				.add(EntityType.MULE)
				.add(EntityType.SKELETON_HORSE)
				.add(EntityType.ZOMBIE_HORSE)
				.add(EntityType.VILLAGER)
				.add(EntityType.WANDERING_TRADER)
				.add(EntityType.ALLAY)
		;
	}
}
//?}
