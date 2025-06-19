package mod.crend.dynamiccrosshair.neoforge.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshair.neoforge.mixin.DynamicCrosshairBaseBlock;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.block.FenceBlock;
//? if >1.20.6
import net.minecraft.entity.Leashable;
import net.minecraft.entity.mob.MobEntity;
//? if >1.20.6
import net.minecraft.item.LeadItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(FenceBlock.class)
public abstract class FenceBlockMixin extends DynamicCrosshairBaseBlock implements DynamicCrosshairBlock {
	public FenceBlockMixin(Settings settings) {
		super(settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		BlockPos pos = context.getBlockPos();
		//? if >1.21.5 {
		/*List<Leashable> list = Leashable.collectLeashablesAround(context.getWorld(), pos.toCenterPos(), entity -> entity.getLeashHolder() == context.getPlayer());
		 *///?} else if >1.20.6 {
		List<Leashable> list = LeadItem.collectLeashablesAround(context.getWorld(), pos, entity -> entity.getLeashHolder() == context.getPlayer());
		//?} else {
		/*List<MobEntity> list = context.getWorld().getNonSpectatingEntities(MobEntity.class, new Box(pos.getX() - 7.0, pos.getY() - 7.0, pos.getZ() - 7.0, pos.getX() + 7.0, pos.getY() + 7.0, pos.getZ() + 7.0));
		*///?}

		if (!list.isEmpty()) {
			// Leash all leaded mobs to fence
			return InteractionType.USE_ITEM_ON_BLOCK;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
