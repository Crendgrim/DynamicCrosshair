package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
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
		ItemStack mainHandStack;
		ItemStack offHandStack;
		boolean cancelInteraction;
		boolean isCoolingDown;

		public HitState(ClientPlayerEntity player) {
			mainHandStack = player.getMainHandStack();
			offHandStack = player.getOffHandStack();
			cancelInteraction = player.shouldCancelInteraction();
			isCoolingDown = (player.getItemCooldownManager().isCoolingDown(mainHandStack.getItem()) || player.getItemCooldownManager().isCoolingDown(offHandStack.getItem()));
		}

		public boolean isChanged(HitState other) {
			boolean invalidated = false;
			if (mainHandStack != other.mainHandStack) {
				mainHandContext.invalidateItem();
				invalidated = true;
			}
			if (offHandStack != other.offHandStack) {
				offHandContext.invalidateItem();
				invalidated = true;
			}
			return (invalidated || cancelInteraction != other.cancelInteraction || isCoolingDown != other.isCoolingDown);
		}
	}

	private class HitStateBlock extends HitState {
		int x;
		int y;
		int z;
		Direction side;
		BlockState blockState;

		public HitStateBlock(ClientPlayerEntity player, BlockHitResult blockHitResult) {
			super(player);
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

			mainHandContext.invalidateHitResult();
			offHandContext.invalidateHitResult();
			return true;
		}
	}

	private class HitStateEntity extends HitState {
		Entity entity;

		public HitStateEntity(ClientPlayerEntity player, EntityHitResult entityHitResult) {
			super(player);
			entity = entityHitResult.getEntity();
		}

		@Override
		public boolean isChanged(HitState other) {
			if (!super.isChanged(other) && other instanceof HitStateEntity otherEntity && entity == otherEntity.entity) {
				return false;
			}

			mainHandContext.invalidateHitResult();
			offHandContext.invalidateHitResult();
			return true;
		}
	}

	private class HitStateMiss extends HitState {
		public HitStateMiss(ClientPlayerEntity player) {
			super(player);
		}

		@Override
		public boolean isChanged(HitState other) {
			if (super.isChanged(other)) return true;
			if (other instanceof HitStateMiss) {
				return false;
			}
			mainHandContext.invalidateHitResult();
			offHandContext.invalidateHitResult();
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
	public final CrosshairContext mainHandContext;
	public final CrosshairContext offHandContext;

	public State() {
		mainHandContext = new CrosshairContext(Hand.MAIN_HAND);
		offHandContext = new CrosshairContext(Hand.OFF_HAND);
	}

	public boolean changed(HitResult hitResult, ClientPlayerEntity player) {
		HitState newState = switch(hitResult.getType()) {
			case BLOCK -> new HitStateBlock(player, (BlockHitResult) hitResult);
			case ENTITY -> new HitStateEntity(player, (EntityHitResult) hitResult);
			case MISS -> new HitStateMiss(player);
		};

		if (previousState == null) {
			previousState = newState;
			return true;
		}

		if (newState.isChanged(previousState)) {
			previousState = newState;
			if (previousFluidState != null) {
				previousFluidState = null;
				mainHandContext.invalidateHitResult();
				offHandContext.invalidateHitResult();
			}
			return true;
		}

		if (!player.getActiveItem().isEmpty()) {
			return true;
		}

		BlockHitResult fluidHitResult = mainHandContext.raycastWithFluid();
		if (fluidHitResult.getType() == HitResult.Type.BLOCK) {
			HitStateFluid newFluidState = new HitStateFluid(fluidHitResult);
			if (newFluidState.isChanged(previousFluidState)) {
				previousFluidState = newFluidState;
				mainHandContext.invalidateHitResult();
				offHandContext.invalidateHitResult();
				return true;
			}
		} else if (previousFluidState != null) {
			previousFluidState = null;
			mainHandContext.invalidateHitResult();
			offHandContext.invalidateHitResult();
			return true;
		}
		return false;
	}

}
