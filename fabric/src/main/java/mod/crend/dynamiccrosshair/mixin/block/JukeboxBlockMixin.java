package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.JukeboxBlock;

//? >=1.20.6
/*import net.minecraft.component.DataComponentTypes;*/
//? <1.21
import net.minecraft.item.MusicDiscItem;
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
		} else if (
				//? >=1.21 {
				/*context.getItemStack().contains(DataComponentTypes.JUKEBOX_PLAYABLE)
				*///?} else {
				context.getItem() instanceof MusicDiscItem
				//?}
		) {
			return InteractionType.PLACE_ITEM_ON_BLOCK;
		}
		return InteractionType.NO_ACTION;
	}
}
