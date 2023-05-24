package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.FlowerPotBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(FlowerPotBlock.class)
public interface FlowerPotBlockAccessor {
    @Accessor
    static Map<Block, Block> getCONTENT_TO_POTTED() {
        throw new AssertionError();
    }
}
