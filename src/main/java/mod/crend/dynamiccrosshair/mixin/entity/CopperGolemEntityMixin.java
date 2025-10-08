//? if >1.21.8 {
/*package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CopperGolemEntity.class)
public abstract class CopperGolemEntityMixin extends MobEntityMixin implements Shearable, DynamicCrosshairEntity {
	@Shadow private long nextOxidationAge;

	@Shadow public abstract Oxidizable.OxidationLevel getOxidationLevel();

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		ItemStack itemStack = context.getItemStack();
		if (itemStack.isEmpty()) {
			if (!getMainHandStack().isEmpty()) {
				return InteractionType.TAKE_ITEM_FROM_ENTITY;
			}
		}

		if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		} else if (itemStack.isOf(Items.HONEYCOMB) && this.nextOxidationAge != -2L) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		} else if (itemStack.isIn(ItemTags.AXES)) {
			if (this.nextOxidationAge == -2L || this.getOxidationLevel() != Oxidizable.OxidationLevel.UNAFFECTED) {
				return InteractionType.USE_ITEM_ON_ENTITY;
			}
		}
		return super.dynamiccrosshair$compute(context);
	}
}
*///?}
