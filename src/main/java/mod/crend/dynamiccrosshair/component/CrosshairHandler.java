package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.config.CrosshairPolicy;
import mod.crend.dynamiccrosshair.config.InteractableCrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.*;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;

import java.util.LinkedList;
import java.util.List;

public class CrosshairHandler {

    public static final Identifier crosshairTexture = new Identifier("dynamiccrosshair", "textures/gui/crosshairs.png");
    enum ModifierUse {
        NONE,
        USE_ITEM,
        INTERACTABLE
    }
    enum ModifierHit {
        NONE,
        CORRECT_TOOL,
        INCORRECT_TOOL
    }

    private static Crosshair activeCrosshair = Crosshair.DEFAULT;
    private static boolean shouldShowCrosshair = true;
    private static ModifierUse modifierUse = CrosshairHandler.ModifierUse.NONE;
    private static ModifierHit modifierHit = ModifierHit.NONE;

    public static Crosshair getActiveCrosshair() {
        return activeCrosshair;
    }

    private static boolean policyMatches(CrosshairPolicy policy, HitResult hitResult) {
        return (policy == CrosshairPolicy.Always || (policy == CrosshairPolicy.IfTargeting && hitResult.getType() != HitResult.Type.MISS));
    }
    private static boolean policyMatches(BlockCrosshairPolicy policy, HitResult hitResult) {
        return (policy == BlockCrosshairPolicy.Always || (policy != BlockCrosshairPolicy.Disabled && hitResult.getType() == HitResult.Type.BLOCK));
    }

