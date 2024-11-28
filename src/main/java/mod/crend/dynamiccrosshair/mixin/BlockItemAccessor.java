package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockItem.class)
public interface BlockItemAccessor {
	@Invoker
	boolean invokeCanPlace(ItemPlacementContext context, BlockState state);

	@Invoker
	@Nullable BlockState invokeGetPlacementState(ItemPlacementContext context);
}
