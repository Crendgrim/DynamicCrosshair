package mod.crend.dynamiccrosshair.neoforge.mixin.item;

import com.mojang.datafixers.util.Pair;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.config.UsableCrosshairPolicy;
import mod.crend.dynamiccrosshair.neoforge.mixin.DynamicCrosshairBaseItem;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.block.Block;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(HoeItem.class)
public class HoeItemMixin extends DynamicCrosshairBaseItem implements DynamicCrosshairItem {
	@Shadow @Final protected static Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLING_ACTIONS;

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock() && DynamicCrosshairMod.config.dynamicCrosshairHoldingUsableItem() != UsableCrosshairPolicy.Disabled) {
			if (TILLING_ACTIONS.get(context.getBlock()) != null) {
				return InteractionType.USABLE_TOOL;
			}
		}
		return super.dynamiccrosshair$compute(context);
	}
}
