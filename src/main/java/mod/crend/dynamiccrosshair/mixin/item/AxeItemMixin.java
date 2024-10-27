package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.config.UsableCrosshairPolicy;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoneycombItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(AxeItem.class)
public class AxeItemMixin extends ItemMixin implements DynamicCrosshairItem {
	@Shadow @Final protected static Map<Block, Block> STRIPPED_BLOCKS;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.canUseWeaponAsTool()) {
			if (context.isWithBlock() && DynamicCrosshairMod.config.dynamicCrosshairHoldingUsableItem() != UsableCrosshairPolicy.Disabled) {
				Block block = context.getBlock();
				if (STRIPPED_BLOCKS.get(block) != null
						|| Oxidizable.getDecreasedOxidationBlock(block).isPresent()
						|| HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(block) != null) {
					return InteractionType.USABLE_TOOL;
				}
			}
			return super.dynamiccrosshair$compute(context);
		}
		return InteractionType.MELEE_WEAPON;
	}
}
