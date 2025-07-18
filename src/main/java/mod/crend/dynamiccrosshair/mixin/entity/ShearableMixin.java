package mod.crend.dynamiccrosshair.mixin.entity;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairEntity;
import net.minecraft.entity.Shearable;

//? if >=1.20.5
/*import net.minecraft.entity.mob.BoggedEntity;*/
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;

//? if forge && <1.21
/*import net.minecraftforge.common.IForgeShearable;*/
//? if neoforge
/*import net.neoforged.neoforge.common.IShearable;*/

@Mixin({
		//? if >=1.20.5
		/*BoggedEntity.class,*/
		MooshroomEntity.class,
		SheepEntity.class,
		SnowGolemEntity.class
})
public abstract class ShearableMixin extends MobEntityMixin
		implements DynamicCrosshairEntity,
		/*? if forge && <1.21 {*//*IForgeShearable
		*//*?} else if neoforge {*//*IShearable
		*//*?} else {*/Shearable
		/*?}*/ {

	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		if (
			//? if forge {
			/*isShearable(context.getItemStack(), context.getWorld(), context.getBlockPos())
			*///?} else if neoforge {
			/*isShearable(context.getPlayer(), context.getItemStack(), context.getWorld(), context.getBlockPos())
			*///?} else {
			isShearable() && context.getItemStack().isOf(Items.SHEARS)
			//?}
		) {
			return InteractionType.USE_ITEM_ON_ENTITY;
		}
		return super.dynamiccrosshair$compute(context);
	}
}
