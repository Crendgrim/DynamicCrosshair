package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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
		List<MobEntity> list = context.world.getNonSpectatingEntities(MobEntity.class,
				new Box((double) pos.getX() - 7.0, (double) pos.getY() - 7.0, (double) pos.getZ() - 7.0,
						(double) pos.getX() + 7.0, (double) pos.getY() + 7.0, (double) pos.getZ() + 7.0));

		for (MobEntity mob : list) {
			if (mob.getHoldingEntity() == context.player) {
				// Leash all leaded mobs to fence
				return InteractionType.USE_ITEM_ON_BLOCK;
			}
		}
		return super.dynamiccrosshair$compute(context);
	}
}
