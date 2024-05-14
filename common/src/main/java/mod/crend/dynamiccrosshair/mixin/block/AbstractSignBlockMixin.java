package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.InteractionType;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SignChangingItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractSignBlock.class)
public abstract class AbstractSignBlockMixin extends BlockMixin implements DynamicCrosshairBlock {
	public AbstractSignBlockMixin(Settings settings) {
		super(settings);
	}

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		// Special case: Signs eat all inputs
		if (context.getBlockEntity() instanceof SignBlockEntity signBlockEntity) {
			Item handItem = context.getItem();
			SignText signText = signBlockEntity.getText(signBlockEntity.isPlayerFacingFront(context.getPlayer()));

			if (signBlockEntity.isWaxed()) {
				if (signText.hasRunCommandClickEvent(context.getPlayer())) {
					return InteractionType.INTERACT_WITH_ENTITY;
				}
				return InteractionType.NO_ACTION;
			} else {
				if (handItem instanceof SignChangingItem) {
					if (signText.hasText(context.getPlayer())) {
						if (handItem.equals(Items.GLOW_INK_SAC) && !signText.isGlowing()) {
							return InteractionType.USE_ITEM_ON_ENTITY;
						}
						if (handItem.equals(Items.INK_SAC) && signText.isGlowing()) {
							return InteractionType.USE_ITEM_ON_ENTITY;
						}
						if (handItem instanceof DyeItem dye && signText.getColor() != dye.getColor()) {
							return InteractionType.USE_ITEM_ON_ENTITY;
						}
					}
					if (handItem.equals((Items.HONEYCOMB))) {
						return InteractionType.USE_ITEM_ON_ENTITY;
					}
				}
				return InteractionType.INTERACT_WITH_ENTITY;
			}
		}
		return super.dynamiccrosshair$compute(context);
	}
}
