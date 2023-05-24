package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(ChiseledBookshelfBlock.class)
public interface ChiseledBookshelfBlockAccessor {
	@Invoker
	static Optional<Vec2f> invokeGetHitPos(BlockHitResult hit, Direction facing) {
		throw new AssertionError();
	}

	@Invoker
	static int invokeGetSlotForHitPos(Vec2f hitPos) {
		throw new AssertionError();
	}
}
