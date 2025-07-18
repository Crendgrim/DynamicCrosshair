package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;

//? if >=1.20.6 {
/*import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.registry.tag.ItemTags;

*///?}

//? if <1.20.6
import net.minecraft.item.DyeableItem;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.PowderSnowBucketItem;
//? if <1.20.6
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractCauldronBlock.class)
public abstract class AbstractCauldronBlockMixin extends Block implements DynamicCrosshairBlock {
	public AbstractCauldronBlockMixin(Settings settings) {
		super(settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		ItemStack handItemStack = context.getItemStack();
		Item handItem = handItemStack.getItem();
		if (handItem instanceof BucketItem) {
			// Deal with this in BucketItem because its fluid field is private
			return InteractionType.EMPTY;
		}
		if (handItem instanceof PowderSnowBucketItem) {
			return InteractionType.FILL_BLOCK_FROM_ITEM;
		}
		if (this.equals(Blocks.WATER_CAULDRON)) {
			if (handItem instanceof GlassBottleItem) {
				return InteractionType.FILL_ITEM_FROM_BLOCK;
			}
			if (handItem instanceof PotionItem) {
				//? if >=1.20.6 {
				/*PotionContentsComponent potionContentsComponent = handItemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
				if (potionContentsComponent.matches(Potions.WATER)) {
					return InteractionType.FILL_BLOCK_FROM_ITEM;
				}
				*///?} else {
				if (PotionUtil.getPotion(handItemStack) == Potions.WATER) {
					return InteractionType.FILL_BLOCK_FROM_ITEM;
				}
				//?}
			}
			if (handItem instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock sbb && sbb.getColor() != null) {
				return InteractionType.USE_ITEM_ON_BLOCK;
			}
			if (
					//? if >=1.20.6 {
					/*handItemStack.isIn(ItemTags.DYEABLE) && handItemStack./^? if neoforge && >=1.21.4 {^//^has^//^?} else {^/contains/^?}^/(DataComponentTypes.DYED_COLOR)
					*///?} else {
					handItem instanceof DyeableItem dyeableItem && dyeableItem.hasColor(handItemStack)
					//?}
			) {
				return InteractionType.USE_ITEM_ON_BLOCK;
			}
			if (handItem instanceof BannerItem) {
				//? if >=1.20.6 {
				
				/*BannerPatternsComponent bannerPatternsComponent = handItemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
				if (!bannerPatternsComponent.layers().isEmpty()) {
					return InteractionType.USE_ITEM_ON_BLOCK;
				}
				*///?} else {
				if (BannerBlockEntity.getPatternCount(handItemStack) > 0) {
					return InteractionType.USE_ITEM_ON_BLOCK;
				}
				//?}
			}
		}
		return InteractionType.NO_ACTION;
	}
}
