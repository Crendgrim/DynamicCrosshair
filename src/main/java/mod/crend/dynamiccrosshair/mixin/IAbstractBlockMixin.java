package mod.crend.dynamiccrosshair.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBlock.class)
public interface IAbstractBlockMixin {
    @Accessor("material")
    public Material getMaterial();
}
