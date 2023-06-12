package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.PlatformUtils;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.mixin.ArmorStandEntityAccessor;
import mod.crend.dynamiccrosshair.mixin.BoatEntityAccessor;
import mod.crend.dynamiccrosshair.mixin.FurnaceMinecartEntityAccessor;
import mod.crend.dynamiccrosshair.mixin.ParrotEntityAccessor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class VanillaEntityHandler {

    public static boolean isEntityInteractable(Entity entity) {
        return (   entity instanceof ItemFrameEntity
                || entity instanceof BoatEntity
                || entity instanceof AbstractMinecartEntity
                || entity.getType() == EntityType.CAT
                || entity.getType() == EntityType.WOLF
                || entity instanceof ParrotEntity
                || entity instanceof AbstractHorseEntity
                || entity instanceof MerchantEntity
                || entity instanceof AllayEntity
        );
    }

    public static Crosshair checkEntity(CrosshairContext context) {
        Item handItem = context.getItem();
        Entity entity = context.getEntity();
        if (entity instanceof AnimalEntity) {
            if (((AnimalEntity) entity).isBreedingItem(context.getItemStack())) {
                return Crosshair.USABLE;
            }
        }
        if (entity instanceof MobEntity mobEntity) {
            if (handItem instanceof SpawnEggItem) return Crosshair.USABLE;

            if (handItem == Items.LEAD) {
                if (mobEntity.canBeLeashedBy(context.player)) {
                    return Crosshair.USABLE;
                }
                return null;
            }
        }
        if (entity instanceof Shearable shearableEntity && handItem == Items.SHEARS) {
            if (shearableEntity.isShearable()) {
                return Crosshair.USABLE;
            }
            return null;
        }
        if (entity == null) {
            // Not entirely sure why this happens, but let's make sure it doesn't crash
            return null;
        }
        if (entity instanceof ArmorStandEntity armorStand) {
            if (context.isMainHand() || context.getItemStack(Hand.MAIN_HAND).isOf(Items.NAME_TAG)) {
                ItemStack itemStack = context.getItemStack();
                if (itemStack.isEmpty()) {
                    Vec3d hitPos = context.hitResult.getPos().subtract(armorStand.getPos());
                    EquipmentSlot slot = ((ArmorStandEntityAccessor) armorStand).invokeGetSlotFromPosition(hitPos);
                    if (armorStand.hasStackEquipped(slot)) {
                        return Crosshair.INTERACTABLE;
                    }
                } else if (itemStack.isOf(Items.NAME_TAG)) {
                    if (itemStack.hasCustomName()) {
                        // rename armor stand
                        return Crosshair.USABLE;
                    }
                } else {
                    EquipmentSlot slot = MobEntity.getPreferredEquipmentSlot(itemStack);
                    if (!((ArmorStandEntityAccessor) armorStand).invokeIsSlotDisabled(slot) && (slot.getType() != EquipmentSlot.Type.HAND || armorStand.shouldShowArms())) {
                        if (!armorStand.hasStackEquipped(slot) || itemStack.getCount() == 1) {
                            return Crosshair.USABLE;
                        }
                    }
                }
            }
        }
        else if (entity instanceof Bucketable) {
            //noinspection ConstantValue
            if (handItem instanceof BucketItem bucketItem && PlatformUtils.getFluidFromBucket(bucketItem) == Fluids.WATER) {
                return Crosshair.USABLE;
            }
            return null;
        } else if (entity instanceof BoatEntity boatEntity) {
            if (entity instanceof VehicleInventory) {
                return Crosshair.INTERACTABLE;
            }
            if (!context.player.shouldCancelInteraction() && ((BoatEntityAccessor) boatEntity).invokeCanAddPassenger(context.player)) {
                return Crosshair.INTERACTABLE;
            }
        } else if (entity instanceof AbstractMinecartEntity minecartEntity) {
            //noinspection ConstantValue
            if ((entity.getType() == EntityType.MINECART && !minecartEntity.hasPassengers())
                    || entity instanceof StorageMinecartEntity
                    || (entity.getType() == EntityType.FURNACE_MINECART && FurnaceMinecartEntityAccessor.getACCEPTABLE_FUEL().test(context.getItemStack()))
            ) {
                return Crosshair.INTERACTABLE;
            }
        } else if (entity.getType() == EntityType.CAT
                || entity.getType() == EntityType.WOLF) {
            TameableEntity pet = (TameableEntity) entity;
            if (pet.isTamed() && pet.isOwner(context.player)) {
                return Crosshair.INTERACTABLE;
            }
            return null;
        } else if (entity.getType() == EntityType.COW
                || entity.getType() == EntityType.GOAT) {
            if (handItem == Items.BUCKET && !((AnimalEntity)entity).isBaby()) {
                return Crosshair.USABLE;
            }
            return null;
        } else if (entity.getType() == EntityType.CREEPER) {
            if (handItem == Items.FLINT_AND_STEEL) {
                return Crosshair.USABLE;
            }
            return null;
        } else if (entity.getType() == EntityType.DOLPHIN) {
            if (context.getItemStack().isIn(ItemTags.FISHES)) {
                return Crosshair.USABLE;
            }
            return null;
        } else if (entity instanceof AbstractDonkeyEntity
                || entity instanceof HorseEntity) {
            AbstractHorseEntity horse = (AbstractHorseEntity) entity;
            if (horse.isBaby() || !horse.isTame()) {
                return null;
            }
            if (horse.isTame() && context.player.shouldCancelInteraction()) {
                return Crosshair.INTERACTABLE;
            }
            if (entity instanceof AbstractDonkeyEntity donkey) {
                if (!donkey.hasChest() && context.getItemStack().isOf(Blocks.CHEST.asItem())) {
                    return Crosshair.USABLE;
                }
            }
            if (horse.canBeSaddled() && !horse.isSaddled() && handItem == Items.SADDLE) {
                return Crosshair.USABLE;
            }
            return Crosshair.INTERACTABLE;
        } else if (entity.getType() == EntityType.IRON_GOLEM) {
            if (handItem == Items.IRON_INGOT && (((LivingEntity) entity).getHealth() < ((LivingEntity) entity).getMaxHealth())) {
                return Crosshair.USABLE;
            }
            return null;
        } else if (entity instanceof ItemFrameEntity itemFrame) {
            if (itemFrame.getHeldItemStack().isEmpty()) {
                if (context.getItemStack().isEmpty()) {
                    return null;
                }
                return Crosshair.USABLE;
            }
            return Crosshair.INTERACTABLE;
        } else if (entity.getType() == EntityType.LEASH_KNOT) {
            return Crosshair.USABLE;
        } else if (entity.getType() == EntityType.PANDA) {
            if (((PandaEntity) entity).isLyingOnBack()) {
                return Crosshair.INTERACTABLE;
            }
            return null;
        } else if (entity instanceof ParrotEntity parrot) {
            //noinspection ConstantValue
            if (!parrot.isTamed() && ParrotEntityAccessor.getTAMING_INGREDIENTS().contains(handItem)) {
                return Crosshair.USABLE;
            }
            if (handItem == Items.COOKIE) {
                // :'(
                return Crosshair.USABLE;
            }
            if (!parrot.isInAir() && parrot.isTamed() && parrot.isOwner(context.player)) {
                return Crosshair.INTERACTABLE;
            }
        } else if (entity instanceof MerchantEntity merchant) {
            if (!merchant.hasCustomer() && !merchant.isSleeping() && !merchant.getOffers().isEmpty()) {
                return Crosshair.INTERACTABLE;
            }
            return null;
        } else if (entity.getType() == EntityType.ZOMBIE_VILLAGER) {
            if (handItem == Items.GOLDEN_APPLE && ((LivingEntity) entity).hasStatusEffect(StatusEffects.WEAKNESS)) {
                return Crosshair.USABLE;
            }
            return null;
        } else if (entity instanceof AllayEntity allay) {
            ItemStack allayItemStack = allay.getStackInHand(Hand.MAIN_HAND);
            ItemStack handItemStack = context.getItemStack();
            if (allayItemStack.isEmpty() && !handItemStack.isEmpty()) {
                return Crosshair.USABLE;
            }
            if (!allayItemStack.isEmpty() && context.isMainHand() && handItemStack.isEmpty()) {
                return Crosshair.INTERACTABLE;
            }
        }
        return null;
    }
}
