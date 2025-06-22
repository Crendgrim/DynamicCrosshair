package mod.crend.dynamiccrosshair.mixin.block;

import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.type.DynamicCrosshairBlock;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CartographyTableBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.LoomBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.StonecutterBlock;
import org.spongepowered.asm.mixin.Mixin;

//? if >=1.20.3
/*import net.minecraft.block.CrafterBlock;*/

@Mixin(value = {
		AbstractChestBlock.class,
		AbstractFurnaceBlock.class,
		AbstractRedstoneGateBlock.class,
		BeaconBlock.class,
		BedBlock.class,
		BellBlock.class,
		BrewingStandBlock.class,
		ButtonBlock.class,
		CartographyTableBlock.class,
		//? if >=1.20.3
		/*CrafterBlock.class,*/
		CraftingTableBlock.class,
		DaylightDetectorBlock.class,
		DispenserBlock.class,
		DropperBlock.class,
		EnchantingTableBlock.class,
		FenceGateBlock.class,
		GrindstoneBlock.class,
		HopperBlock.class,
		LeverBlock.class,
		LoomBlock.class,
		NoteBlock.class,
		ShulkerBoxBlock.class,
		StonecutterBlock.class,
})
public class AlwaysInteractableBlockMixin implements DynamicCrosshairBlock {
	@Override
	public InteractionType dynamiccrosshair$compute(CrosshairContext context) {
		return InteractionType.INTERACT_WITH_BLOCK;
	}
}