    // Return true if main hand item is usable
    private static boolean checkHand(ClientPlayerEntity player, ItemStack handItemStack, HitResult hitResult) {
        Item handItem = handItemStack.getItem();
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            if (canUseItem(player, handItemStack, hitResult, handItem, false)) {
                modifierUse = ModifierUse.USE_ITEM;
                return true;
            } else {
                modifierUse = checkEntityModifiers(player, handItemStack, hitResult);
                return modifierUse != ModifierUse.NONE;
            }
        } else if (!handItemStack.isEmpty()) {
            boolean ignoreUse = false;
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockState blockState = MinecraftClient.getInstance().world.getBlockState(((BlockHitResult) hitResult).getBlockPos());
                Block block = blockState.getBlock();

                // Special case: cake eats input
                if (block instanceof CakeBlock && !player.shouldCancelInteraction() && player.getHungerManager().isNotFull()) {
                    ignoreUse = true;
                }
                // Special case: signs and flower pots eat inputs
                if (!player.shouldCancelInteraction() && (block instanceof SignBlock || block instanceof FlowerPotBlock)) {
                    ignoreUse = true;
                }
            }
            if (canUseItem(player, handItemStack, hitResult, handItem, ignoreUse)) {
                modifierUse = ModifierUse.USE_ITEM;
                return true;
            } else if (!ignoreUse && checkRangedWeapon(hitResult, handItem)) {
                activeCrosshair = DynamicCrosshair.config.getCrosshairStyleHoldingRangedWeapon();
                return true;
            } else if (!ignoreUse && checkThrowable(player, hitResult, handItem)) {
                activeCrosshair = DynamicCrosshair.config.getCrosshairStyleHoldingThrowable();
                return true;
            } else if (checkTool(player, handItem, hitResult, ignoreUse)) {
                activeCrosshair = DynamicCrosshair.config.getCrosshairStyleHoldingTool();
                return true;
            } else if (!ignoreUse && checkBlock(player, handItemStack, hitResult, handItem)) {
                activeCrosshair = DynamicCrosshair.config.getCrosshairStyleHoldingBlock();
                return true;
            }
        } else {
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
            if (blockState.isOf(Blocks.LECTERN) && blockState.get(LecternBlock.HAS_BOOK)) {
                modifierUse = ModifierUse.USE_ITEM;
                return true;
            }
        }

        return false;
    }

    private static boolean checkBlock(ClientPlayerEntity player, ItemStack handItemStack, HitResult hitResult, Item handItem) {
        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingBlock(), hitResult)) {
            if (handItem instanceof BlockItem) {
                if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.IfInteractable) {
                    IBlockItemMixin blockItem = (IBlockItemMixin) handItem;
                    ItemPlacementContext itemPlacementContext = new ItemPlacementContext(player, player.getActiveHand(), handItemStack, (BlockHitResult) hitResult);
                    BlockState blockState = blockItem.invokeGetPlacementState(itemPlacementContext);
                    if (blockState != null && blockItem.invokeCanPlace(itemPlacementContext, blockState)) return true;
                } else return true;
            }
            if (handItem instanceof ArmorStandItem) return true;
            if (handItem instanceof MinecartItem) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                    BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
                    if (blockState.isIn(BlockTags.RAILS)) return true;
                }
            }
            if (handItem instanceof EndCrystalItem) {
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                    BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    if ((block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) && MinecraftClient.getInstance().world.isAir(blockPos.up())) {
                        return true;
                    }
                }
            }
        }
        if (handItem instanceof BoatItem) {
            BlockHitResult boatHitResult = IItemMixin.invokeRaycast(MinecraftClient.getInstance().world, player, RaycastContext.FluidHandling.ANY);
            if (boatHitResult.getType() == HitResult.Type.BLOCK) {
                return true;
            }
        }
        return false;
    }

    // Tools & Melee Weapons
    private static void checkBreakable(ClientPlayerEntity player, Item handItem, HitResult hitResult) {
        if (DynamicCrosshair.config.dynamicCrosshairHoldingTool() == CrosshairPolicy.Disabled) return;
        if (DynamicCrosshair.config.isDynamicCrosshairStyle() && hitResult.getType() == HitResult.Type.BLOCK) {
            if (handItem instanceof MiningToolItem) {
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
                if (handItem.isSuitableFor(blockState)
                        && handItem.canMine(blockState, MinecraftClient.getInstance().world, blockPos, player)) {
                    modifierHit = ModifierHit.CORRECT_TOOL;
                } else {
                    modifierHit = ModifierHit.INCORRECT_TOOL;
                }
                activeCrosshair = DynamicCrosshair.config.getCrosshairStyleHoldingTool();
            }
            if (handItem instanceof ShearsItem) {
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                if (blockState.isIn(BlockTags.LEAVES)
                        || blockState.isIn(BlockTags.WOOL)
                        || block.equals(Blocks.COBWEB)
                        || block.equals(Blocks.VINE)
                        || block.equals(Blocks.GLOW_LICHEN)) {
                    modifierHit = ModifierHit.CORRECT_TOOL;
                } else {
                    modifierHit = ModifierHit.INCORRECT_TOOL;
                }
                activeCrosshair = DynamicCrosshair.config.getCrosshairStyleHoldingTool();
            }
        }
    }
    private static boolean checkTool(ClientPlayerEntity player, Item handItem, HitResult hitResult, boolean ignoreUse) {
        if (!policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingTool(), hitResult)) return false;
        if (handItem instanceof ToolItem) {
            if (DynamicCrosshair.config.isDynamicCrosshairStyle() && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
                if (handItem instanceof AxeItem) {
                    if (IAxeItemMixin.getSTRIPPED_BLOCKS().get(blockState.getBlock()) != null
                            || Oxidizable.getDecreasedOxidationBlock(blockState.getBlock()).isPresent()
                            || HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(blockState.getBlock()) != null) {
                        modifierUse = CrosshairHandler.ModifierUse.USE_ITEM;
                    }
                } else if (handItem instanceof ShovelItem) {
                    if (IShovelItemMixin.getPATH_STATES().get(blockState.getBlock()) != null) {
                        modifierUse = CrosshairHandler.ModifierUse.USE_ITEM;
                    }
                } else if (handItem instanceof HoeItem) {
                    if (IHoeItemMixin.getTILLING_ACTIONS().get(blockState.getBlock()) != null) {
                        modifierUse = CrosshairHandler.ModifierUse.USE_ITEM;
                    }
                }
            }
            return true;
        }
        if (handItem instanceof FlintAndSteelItem && !ignoreUse) {
            if (hitResult.getType() == HitResult.Type.BLOCK) modifierUse = CrosshairHandler.ModifierUse.USE_ITEM;
            return true;
        }
        if (handItem instanceof ShearsItem) {
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(((BlockHitResult) hitResult).getBlockPos());
            if (blockState.getBlock() instanceof AbstractPlantStemBlock && !((AbstractPlantStemBlock)blockState.getBlock()).hasMaxAge(blockState)) {
                modifierUse = CrosshairHandler.ModifierUse.USE_ITEM;
            }
            if (!player.shouldCancelInteraction() && blockState.getBlock() instanceof BeehiveBlock && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
                modifierUse = CrosshairHandler.ModifierUse.USE_ITEM;
            }
            return true;
        }
        if (handItem instanceof FishingRodItem && !ignoreUse) {
            modifierUse = CrosshairHandler.ModifierUse.USE_ITEM;
            return true;
        }
        return false;
    }

    private static boolean checkThrowable(ClientPlayerEntity player, HitResult hitResult, Item handItem) {
        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingThrowable(), hitResult)) {
            if (handItem instanceof EggItem) return true;
            if (handItem instanceof SnowballItem) return true;
            if (handItem instanceof ThrowablePotionItem) return true;
            if (handItem instanceof ExperienceBottleItem) return true;
            if (handItem instanceof EnderPearlItem && !player.getItemCooldownManager().isCoolingDown(handItem)) return true;
        }
        return false;
    }

    private static boolean checkRangedWeapon(HitResult hitResult, Item handItem) {
        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingRangedWeapon(), hitResult)) {
            if (handItem instanceof RangedWeaponItem) return true;
            if (handItem instanceof TridentItem) return true;
        }
        return false;
    }

    private static boolean canUseItem(ClientPlayerEntity player, ItemStack handItemStack, HitResult hitResult, Item handItem, boolean ignoreUse) {
        if (DynamicCrosshair.config.dynamicCrosshairHoldingUsableItem() == BlockCrosshairPolicy.Disabled) return false;
        if (DynamicCrosshair.config.dynamicCrosshairHoldingUsableItem() == BlockCrosshairPolicy.Always
                || (DynamicCrosshair.config.dynamicCrosshairHoldingUsableItem() == BlockCrosshairPolicy.IfTargeting && hitResult.getType() == HitResult.Type.BLOCK)) {
            return (handItem.isFood()
                    || handItem.getUseAction(handItemStack) == UseAction.DRINK
                    || handItem instanceof SpawnEggItem
                    || handItem instanceof FireChargeItem
                    || handItem instanceof MusicDiscItem
                    || handItem instanceof HoneycombItem
                    || handItem instanceof EnderEyeItem
                    || handItem instanceof GlassBottleItem
                    || handItem instanceof PotionItem
                    || handItem instanceof BucketItem
                    || handItem instanceof BoneMealItem
                    || handItem instanceof WritableBookItem
                    || handItem instanceof WrittenBookItem);
        }
        if (ignoreUse) return false;

        // Enable crosshair on food and drinks also when not targeting if "when interactable" is chosen
        if (handItem.isFood()) {
            if (handItem instanceof ChorusFruitItem) {
                if (!player.getItemCooldownManager().isCoolingDown(handItem)) return true;
            }
            else if (player.getHungerManager().isNotFull() || handItem.getFoodComponent().isAlwaysEdible()) return true;
        }
        if (handItem.getUseAction(handItemStack) == UseAction.DRINK) return true;

        if (handItem instanceof SpawnEggItem && hitResult.getType() != HitResult.Type.MISS) return true;

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(((BlockHitResult) hitResult).getBlockPos());
            Block block = blockState.getBlock();
            if (handItem instanceof FireChargeItem) return true;
            if (handItem instanceof MusicDiscItem && block.equals(Blocks.JUKEBOX)) return true;
            if (handItem instanceof HoneycombItem && HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().get(block) != null)
                return true;
            if (handItem instanceof EnderEyeItem && block.equals(Blocks.END_PORTAL_FRAME)) return true;
            if (handItem instanceof GlassBottleItem) {
                if (block.equals(Blocks.WATER_CAULDRON) && !player.shouldCancelInteraction()) return true;
                if (block instanceof BeehiveBlock && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5 && !player.shouldCancelInteraction()) return true;
            }
            if (handItem instanceof PotionItem && PotionUtil.getPotion(handItemStack) == Potions.WATER) {
                if (block.equals(Blocks.CAULDRON) && !player.shouldCancelInteraction()) return true;
            }
            if (handItem instanceof BucketItem) {
                if (handItem instanceof EntityBucketItem) {
                    if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled) {
                        activeCrosshair = DynamicCrosshair.config.getCrosshairStyleHoldingBlock();
                    }
                    return true;
                }
                if (block.equals(Blocks.WATER_CAULDRON) && !player.shouldCancelInteraction()) return true;
                if (block.equals(Blocks.LAVA_CAULDRON) && !player.shouldCancelInteraction()) return true;
                if (block.equals(Blocks.POWDER_SNOW_CAULDRON) && !player.shouldCancelInteraction()) return true;
                if (handItem == Items.WATER_BUCKET || handItem == Items.LAVA_BUCKET) {
                    if (block.equals(Blocks.CAULDRON) && !player.shouldCancelInteraction()) return true;
                    if (DynamicCrosshair.config.dynamicCrosshairHoldingBlock() != BlockCrosshairPolicy.Disabled) {
                        activeCrosshair = DynamicCrosshair.config.getCrosshairStyleHoldingBlock();
                        return false;
                    }
                    return true;
                } else if (block.equals(Blocks.POWDER_SNOW)) return true;
            }
            if (handItem instanceof PowderSnowBucketItem) {
                if (block.equals(Blocks.CAULDRON) && !player.shouldCancelInteraction()) return true;
                if (block.equals(Blocks.WATER_CAULDRON) && !player.shouldCancelInteraction()) return true;
                if (block.equals(Blocks.LAVA_CAULDRON) && !player.shouldCancelInteraction()) return true;
                if (block.equals(Blocks.POWDER_SNOW_CAULDRON) && !player.shouldCancelInteraction()) return true;
                return false; // crosshair will be updated later because PowderSnowBucketItem is also a BlockItem
            }
            if (handItem instanceof BoneMealItem) {
                if (BoneMealItem.useOnFertilizable(handItemStack, MinecraftClient.getInstance().world, ((BlockHitResult) hitResult).getBlockPos())) {
                    return true;
                }
                if (BoneMealItem.useOnGround(handItemStack, MinecraftClient.getInstance().world, ((BlockHitResult) hitResult).getBlockPos(), null)) {
                    return true;
                }
            }
            if (handItem instanceof CompassItem) {
                if (block == Blocks.LODESTONE) {
                    return true;
                }
            }
            if (handItem instanceof WritableBookItem || handItem instanceof WrittenBookItem) return true;
        } else if (hitResult.getType() == HitResult.Type.MISS && DynamicCrosshair.config.dynamicCrosshairHoldingBlock() == BlockCrosshairPolicy.Always) {
            if (handItem == Items.WATER_BUCKET || handItem == Items.LAVA_BUCKET) {
                activeCrosshair = DynamicCrosshair.config.getCrosshairStyleHoldingBlock();
                return false;
            }
        }

        // Liquid interactions, ignores block targeting state and casts extra rays
        if (handItem instanceof GlassBottleItem) {
            BlockHitResult blockHitResult = IItemMixin.invokeRaycast(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player, RaycastContext.FluidHandling.ANY);
            if (MinecraftClient.getInstance().world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER))
                return true;
        }
        if (handItem instanceof BucketItem) {
            BlockHitResult blockHitResult = IItemMixin.invokeRaycast(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player, RaycastContext.FluidHandling.SOURCE_ONLY);
            if (!MinecraftClient.getInstance().world.getFluidState(blockHitResult.getBlockPos()).isEmpty())
                return true;
        }

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (block instanceof BlockWithEntity) {
                if (block instanceof AbstractSignBlock) {
                    if (!player.shouldCancelInteraction() && (handItem instanceof DyeItem || handItem.equals(Items.GLOW_INK_SAC) || handItem.equals(Items.INK_SAC))) {
                        BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(blockPos);
                        if (blockEntity instanceof SignBlockEntity) {
                            if (handItem.equals(Items.GLOW_INK_SAC) && !((SignBlockEntity) blockEntity).isGlowingText()) return true;
                            if (handItem.equals(Items.INK_SAC) && ((SignBlockEntity) blockEntity).isGlowingText()) return true;
                            if (handItem instanceof DyeItem && ((SignBlockEntity) blockEntity).getTextColor() != ((DyeItem) handItem).getColor()) return true;
                        }
                    }
                }
                else if (block instanceof LecternBlock) {
                    if (handItem.equals(Items.WRITTEN_BOOK)
                            || handItem.equals(Items.WRITABLE_BOOK)
                            || (!player.shouldCancelInteraction() && blockState.get(LecternBlock.HAS_BOOK)))
                        return true;
                }
                else if (block instanceof CampfireBlock && !player.shouldCancelInteraction()) {
                    BlockEntity blockEntity = MinecraftClient.getInstance().world.getBlockEntity(blockPos);
                    if (blockEntity instanceof CampfireBlockEntity && (((CampfireBlockEntity) blockEntity).getRecipeFor(handItemStack)).isPresent())
                        return true;
                }
            }
        }
        return false;
    }

    private static boolean isBlockInteractible(ClientPlayerEntity player, HitResult hitResult, ItemStack mainHandStack) {
        // interactable blocks if not sneaking
        boolean cancelInteraction = player.shouldCancelInteraction() && !(mainHandStack.isEmpty() && player.getOffHandStack().isEmpty());
        if (DynamicCrosshair.config.dynamicCrosshairOnBlock() != InteractableCrosshairPolicy.Disabled && hitResult.getType() == HitResult.Type.BLOCK && !cancelInteraction) {
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(((BlockHitResult) hitResult).getBlockPos());
            Block block = blockState.getBlock();
            if (block instanceof BlockWithEntity) {
                if (!(     block instanceof BeehiveBlock
                        || block instanceof AbstractSignBlock
                        || block instanceof LecternBlock
                        || block instanceof CampfireBlock
                        || block instanceof BannerBlock
                )) {
                    return true;
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
                    || (block instanceof ComposterBlock && ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(mainHandStack.getItem()))
            ) {
                return true;
            }
            // Special case: Flower pots behave oddly
            if (block instanceof FlowerPotBlock) {
                Item handItem = player.getMainHandStack().getItem();
                boolean potItemIsAir = ((FlowerPotBlock) block).getContent() == Blocks.AIR;
                boolean handItemIsPottable = handItem instanceof BlockItem && IFlowerPotBlockMixin.getCONTENT_TO_POTTED().containsKey(((BlockItem) handItem).getBlock());
                if (potItemIsAir && handItemIsPottable) {
                    modifierUse = ModifierUse.USE_ITEM;
                    return false;
                }
                if (!potItemIsAir && !handItemIsPottable) {
                    return true;
                }
            }
            // Special case: Cake gets eaten (modified), so "use" makes more sense to me
            if (block instanceof CakeBlock) {
                if (player.getHungerManager().isNotFull() && (!player.shouldCancelInteraction() || (player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty()))) {
                    modifierUse = ModifierUse.USE_ITEM;
                    return false;
                }
            }
            // Special case: Redstone ore: can be placed against, but still activates
            if (block instanceof RedstoneOreBlock) {
                if (!player.shouldCancelInteraction() || (player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty())) {
                    modifierUse = CrosshairHandler.ModifierUse.INTERACTABLE;
                    return false;
                }
            }

            if (block instanceof AbstractCandleBlock && blockState.get(AbstractCandleBlock.LIT)) {
                Item mainItem = mainHandStack.getItem();
                if (!(mainItem.equals(Items.FLINT_AND_STEEL)
                        || mainItem instanceof BlockItem
                        || mainItem instanceof SpawnEggItem
                        || mainItem instanceof FireChargeItem
                        || mainItem instanceof EnderEyeItem
                        || mainItem instanceof EnderPearlItem
                        || mainItem instanceof WritableBookItem
                        || mainItem instanceof WrittenBookItem
                        || mainItem instanceof PotionItem
                        || mainItem.getUseAction(mainHandStack) == UseAction.DRINK
                        || (mainItem.getUseAction(mainHandStack) == UseAction.EAT && player.getHungerManager().isNotFull()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ModifierUse checkEntityModifiers(ClientPlayerEntity player, ItemStack itemStack, HitResult hitResult) {
        Entity target = ((EntityHitResult) hitResult).getEntity();
        Item handItem = itemStack.getItem();
        if (target instanceof AnimalEntity) {
            if (((AnimalEntity) target).isBreedingItem(itemStack)) {
                return ModifierUse.USE_ITEM;
            }
        }
        if (target instanceof MobEntity && handItem == Items.LEAD) {
            if (((MobEntity) target).canBeLeashedBy(player)) {
                return ModifierUse.USE_ITEM;
            }
            return ModifierUse.NONE;
        }
        if (target instanceof Shearable && handItem == Items.SHEARS) {
            if (((Shearable) target).isShearable()) {
                return ModifierUse.USE_ITEM;
            }
            return ModifierUse.NONE;
        }
        if (target.getType() == EntityType.ARMOR_STAND) return ModifierUse.USE_ITEM;
        else if (target.getType() == EntityType.AXOLOTL
                || target instanceof FishEntity) {
            if (handItem == Items.WATER_BUCKET) {
                return ModifierUse.USE_ITEM;
            }
            return ModifierUse.NONE;
        } else if (target.getType() == EntityType.BOAT
                || target.getType() == EntityType.MINECART
                || (target.getType() == EntityType.FURNACE_MINECART && IFurnaceMinecartEntityMixin.getACCEPTABLE_FUEL().test(itemStack))
                || target.getType() == EntityType.CHEST_MINECART
                || target.getType() == EntityType.HOPPER_MINECART) {
            return ModifierUse.INTERACTABLE;
        } else if (target.getType() == EntityType.CAT
                || target.getType() == EntityType.WOLF) {
            TameableEntity pet = (TameableEntity) target;
            if (pet.isTamed() && pet.isOwner(player)) {
                return ModifierUse.INTERACTABLE;
            }
            return ModifierUse.NONE;
        } else if (target.getType() == EntityType.COW
                || target.getType() == EntityType.GOAT) {
            if (handItem == Items.BUCKET && !((AnimalEntity)target).isBaby()) {
                return ModifierUse.USE_ITEM;
            }
            return ModifierUse.NONE;
        } else if (target.getType() == EntityType.CREEPER) {
            if (handItem == Items.FLINT_AND_STEEL) {
                return ModifierUse.USE_ITEM;
            }
            return ModifierUse.NONE;
        } else if (target.getType() == EntityType.DOLPHIN) {
            if (itemStack.isIn(ItemTags.FISHES)) {
                return ModifierUse.USE_ITEM;
            }
            return ModifierUse.NONE;
        } else if (target instanceof AbstractDonkeyEntity
                || target instanceof HorseEntity) {
            HorseBaseEntity horse = (HorseBaseEntity) target;
            if (horse.isBaby() || !horse.isTame()) {
                return ModifierUse.NONE;
            }
            if (horse.isTame() && player.shouldCancelInteraction()) {
                return ModifierUse.INTERACTABLE;
            }
            // horse armor, llama carpets
            if (horse.hasArmorSlot() && !horse.hasArmorInSlot() && horse.isHorseArmor(itemStack)) {
                return ModifierUse.USE_ITEM;
            }
            if (target instanceof AbstractDonkeyEntity) {
                if (!((AbstractDonkeyEntity) target).hasChest() && itemStack.isOf(Blocks.CHEST.asItem())) {
                    return ModifierUse.USE_ITEM;
                }
            }
            if (horse.canBeSaddled() && !horse.isSaddled() && handItem == Items.SADDLE) {
                return ModifierUse.USE_ITEM;
            }
            return ModifierUse.INTERACTABLE;
        } else if (target.getType() == EntityType.IRON_GOLEM) {
            if (handItem == Items.IRON_INGOT && (((LivingEntity) target).getHealth() < ((LivingEntity) target).getMaxHealth())) {
                return ModifierUse.USE_ITEM;
            }
            return ModifierUse.NONE;
        } else if (target instanceof ItemFrameEntity) {
            if (((ItemFrameEntity) target).getHeldItemStack().isEmpty()) {
                if (itemStack.isEmpty()) {
                    return ModifierUse.NONE;
                }
                return ModifierUse.USE_ITEM;
            }
            return ModifierUse.INTERACTABLE;
        } else if (target.getType() == EntityType.LEASH_KNOT) {
            return ModifierUse.USE_ITEM;
        } else if (target.getType() == EntityType.PANDA) {
            if (((PandaEntity) target).isLyingOnBack()) {
                return ModifierUse.INTERACTABLE;
            }
            return ModifierUse.NONE;
        } else if (target instanceof ParrotEntity parrot) {
            if (!parrot.isTamed() && IParrotEntityMixin.getTAMING_INGREDIENTS().contains(handItem)) {
                return ModifierUse.USE_ITEM;
            }
            if (handItem == Items.COOKIE) {
                // :'(
                return ModifierUse.USE_ITEM;
            }
            if (!parrot.isInAir() && parrot.isTamed() && parrot.isOwner(player)) {
                return ModifierUse.INTERACTABLE;
            }
        } else if (target instanceof MerchantEntity merchant) {
            if (!merchant.hasCustomer() && !merchant.isSleeping() && !merchant.getOffers().isEmpty()) {
                return ModifierUse.INTERACTABLE;
            }
            return ModifierUse.NONE;
        } else if (target.getType() == EntityType.ZOMBIE_VILLAGER) {
            if (handItem == Items.GOLDEN_APPLE && ((LivingEntity) target).hasStatusEffect(StatusEffects.WEAKNESS)) {
                return ModifierUse.INTERACTABLE;
            }
            return ModifierUse.NONE;
        }
        return ModifierUse.NONE;
    }

    // TODO
    // silk touch awareness
    private static boolean checkShowCrosshair() {
        activeCrosshair = DynamicCrosshair.config.getCrosshairStyleRegular();
        modifierUse = CrosshairHandler.ModifierUse.NONE;
        modifierHit = ModifierHit.NONE;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;

        // Hide crosshair when rendering any screen
        // This makes it not show up when using a transparent GUI resource pack
        if (DynamicCrosshair.config.isHideWithScreen() && MinecraftClient.getInstance().currentScreen != null) return false;

        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        if (hitResult == null) return false; // Failsafe: no target when not in world
        boolean useEntityCrosshair = false;
        if (DynamicCrosshair.config.dynamicCrosshairOnEntity() && hitResult.getType() == HitResult.Type.ENTITY) {
            activeCrosshair = DynamicCrosshair.config.getCrosshairStyleOnEntity();
            useEntityCrosshair = true;
        }

        if (DynamicCrosshair.config.dynamicCrosshairOnBlock() == InteractableCrosshairPolicy.IfTargeting && hitResult.getType() == HitResult.Type.BLOCK) {
            activeCrosshair = DynamicCrosshair.config.getCrosshairStyleOnBlock();
        }

        ItemStack mainHandStack = player.getMainHandStack();
        checkBreakable(player, mainHandStack.getItem(), hitResult);
        if (isBlockInteractible(player, hitResult, mainHandStack)) {
            modifierUse = CrosshairHandler.ModifierUse.INTERACTABLE;
        } else if (checkHand(player, mainHandStack, hitResult)) return true;

        if (modifierUse == CrosshairHandler.ModifierUse.NONE) {
           if (checkHand(player, player.getOffHandStack(), hitResult)) return true;
        }

        if (DynamicCrosshair.config.dynamicCrosshairOnBlock() == InteractableCrosshairPolicy.IfTargeting && hitResult.getType() == HitResult.Type.BLOCK) {
            return true;
        }

        // Force modded items to have a crosshair. This has to be done because modded tools/weapons cannot be distinguished
        // from regular items and thus will hide the crosshair.
        // Hopefully we can do this better in the future.
        if (DynamicCrosshair.config.isDynamicCrosshair() && activeCrosshair == DynamicCrosshair.config.getCrosshairStyleRegular()) {
            Item handItem = player.getMainHandStack().getItem();
            if (!Registry.ITEM.getId(handItem).getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
                return true;
            }
        }

        if (!useEntityCrosshair && !DynamicCrosshair.config.isDynamicCrosshair()) {
            return true;
        }
        return useEntityCrosshair;
    }

    public static boolean shouldShowCrosshair() {
        return shouldShowCrosshair;
    }
    public static List<CrosshairModifier> getActiveCrosshairModifiers() {
        List<CrosshairModifier> modifiers = new LinkedList<>();
        switch (modifierHit) {
            case CORRECT_TOOL -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierCorrectTool());
            case INCORRECT_TOOL -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierIncorrectTool());
        }
        switch (modifierUse) {
            case USE_ITEM -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierUsableItem());
            case INTERACTABLE -> modifiers.add(DynamicCrosshair.config.getCrosshairModifierInteractable());
        }
        return modifiers;
    }

    public static void tick() {
        shouldShowCrosshair = checkShowCrosshair();
    }
}
