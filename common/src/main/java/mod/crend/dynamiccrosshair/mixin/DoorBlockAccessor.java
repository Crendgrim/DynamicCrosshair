package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.DoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DoorBlock.class)
public interface DoorBlockAccessor {
	@Accessor
	BlockSetType getBlockSetType();
}
