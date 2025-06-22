package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
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
