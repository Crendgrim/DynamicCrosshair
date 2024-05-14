package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.BlockState;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SwordItem.class)
public class SwordItemMixin extends ToolItem implements DynamicCrosshairItem {
	public SwordItemMixin(ToolMaterial material, Settings settings) {
		super(material, settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.canUseWeaponAsTool() && !DynamicCrosshairMod.config.dynamicCrosshairMeleeWeaponOnBreakableBlock()) {
			BlockState blockState = context.getBlockState();
			if (context.getItemStack().getMiningSpeedMultiplier(blockState) > 1.0f
					&& this.canMine(blockState, context.getWorld(), context.getBlockPos(), context.getPlayer())) {
				return InteractionType.NO_ACTION;
			}
		}
		if (context.isWithEntity() && !DynamicCrosshairMod.config.dynamicCrosshairMeleeWeaponOnEntity()) {
			return InteractionType.NO_ACTION;
		}
		return InteractionType.MELEE_WEAPON;
	}
}
