//? if >1.21.8 {
/*package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.mixin.DynamicCrosshairBaseBlock;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.CopperGolemStatueBlock;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CopperGolemStatueBlock.class)
public abstract class CopperGolemStatueBlockMixin extends DynamicCrosshairBaseBlock implements DynamicCrosshairBlock {
	public CopperGolemStatueBlockMixin(Settings settings) {
		super(settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.getItemStack().isIn(ItemTags.AXES)) return InteractionType.USE_ITEM_ON_BLOCK;
		return InteractionType.INTERACT_WITH_BLOCK;
	}
}
*///?}