package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.Leashable;
import net.minecraft.item.LeadItem;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(FenceBlock.class)
public abstract class FenceBlockMixin extends BlockMixin implements DynamicCrosshairBlock {
	public FenceBlockMixin(Settings settings) {
		super(settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		BlockPos pos = context.getBlockPos();
		List<Leashable> list = LeadItem.collectLeashablesAround(context.getWorld(), pos, entity -> entity.getLeashHolder() == context.getPlayer());

		if (!list.isEmpty()) {
			// Leash all leaded mobs to fence
			return InteractionType.USE_ITEM_ON_BLOCK;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
