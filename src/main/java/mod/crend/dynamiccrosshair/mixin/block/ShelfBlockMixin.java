//? if >1.21.8 {
/*package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.mixin.DynamicCrosshairBaseBlock;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.InteractibleSlotContainer;
import net.minecraft.block.ShelfBlock;
import net.minecraft.block.entity.ShelfBlockEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.OptionalInt;

@Mixin(ShelfBlock.class)
public abstract class ShelfBlockMixin extends DynamicCrosshairBaseBlock implements InteractibleSlotContainer, DynamicCrosshairBlock {
	@Shadow @Final public static EnumProperty<Direction> FACING;

	@Shadow @Final public static BooleanProperty POWERED;

	public ShelfBlockMixin(Settings settings) {
		super(settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isMainHand()) {
			OptionalInt optionalInt = getHitSlot(context.getBlockHitResult(), context.getBlockState().get(FACING));
			if (optionalInt.isPresent()) {
				if (context.getItemStack().isEmpty() && context.getBlockEntity() instanceof ShelfBlockEntity shelf) {
					if (context.getBlockState().get(POWERED)) {
						return InteractionType.TAKE_ITEM_FROM_BLOCK;
					} else {
						if (!shelf.getStack(optionalInt.getAsInt()).isEmpty()) {
							return InteractionType.TAKE_ITEM_FROM_BLOCK;
						}
					}
				}
				else return InteractionType.PLACE_ITEM_ON_BLOCK;
			}
		}
		return InteractionType.EMPTY;
	}
}
*///?}
