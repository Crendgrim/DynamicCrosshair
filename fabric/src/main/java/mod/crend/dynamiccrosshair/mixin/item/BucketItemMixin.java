package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.Bucketable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BucketItem.class)
public class BucketItemMixin extends Item implements DynamicCrosshairItem {
	@Shadow @Final private Fluid fluid;

	public BucketItemMixin(Settings settings) {
		super(settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		// Liquid interactions ignore block hit, cast extra rays
		// This getting called for entity hits is on purpose, as liquid interactions overwrite entity interactions
		BlockHitResult blockHitResult = context.raycastWithFluid(RaycastContext.FluidHandling.SOURCE_ONLY);
		switch (blockHitResult.getType()) {
			case BLOCK -> {
				Block block = context.getWorld().getBlockState(blockHitResult.getBlockPos()).getBlock();
				if (this.fluid == Fluids.EMPTY) {
					FluidState bucketFluidState = context.getWorld().getFluidState(blockHitResult.getBlockPos());
					if (!bucketFluidState.isEmpty() && bucketFluidState.isStill()) {
						return InteractionType.FILL_ITEM_FROM_BLOCK;
					}
					if (block instanceof FluidDrainable) {
						if (!(block instanceof Waterloggable || block instanceof FluidBlock)) {
							return InteractionType.FILL_ITEM_FROM_BLOCK;
						}
					}
					if (block instanceof AbstractCauldronBlock cauldron) {
						if (cauldron.isFull(context.getBlockState())) {
							return InteractionType.FILL_ITEM_FROM_BLOCK;
						}
					}
				} else {
					if (this.fluid == Fluids.WATER) {
						if (block instanceof FluidFillable) {
							return InteractionType.FILL_BLOCK_FROM_ITEM;
						}
					}
					if (fluid == Fluids.WATER || fluid == Fluids.LAVA) {
						if (block instanceof AbstractCauldronBlock) {
							return InteractionType.FILL_BLOCK_FROM_ITEM;
						}
					}
					return InteractionType.PLACE_BLOCK;
				}
			}
			case ENTITY -> {
				if (this.fluid == Fluids.WATER && context.getEntity() instanceof Bucketable) {
					return InteractionType.PICK_UP_ENTITY;
				}
			}
			case MISS -> {
				if (this.fluid != Fluids.EMPTY) {
					return InteractionType.PLACE_BLOCK;
				}
			}
		}
		return InteractionType.NO_ACTION;
	}
}
