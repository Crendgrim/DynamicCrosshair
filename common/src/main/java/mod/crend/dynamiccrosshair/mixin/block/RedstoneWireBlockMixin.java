package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin extends Block implements DynamicCrosshairBlock {
	public RedstoneWireBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow
	private static boolean isNotConnected(BlockState state) {
		return false;
	}

	@Shadow
	private static boolean isFullyConnected(BlockState state) {
		return false;
	}

	@Shadow @Final private BlockState dotState;

	@Shadow @Final public static IntProperty POWER;

	@Shadow protected abstract BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos);

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		BlockState state = context.getBlockState();
		if (isNotConnected(state)) {
			return InteractionType.INTERACT_WITH_BLOCK;
		}
		if (isFullyConnected(state)) {
			BlockState blockState = isFullyConnected(state) ? getDefaultState() : this.dotState;
			blockState = blockState.with(POWER, state.get(POWER));
			blockState = this.getPlacementState(context.getWorld(), blockState, context.getBlockPos());
			if (blockState != state) {
				return InteractionType.INTERACT_WITH_BLOCK;
			}
		}
		return InteractionType.NO_ACTION;
	}
}
