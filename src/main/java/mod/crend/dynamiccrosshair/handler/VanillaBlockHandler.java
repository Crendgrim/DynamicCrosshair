package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.component.ModifierUse;
import mod.crend.dynamiccrosshair.mixin.IAbstractBlockMixin;
import mod.crend.dynamiccrosshair.mixin.IFlowerPotBlockMixin;
import mod.crend.dynamiccrosshair.mixin.IRedstoneWireBlockMixin;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.*;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class VanillaBlockHandler {
    public static Crosshair checkToolWithBlock(CrosshairContext context) {
        Item handItem = context.getItem();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = context.getBlockState();
        if (blockState == null) {
            return null;
        }
        if (handItem instanceof MiningToolItem) {
            if (handItem.isSuitableFor(blockState)
                    && handItem.canMine(blockState, context.world, blockPos, context.player)) {
                return Crosshair.CORRECT_TOOL;
            } else {
                return Crosshair.INCORRECT_TOOL;
            }
        }
        if (handItem.getMiningSpeedMultiplier(context.getItemStack(), blockState) > 1.0f
                && handItem.canMine(blockState, context.world, blockPos, context.player)) {
            return Crosshair.CORRECT_TOOL;
        }
        if (handItem instanceof ShearsItem) {
            // (shears item && correct tool) is handled by the getMiningSpeedMultiplier branch
            return Crosshair.INCORRECT_TOOL;
        }
        return null;
    }

    public static boolean isBlockInteractable(BlockState blockState) {
        Block block = blockState.getBlock();
        return (    block instanceof AbstractChestBlock
                ||  block instanceof AbstractFurnaceBlock
                ||  block instanceof BarrelBlock
                ||  block instanceof BeaconBlock
                ||  block instanceof BellBlock
                ||  block instanceof BrewingStandBlock
                ||  block instanceof CommandBlock
                ||  block instanceof DaylightDetectorBlock
                ||  block instanceof DispenserBlock
                ||  block instanceof EnchantingTableBlock
                ||  block instanceof HopperBlock
                ||  block instanceof JukeboxBlock
                ||  block instanceof LecternBlock
                ||  block instanceof ShulkerBoxBlock
                ||  block instanceof StonecutterBlock
                ||  block instanceof GrindstoneBlock
                ||  block instanceof CartographyTableBlock
                ||  block instanceof LoomBlock
                ||  block instanceof BedBlock
                || (block instanceof TrapdoorBlock && ((IAbstractBlockMixin) block).getMaterial() != Material.METAL)
                || (block instanceof DoorBlock && ((IAbstractBlockMixin) block).getMaterial() != Material.METAL)
                || (block instanceof FenceGateBlock && ((IAbstractBlockMixin) block).getMaterial() != Material.METAL)
                ||  block instanceof AbstractButtonBlock
                ||  block instanceof NoteBlock
                ||  block instanceof LeverBlock
                ||  block instanceof AbstractRedstoneGateBlock
                ||  block instanceof AnvilBlock
                || (block instanceof CraftingTableBlock && !(block instanceof FletchingTableBlock))
                ||  block instanceof ComposterBlock
                ||  block instanceof FlowerPotBlock
                ||  block instanceof CakeBlock
                ||  block instanceof SweetBerryBushBlock
                ||  block instanceof AbstractCandleBlock
                ||  block instanceof CampfireBlock
        );
    }

    public static Crosshair checkBlockInteractable(CrosshairContext context) {
        BlockState blockState = context.getBlockState();
        Block block = blockState.getBlock();
        if (        block instanceof AbstractChestBlock
                ||  block instanceof AbstractFurnaceBlock
                ||  block instanceof BarrelBlock
                ||  block instanceof BeaconBlock
                ||  block instanceof BellBlock
                ||  block instanceof BrewingStandBlock
                || (block instanceof CommandBlock && context.player.isCreative())
                ||  block instanceof DaylightDetectorBlock
                ||  block instanceof DispenserBlock
                ||  block instanceof EnchantingTableBlock
                ||  block instanceof HopperBlock
                || (block instanceof JukeboxBlock && blockState.get(JukeboxBlock.HAS_RECORD))
                || (block instanceof LecternBlock && blockState.get(LecternBlock.HAS_BOOK))
                ||  block instanceof ShulkerBoxBlock
                ||  block instanceof StonecutterBlock
                ||  block instanceof GrindstoneBlock
                ||  block instanceof CartographyTableBlock
                ||  block instanceof LoomBlock
                ||  block instanceof BedBlock
                || (block instanceof TrapdoorBlock && ((IAbstractBlockMixin) block).getMaterial() != Material.METAL)
                || (block instanceof DoorBlock && ((IAbstractBlockMixin) block).getMaterial() != Material.METAL)
                || (block instanceof FenceGateBlock && ((IAbstractBlockMixin) block).getMaterial() != Material.METAL)
                ||  block instanceof AbstractButtonBlock
                ||  block instanceof NoteBlock
                ||  block instanceof LeverBlock
                ||  block instanceof AbstractRedstoneGateBlock
                ||  block instanceof AnvilBlock
                || (block instanceof CraftingTableBlock && !(block instanceof FletchingTableBlock))
                || (block instanceof ComposterBlock && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(context.getItem()))
        ) {
            return Crosshair.INTERACTABLE;
        }
        // Special case: Flower pots behave oddly
        if (block instanceof FlowerPotBlock) {
            Item handItem = context.getItem();
            boolean potItemIsAir = ((FlowerPotBlock) block).getContent() == Blocks.AIR;
            boolean handItemIsPottable = handItem instanceof BlockItem && IFlowerPotBlockMixin.getCONTENT_TO_POTTED().containsKey(((BlockItem) handItem).getBlock());
            if (potItemIsAir && handItemIsPottable) {
                return Crosshair.USABLE;
            }
            if (!potItemIsAir && !handItemIsPottable) {
                return Crosshair.INTERACTABLE.withFlag(Crosshair.Flag.FixedStyle);
            }
            return Crosshair.NONE.withFlag(Crosshair.Flag.FixedStyle, Crosshair.Flag.FixedModifierUse);
        }

        // Special case: Signs eat all inputs
        if (block instanceof AbstractSignBlock) {
            Item handItem = context.getItem();
            if (handItem instanceof DyeItem || handItem.equals(Items.GLOW_INK_SAC) || handItem.equals(Items.INK_SAC)) {
                BlockEntity blockEntity = context.getBlockEntity();
                if (blockEntity instanceof SignBlockEntity signBlockEntity) {
                    if (handItem.equals(Items.GLOW_INK_SAC) && !signBlockEntity.isGlowingText())
                        return Crosshair.USABLE;
                    if (handItem.equals(Items.INK_SAC) && signBlockEntity.isGlowingText())
                        return Crosshair.USABLE;
                    if (handItem instanceof DyeItem && signBlockEntity.getTextColor() != ((DyeItem) handItem).getColor())
                        return Crosshair.USABLE;
                }
            }
            return Crosshair.NONE.withFlag(Crosshair.Flag.FixedStyle, Crosshair.Flag.FixedModifierUse);
        }

        // Special case: Cake gets eaten (modified), so "use" makes more sense to me
        if (block instanceof CakeBlock) {
            if (context.player.canConsume(false) && context.shouldInteract()) {
                return Crosshair.USABLE;
            }
        }
        // Special case: Sweet berries get harvested
        if (block instanceof SweetBerryBushBlock && blockState.get(SweetBerryBushBlock.AGE) > 1) {
            return Crosshair.USABLE;
        }
        // Special case: Glow berries get harvested
        if (block instanceof CaveVines && CaveVines.hasBerries(blockState)) {
            return Crosshair.USABLE;
        }
        // Special case: Redstone ore: can be placed against, but still activates
        if (block instanceof RedstoneOreBlock) {
            if (context.shouldInteract()) {
                // Allow extra crosshair style
                return new Crosshair(ModifierUse.USE_ITEM).withFlag(Crosshair.Flag.FixedModifierUse);
            }
        }

        if (block instanceof AbstractCandleBlock && blockState.get(AbstractCandleBlock.LIT)) {
            Item mainItem = context.getItem();
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
                    || mainItem.getUseAction(context.getItemStack()) == UseAction.DRINK
                    || (mainItem.getUseAction(context.getItemStack()) == UseAction.EAT && context.player.canConsume(false)))) {
                return Crosshair.INTERACTABLE;
            }
        }

        if (block instanceof LecternBlock) {
            Item handItem = context.getItem();
            if (handItem.equals(Items.WRITTEN_BOOK)
                    || handItem.equals(Items.WRITABLE_BOOK)
                    || (!context.player.shouldCancelInteraction() && blockState.get(LecternBlock.HAS_BOOK)))
                return Crosshair.USABLE;
            return Crosshair.NONE.withFlag(Crosshair.Flag.FixedModifierUse);
        }

        if (block instanceof CampfireBlock) {
            if (context.getBlockEntity() instanceof CampfireBlockEntity campfire && campfire.getRecipeFor(context.getItemStack()).isPresent())
                return Crosshair.USABLE;
        }

        if (block instanceof RedstoneWireBlock) {
            if (IRedstoneWireBlockMixin.invokeIsFullyConnected(blockState) || IRedstoneWireBlockMixin.invokeIsNotConnected(blockState)) {
                return Crosshair.INTERACTABLE;
            }
        }

        if (blockState.isIn(BlockTags.FENCES)) {
            BlockPos pos = context.getBlockPos();
            List<MobEntity> list = context.world.getNonSpectatingEntities(MobEntity.class,
                    new Box((double) pos.getX() - 7.0, (double) pos.getY() - 7.0, (double) pos.getZ() - 7.0,
                            (double) pos.getX() + 7.0, (double) pos.getY() + 7.0, (double) pos.getZ() + 7.0));

            for (MobEntity mob : list) {
                if (mob.getHoldingEntity() == context.player) {
                    return Crosshair.USABLE;
                }
            }
        }

        return null;
    }

}
