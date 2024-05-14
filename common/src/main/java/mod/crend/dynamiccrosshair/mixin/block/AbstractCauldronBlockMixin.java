package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.PlatformUtils;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
import mod.crend.dynamiccrosshair.component.Crosshair;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.item.PowderSnowBucketItem;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.ItemTags;
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
				PotionContentsComponent potionContentsComponent = handItemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
				if (potionContentsComponent.matches(Potions.WATER)) {
					return InteractionType.FILL_BLOCK_FROM_ITEM;
				}
			}
			if (handItem instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock sbb && sbb.getColor() != null) {
				return InteractionType.USE_ITEM_ON_BLOCK;
			}
			if (handItemStack.isIn(ItemTags.DYEABLE) && !handItemStack.contains(DataComponentTypes.DYED_COLOR)) {
				return InteractionType.USE_ITEM_ON_BLOCK;
			}
			if (handItem instanceof BannerItem) {
				BannerPatternsComponent bannerPatternsComponent = handItemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
				if (!bannerPatternsComponent.layers().isEmpty()) {
					return InteractionType.USE_ITEM_ON_BLOCK;
				}
			}
		}
		return InteractionType.NO_ACTION;
	}
}
