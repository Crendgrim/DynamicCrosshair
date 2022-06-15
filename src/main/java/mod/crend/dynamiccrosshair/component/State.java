package mod.crend.dynamiccrosshair.component;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class State {
	private static class HitState {
		ItemStack mainHandStack;
		ItemStack offHandStack;
		boolean cancelInteraction;

		public HitState(ClientPlayerEntity player) {
			mainHandStack = player.getMainHandStack();
			offHandStack = player.getOffHandStack();
			cancelInteraction = player.shouldCancelInteraction();
		}

		public boolean isChanged(HitState other) {
			return (mainHandStack != other.mainHandStack || offHandStack != other.offHandStack || cancelInteraction != other.cancelInteraction);
		}
	}

	private static class HitStateBlock extends HitState {
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
			if (super.isChanged(other)) return true;
			if (other instanceof HitStateBlock otherBlock) {
				return (x != otherBlock.x || y != otherBlock.y || z != otherBlock.z || side != otherBlock.side || blockState != otherBlock.blockState);
			}
			return true;
		}
	}

	private static class HitStateEntity extends HitState {
		Entity entity;

		public HitStateEntity(ClientPlayerEntity player, EntityHitResult entityHitResult) {
			super(player);
			entity = entityHitResult.getEntity();
		}

		@Override
		public boolean isChanged(HitState other) {
			if (super.isChanged(other)) return true;
			if (other instanceof HitStateEntity otherEntity) {
				return (entity != otherEntity.entity);
			}
			return true;
		}
	}

	private static class HitStateMiss extends HitState {
		public HitStateMiss(ClientPlayerEntity player) {
			super(player);
		}

		@Override
		public boolean isChanged(HitState other) {
			if (super.isChanged(other)) return true;
			return !(other instanceof HitStateMiss);
		}
	}

	HitState previousState;

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
			return true;
		}
		return false;
	}

}
