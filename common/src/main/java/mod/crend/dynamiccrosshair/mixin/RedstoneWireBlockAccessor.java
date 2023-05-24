package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RedstoneWireBlock.class)
public interface RedstoneWireBlockAccessor {
	@Invoker
	static boolean invokeIsFullyConnected(BlockState blockState) {
		throw new AssertionError();
	}

	@Invoker
	static boolean invokeIsNotConnected(BlockState blockState) {
		throw new AssertionError();
	}
}
