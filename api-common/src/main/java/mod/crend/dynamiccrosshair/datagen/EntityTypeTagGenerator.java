package mod.crend.dynamiccrosshair.datagen;

import mod.crend.dynamiccrosshair.registry.DynamicCrosshairEntityTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

class EntityTypeTagGenerator extends FabricTagProvider.EntityTypeTagProvider {
	public EntityTypeTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup arg) {
		getOrCreateTagBuilder(DynamicCrosshairEntityTags.ALWAYS_INTERACTABLE)
				.add(EntityType.LEASH_KNOT)
				.add(EntityType.CHEST_MINECART)
				.add(EntityType.HOPPER_MINECART)
		;

		getOrCreateTagBuilder(DynamicCrosshairEntityTags.INTERACTABLE)
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
