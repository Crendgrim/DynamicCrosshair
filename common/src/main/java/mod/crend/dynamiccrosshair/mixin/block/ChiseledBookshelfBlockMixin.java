package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.OptionalInt;

@Mixin(ChiseledBookshelfBlock.class)
public abstract class ChiseledBookshelfBlockMixin implements DynamicCrosshairBlock {
	@Shadow protected abstract OptionalInt getSlotForHitPos(BlockHitResult hit, BlockState state);

	@Shadow @Final public static List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		OptionalInt optionalInt = getSlotForHitPos(context.getBlockHitResult(), context.getBlockState());
		if (optionalInt.isPresent()) {
			if (context.getBlockState().get(SLOT_OCCUPIED_PROPERTIES.get(optionalInt.getAsInt()))) {
				return InteractionType.TAKE_ITEM_FROM_BLOCK;
			} else if (context.getItemStack().isIn(ItemTags.BOOKSHELF_BOOKS)) {
				return InteractionType.PLACE_ITEM_ON_BLOCK;
			}
		}
		return InteractionType.NO_ACTION;
	}
}
