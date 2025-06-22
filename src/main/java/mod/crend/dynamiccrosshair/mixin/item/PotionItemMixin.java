package mod.crend.dynamiccrosshair.mixin.item;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairItem;
import net.minecraft.item.PotionItem;


import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;

//? if <1.20.5 {
import net.minecraft.potion.PotionUtil;
//?} else {
/*import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
*///?}

@Mixin(PotionItem.class)
public class PotionItemMixin implements DynamicCrosshairItem {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (context.isWithBlock()) {
			if (context.getBlockHitSide() != Direction.DOWN
					&& context.getBlockState().isIn(BlockTags.CONVERTABLE_TO_MUD)
					//? if <1.20.5 {
					&& PotionUtil.getPotion(context.getItemStack()) == Potions.WATER
					//?} else {
					/*&& context.getItemStack().getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).matches(Potions.WATER)
					*///?}
			) {
				return InteractionType.USE_ITEM_ON_BLOCK;
			}
		}
		return InteractionType.USE_ITEM;
	}
}
