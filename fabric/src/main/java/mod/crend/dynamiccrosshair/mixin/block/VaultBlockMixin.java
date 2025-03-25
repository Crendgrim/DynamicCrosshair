package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.VersionUtils;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import org.spongepowered.asm.mixin.Mixin;

//? if >=1.20.5 {
/*import net.minecraft.block.VaultBlock;
import net.minecraft.block.enums.VaultState;

*///?}

//? if >=1.20.5 {
/*@Mixin(VaultBlock.class)
*///?} else {
@Mixin(VersionUtils.class)
//?}
public class VaultBlockMixin implements DynamicCrosshairBlock {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		//? if >=1.20.5 {
		/*if (!context.getItemStack().isEmpty() && context.getBlockState().get(VaultBlock.VAULT_STATE) == VaultState.ACTIVE) {
			return InteractionType.USE_ITEM_ON_BLOCK;
		}
		*///?}
		return InteractionType.EMPTY;
	}
}
