package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;

import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.util.List;
import java.util.Optional;

//? if >=1.20.6 {
/*import java.util.OptionalInt;
import net.minecraft.block.BlockState;
*///?}
//? if >1.21.8 {
/*import net.minecraft.block.InteractibleSlotContainer;
import net.minecraft.state.property.EnumProperty;
*///?}

@Mixin(ChiseledBookshelfBlock.class)
public abstract class ChiseledBookshelfBlockMixin implements /*? if >1.21.8 {*//*InteractibleSlotContainer,*//*?}*/ DynamicCrosshairBlock {

	@Shadow @Final public static List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES;

	//? if <1.20.6 {
	@Shadow
	private static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing) {
		return Optional.empty();
	}
	@Shadow
	private static int getSlotForHitPos(Vec2f hitPos) {
		return 0;
	}
	//?} else if <=1.21.8 {
	//@Shadow protected abstract OptionalInt getSlotForHitPos(BlockHitResult hit, BlockState state);
	//?} else {
	/*@Shadow @Final public static EnumProperty<Direction> FACING;
	*///?}


	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		//? if <1.20.6 {
		Optional<Integer> result = getHitPos(context.getBlockHitResult(), context.getBlockHitSide()).map(ChiseledBookshelfBlockMixin::getSlotForHitPos);
		//?} else if <=1.21.8 {
		//OptionalInt result = getSlotForHitPos(context.getBlockHitResult(), context.getBlockState());
		//?} else {
		/*OptionalInt result = getHitSlot(context.getBlockHitResult(), context.getBlockState().get(FACING));
		*///?}
		if (result.isPresent()) {
			int slot = /*? if <1.20.6 {*/ result.get() /*?} else {*//*result.getAsInt()*//*?}*/;
			if (context.getBlockState().get(SLOT_OCCUPIED_PROPERTIES.get(slot))) {
				return InteractionType.TAKE_ITEM_FROM_BLOCK;
			} else if (context.getItemStack().isIn(ItemTags.BOOKSHELF_BOOKS)) {
				return InteractionType.PLACE_ITEM_ON_BLOCK;
			}
		}
		return InteractionType.NO_ACTION;
	}
}
