package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class State {
	private class HitState {
		HitResult hitResult;
		ItemStack mainHandStack;
		ItemStack offHandStack;
		ItemStack activeStack;
		Hand activeHand;
		boolean cancelInteraction;
		boolean isCoolingDown;
		boolean isOnGround;
		boolean isFallFlying;

		public HitState(ClientPlayerEntity player, HitResult hitResult) {
			this.hitResult = hitResult;
			mainHandStack = player.getMainHandStack().copy();
			offHandStack = player.getOffHandStack().copy();
			activeStack = player.getActiveItem().copy();
			activeHand = player.getActiveHand();
			cancelInteraction = player.shouldCancelInteraction();
			isCoolingDown = (player.getItemCooldownManager().isCoolingDown(mainHandStack.getItem()) || player.getItemCooldownManager().isCoolingDown(offHandStack.getItem()));
			isOnGround = player.isOnGround();
			isFallFlying = player.isFallFlying();
		}

		public boolean isChanged(HitState other) {
			boolean invalidated = false;
			if (!ItemStack.areEqual(mainHandStack, other.mainHandStack)) {
				context.invalidateItem(Hand.MAIN_HAND);
				invalidated = true;
			}
			if (!ItemStack.areEqual(offHandStack, other.offHandStack)) {
				context.invalidateItem(Hand.OFF_HAND);
				invalidated = true;
			}
			if (!ItemStack.areEqual(activeStack, other.activeStack)) {
				context.invalidateItem(activeHand);
				invalidated = true;
			}
			return (invalidated || cancelInteraction != other.cancelInteraction || isCoolingDown != other.isCoolingDown || isOnGround != other.isOnGround || isFallFlying != other.isFallFlying);
		}
	}

	private class HitStateBlock extends HitState {
		int x;
		int y;
		int z;
		Direction side;
		BlockState blockState;

		public HitStateBlock(ClientPlayerEntity player, BlockHitResult blockHitResult) {
			super(player, blockHitResult);
			BlockPos blockPos = blockHitResult.getBlockPos();
			x = blockPos.getX();
			y = blockPos.getY();
			z = blockPos.getZ();
			side = blockHitResult.getSide();
			blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
		}

		@Override
		public boolean isChanged(HitState other) {
			if (!super.isChanged(other) && other instanceof HitStateBlock otherBlock
					&& x == otherBlock.x
					&& y == otherBlock.y
					&& z == otherBlock.z
					&& side == otherBlock.side
					&& blockState == otherBlock.blockState
			) {
				return false;
			}

			context.invalidateHitResult(hitResult);
			return true;
		}
	}

	private class HitStateEntity extends HitState {
		Entity entity;

		public HitStateEntity(ClientPlayerEntity player, EntityHitResult entityHitResult) {
			super(player, entityHitResult);
			entity = entityHitResult.getEntity();
		}

		@Override
		public boolean isChanged(HitState other) {
			if (!super.isChanged(other) && other instanceof HitStateEntity otherEntity && entity == otherEntity.entity) {
				return false;
			}

			context.invalidateHitResult(hitResult);
			return true;
		}
	}

	private class HitStateMiss extends HitState {
		public HitStateMiss(ClientPlayerEntity player, HitResult hitResult) {
			super(player, hitResult);
		}

		@Override
		public boolean isChanged(HitState other) {
			if (!super.isChanged(other) && other instanceof HitStateMiss) {
				return false;
			}

			context.invalidateHitResult(hitResult);
			return true;
		}
	}

	private static class HitStateFluid {
		int level;
		Fluid fluid;

		public HitStateFluid(BlockHitResult fluidHitResult) {
			FluidState fluidState = MinecraftClient.getInstance().world.getFluidState(fluidHitResult.getBlockPos());
			fluid = fluidState.getFluid();
			level = fluid.getLevel(fluidState);
		}

		public boolean isChanged(HitStateFluid other) {
			return (other == null
					|| fluid != other.fluid
					|| level != other.level
			);
		}

	}

	HitState previousState;
	HitStateFluid previousFluidState = null;
	public final CrosshairContext context;

	public State() {
		context = new CrosshairContext();
	}

	public boolean changed(HitResult hitResult, ClientPlayerEntity player) {
		HitState newState = switch(hitResult.getType()) {
			case BLOCK -> new HitStateBlock(player, (BlockHitResult) hitResult);
			case ENTITY -> new HitStateEntity(player, (EntityHitResult) hitResult);
			case MISS -> new HitStateMiss(player, hitResult);
		};

		if (previousState == null) {
			previousState = newState;
			return true;
		}

		for (DynamicCrosshairApi api : context.apis()) {
			if (api.forceInvalidate(context)) {
				context.invalidateHitResult(hitResult);
				return true;
			}
		}

		if (newState.isChanged(previousState)) {
			previousState = newState;
			if (previousFluidState != null) {
				previousFluidState = null;
				context.invalidateHitResult(hitResult);
			}
			return true;
		}

		if (!player.getActiveItem().isEmpty()) {
			return true;
		}

		BlockHitResult fluidHitResult = context.raycastWithFluid();
		if (fluidHitResult.getType() == HitResult.Type.BLOCK) {
			HitStateFluid newFluidState = new HitStateFluid(fluidHitResult);
			if (newFluidState.isChanged(previousFluidState)) {
				previousFluidState = newFluidState;
				context.invalidateHitResult(hitResult);
				return true;
			}
		} else if (previousFluidState != null) {
			previousFluidState = null;
			context.invalidateHitResult(hitResult);
			return true;
		}
		return false;
	}

}
