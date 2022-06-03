package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.api.IBlockBreakHandler;
import mod.crend.dynamiccrosshair.api.IBlockInteractHandler;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.ModifierUse;
import mod.crend.dynamiccrosshair.mixin.IAbstractBlockMixin;
import mod.crend.dynamiccrosshair.mixin.IFlowerPotBlockMixin;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;

public class VanillaBlockHandler implements IBlockBreakHandler, IBlockInteractHandler {
    @Override
    public Crosshair checkBlockBreaking(ClientPlayerEntity player, ItemStack itemStack, BlockPos blockPos, BlockState blockState) {
        Item handItem = player.getMainHandStack().getItem();
        if (handItem instanceof MiningToolItem) {
            if (handItem.isSuitableFor(blockState)
                    && handItem.canMine(blockState, MinecraftClient.getInstance().world, blockPos, player)) {
                return Crosshair.CORRECT_TOOL;
            } else {
                return Crosshair.INCORRECT_TOOL;
            }
        }
        if (handItem instanceof ShearsItem) {
            Block block = blockState.getBlock();
            if (blockState.isIn(BlockTags.LEAVES)
                    || blockState.isIn(BlockTags.WOOL)
                    || block.equals(Blocks.COBWEB)
                    || block.equals(Blocks.VINE)
                    || block.equals(Blocks.GLOW_LICHEN)) {
                return Crosshair.CORRECT_TOOL;
            } else {
                return Crosshair.INCORRECT_TOOL;
            }
        }
        return null;
    }

    @Override
    public Crosshair checkBlockInteractable(ClientPlayerEntity player, ItemStack itemStack, BlockPos blockPos, BlockState blockState) {
        Block block = blockState.getBlock();
        if (block instanceof BlockWithEntity) {
            // Skip the following...
            if (!(     block instanceof BeehiveBlock
                    || block instanceof AbstractSignBlock
                    || (blockState.isOf(Blocks.LECTERN) && !blockState.get(LecternBlock.HAS_BOOK))
                    || block instanceof CampfireBlock
                    || block instanceof BannerBlock
            )) {
                return Crosshair.INTERACTABLE;
            }
        }
        if (        block instanceof StonecutterBlock
                ||  block instanceof GrindstoneBlock
                ||  block instanceof CartographyTableBlock
                ||  block instanceof LoomBlock
                ||  block instanceof BedBlock
                || (block instanceof TrapdoorBlock && ((IAbstractBlockMixin) block).getMaterial() != Material.METAL)
                || (block instanceof DoorBlock && ((IAbstractBlockMixin) block).getMaterial() != Material.METAL)
                ||  block instanceof FenceGateBlock
                ||  block instanceof AbstractButtonBlock
                ||  block instanceof NoteBlock
                ||  block instanceof LeverBlock
                ||  block instanceof AbstractRedstoneGateBlock
                ||  block instanceof AnvilBlock
                || (block instanceof CraftingTableBlock && !(block instanceof FletchingTableBlock))
                || (block instanceof ComposterBlock && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(itemStack.getItem()))
        ) {
            return Crosshair.INTERACTABLE;
        }
        // Special case: Flower pots behave oddly
        if (block instanceof FlowerPotBlock) {
            Item handItem = itemStack.getItem();
            boolean potItemIsAir = ((FlowerPotBlock) block).getContent() == Blocks.AIR;
            boolean handItemIsPottable = handItem instanceof BlockItem && IFlowerPotBlockMixin.getCONTENT_TO_POTTED().containsKey(((BlockItem) handItem).getBlock());
            if (potItemIsAir && handItemIsPottable) {
                return Crosshair.USE_ITEM;
            }
            if (!potItemIsAir && !handItemIsPottable) {
                return Crosshair.INTERACTABLE.withFlag(Crosshair.Flag.FixedStyle);
            }
            return Crosshair.NONE.withFlag(Crosshair.Flag.FixedStyle, Crosshair.Flag.FixedModifierUse);
        }

        // Special case: Signs eat all inputs
        if (block instanceof AbstractSignBlock) {
            Item handItem = itemStack.getItem();
            if (handItem instanceof DyeItem || handItem.equals(Items.GLOW_INK_SAC) || handItem.equals(Items.INK_SAC)) {
                BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(blockPos);
                if (blockEntity instanceof SignBlockEntity) {
                    if (handItem.equals(Items.GLOW_INK_SAC) && !((SignBlockEntity) blockEntity).isGlowingText())
                        return Crosshair.USE_ITEM;
                    if (handItem.equals(Items.INK_SAC) && ((SignBlockEntity) blockEntity).isGlowingText())
                        return Crosshair.USE_ITEM;
                    if (handItem instanceof DyeItem && ((SignBlockEntity) blockEntity).getTextColor() != ((DyeItem) handItem).getColor())
                        return Crosshair.USE_ITEM;
                }
            }
            return Crosshair.NONE.withFlag(Crosshair.Flag.FixedStyle, Crosshair.Flag.FixedModifierUse);
        }

        // Special case: Cake gets eaten (modified), so "use" makes more sense to me
        if (block instanceof CakeBlock) {
            if (player.getHungerManager().isNotFull() && (!player.shouldCancelInteraction() || (player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty()))) {
                return Crosshair.USE_ITEM;
            }
        }
        // Special case: Redstone ore: can be placed against, but still activates
        if (block instanceof RedstoneOreBlock) {
            if (!player.shouldCancelInteraction() || (player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty())) {
                // Allow extra crosshair style
                return new Crosshair(ModifierUse.USE_ITEM).withFlag(Crosshair.Flag.FixedModifierUse);
            }
        }

        if (block instanceof AbstractCandleBlock && blockState.get(AbstractCandleBlock.LIT)) {
            Item mainItem = itemStack.getItem();
            // The following items block candle extinguish
            if (!(mainItem.equals(Items.FLINT_AND_STEEL)
                    || mainItem instanceof BlockItem
                    || mainItem instanceof SpawnEggItem
                    || mainItem instanceof FireChargeItem
                    || mainItem instanceof EnderEyeItem
                    || mainItem instanceof EnderPearlItem
                    || mainItem instanceof WritableBookItem
                    || mainItem instanceof WrittenBookItem
                    || mainItem instanceof PotionItem
                    || mainItem.getUseAction(itemStack) == UseAction.DRINK
                    || (mainItem.getUseAction(itemStack) == UseAction.EAT && player.getHungerManager().isNotFull()))) {
                return Crosshair.INTERACTABLE;
            }
        }

        if (block instanceof LecternBlock) {
            Item handItem = itemStack.getItem();
            if (handItem.equals(Items.WRITTEN_BOOK)
                    || handItem.equals(Items.WRITABLE_BOOK)
                    || (!player.shouldCancelInteraction() && blockState.get(LecternBlock.HAS_BOOK)))
                return Crosshair.USE_ITEM;
            return Crosshair.NONE.withFlag(Crosshair.Flag.FixedModifierUse);
        }

        if (block instanceof CampfireBlock) {
            BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(blockPos);
            if (blockEntity instanceof CampfireBlockEntity && (((CampfireBlockEntity) blockEntity).getRecipeFor(itemStack)).isPresent())
                return Crosshair.USE_ITEM;
        }

        return null;
    }

}
