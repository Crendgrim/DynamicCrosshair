package mod.crend.dynamiccrosshairapi.internal.datagen;

import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairBlockTags;
import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairEntityTags;
import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class EnglishTagLangGenerator extends FabricLanguageProvider {
	protected EnglishTagLangGenerator(FabricDataOutput dataOutput/*? if >=1.20.6 {*//*, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup*//*?}*/) {
		super(dataOutput/*? if >=1.20.6 {*//*, registryLookup*//*?}*/);
	}

	@Override
	public void generateTranslations(/*? if >=1.20.6 {*//*RegistryWrapper.WrapperLookup registryLookup, *//*?}*/TranslationBuilder translationBuilder) {
		//? if >=1.20.6 {
		/*translationBuilder.add(DynamicCrosshairBlockTags.ALWAYS_INTERACTABLE, "Always interactable");
		translationBuilder.add(DynamicCrosshairBlockTags.ALWAYS_INTERACTABLE_IN_CREATIVE_MODE, "Always interactable in creative mode");
		translationBuilder.add(DynamicCrosshairBlockTags.INTERACTABLE, "Interactable");

		translationBuilder.add(DynamicCrosshairEntityTags.ALWAYS_INTERACTABLE, "Always interactable");
		translationBuilder.add(DynamicCrosshairEntityTags.INTERACTABLE, "Interactable");

		translationBuilder.add(DynamicCrosshairItemTags.TOOLS, "Tools");
		translationBuilder.add(DynamicCrosshairItemTags.THROWABLES, "Throwables");
		translationBuilder.add(DynamicCrosshairItemTags.SHIELDS, "Shields");
		translationBuilder.add(DynamicCrosshairItemTags.MELEE_WEAPONS, "Melee Weapons");
		translationBuilder.add(DynamicCrosshairItemTags.RANGED_WEAPONS, "Ranged Weapons");
		translationBuilder.add(DynamicCrosshairItemTags.BLOCKS, "Blocks");
		translationBuilder.add(DynamicCrosshairItemTags.USABLE, "Usable");
		translationBuilder.add(DynamicCrosshairItemTags.ALWAYS_USABLE, "Always usable");
		translationBuilder.add(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_BLOCK, "Always usable on block");
		translationBuilder.add(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_ENTITY, "Always usable on entity");
		translationBuilder.add(DynamicCrosshairItemTags.ALWAYS_USABLE_ON_MISS, "Always usable on miss");
		 *///?}
	}
}
