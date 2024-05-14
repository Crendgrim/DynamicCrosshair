package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.CaveVines;
import net.minecraft.block.CaveVinesBodyBlock;
import net.minecraft.block.CaveVinesHeadBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({CaveVinesBodyBlock.class, CaveVinesHeadBlock.class})
public abstract class CaveVinesMixin implements DynamicCrosshairBlock {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (CaveVines.hasBerries(context.getBlockState())) {
			return InteractionType.TAKE_ITEM_FROM_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
