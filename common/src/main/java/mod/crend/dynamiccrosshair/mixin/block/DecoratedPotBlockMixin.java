package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DecoratedPotBlock.class)
public abstract class DecoratedPotBlockMixin extends BlockMixin implements DynamicCrosshairBlock {
	public DecoratedPotBlockMixin(Settings settings) {
		super(settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.getBlockEntity() instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
			ItemStack itemStack = context.getItemStack();
			ItemStack itemStack2 = decoratedPotBlockEntity.getStack();
			if (!itemStack.isEmpty() && (itemStack2.isEmpty() || (ItemStack.areItemsAndComponentsEqual(itemStack2, itemStack) && itemStack2.getCount() < itemStack2.getMaxCount()))) {
				return InteractionType.PLACE_ITEM_ON_BLOCK;
			} else {
				// Wobble wobble, still show a crosshair
				return InteractionType.INTERACT_WITH_BLOCK;
			}
		}

		return super.dynamiccrosshair$compute(context);
	}
}
