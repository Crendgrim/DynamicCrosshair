package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockItem.class)
public interface BlockItemAccessor {
    @Invoker("canPlace")
    boolean invokeCanPlace(ItemPlacementContext itemPlacementContext, BlockState state);

    @Invoker("getPlacementState")
    BlockState invokeGetPlacementState(ItemPlacementContext itemPlacementContext);
}
