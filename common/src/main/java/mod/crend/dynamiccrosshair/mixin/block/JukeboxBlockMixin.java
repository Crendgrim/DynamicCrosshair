package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.BooleanProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(JukeboxBlock.class)
public class JukeboxBlockMixin implements DynamicCrosshairBlock {
	@Shadow @Final public static BooleanProperty HAS_RECORD;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.getBlockState().get(HAS_RECORD)) {
			return InteractionType.TAKE_ITEM_FROM_BLOCK;
		} else if (context.getItemStack().isIn(ItemTags.MUSIC_DISCS)) {
			return InteractionType.PLACE_ITEM_ON_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
