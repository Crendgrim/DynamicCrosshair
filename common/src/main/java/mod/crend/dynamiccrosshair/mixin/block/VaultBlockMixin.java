package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.enums.VaultState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VaultBlock.class)
public class VaultBlockMixin implements DynamicCrosshairBlock {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (!context.getItemStack().isEmpty() && context.getBlockState().get(VaultBlock.VAULT_STATE) == VaultState.ACTIVE) {
			return InteractionType.USE_ITEM_ON_BLOCK;
		}
		return InteractionType.EMPTY;
	}
}
