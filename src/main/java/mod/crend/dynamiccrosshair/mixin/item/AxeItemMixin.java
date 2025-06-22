package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.config.UsableCrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.DynamicCrosshairBaseItem;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Map;
import java.util.Optional;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin extends DynamicCrosshairBaseItem implements DynamicCrosshairItem {
	@Shadow @Final protected static Map<Block, Block> STRIPPED_BLOCKS;

	//? if neoforge
	/*@Shadow protected abstract Optional<BlockState> evaluateNewBlockState(World arg, BlockPos arg2, @Nullable PlayerEntity arg3, BlockState arg4, ItemUsageContext arg5);*/

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.canUseWeaponAsTool()) {
			if (context.isWithBlock() && DynamicCrosshairMod.config.dynamicCrosshairHoldingUsableItem() != UsableCrosshairPolicy.Disabled) {
				//? if neoforge {
				/*if (evaluateNewBlockState(context.getWorld(), context.getBlockPos(), context.getPlayer(), context.getBlockState(), context.getItemUsageContext()).isPresent()) {
					return InteractionType.USABLE_TOOL;
				}
				*///?} else {
				Block block = context.getBlock();
				if (STRIPPED_BLOCKS.get(block) != null
						|| Oxidizable.getDecreasedOxidationBlock(block).isPresent()
						|| HoneycombItem.getWaxedState(context.getBlockState()).isPresent()
						|| HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(block) != null
				) {
					return InteractionType.USABLE_TOOL;
				}
				//?}
			}
			return super.dynamiccrosshair$compute(context);
		}
		return InteractionType.MELEE_WEAPON;
	}
}
