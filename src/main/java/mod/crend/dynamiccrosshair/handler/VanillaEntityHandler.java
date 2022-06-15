package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.IEntityHandler;
import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.mixin.IBucketItemMixin;
import mod.crend.dynamiccrosshair.mixin.IFurnaceMinecartEntityMixin;
import mod.crend.dynamiccrosshair.mixin.IParrotEntityMixin;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.tag.ItemTags;

public class VanillaEntityHandler implements IEntityHandler {
    @Override
    public Crosshair checkEntity(CrosshairContext context) {
        Item handItem = context.getItem();
        Entity entity = context.getEntity();
        if (entity instanceof AnimalEntity) {
            if (((AnimalEntity) entity).isBreedingItem(context.getItemStack())) {
                return Crosshair.USE_ITEM;
            }
        }
        if (entity instanceof MobEntity mobEntity) {
            if (handItem instanceof SpawnEggItem) return Crosshair.USE_ITEM;

            if (handItem == Items.LEAD) {
                if (mobEntity.canBeLeashedBy(context.player)) {
                    return Crosshair.USE_ITEM;
                }
                return null;
            }
        }
        if (entity instanceof Shearable shearableEntity && handItem == Items.SHEARS) {
            if (shearableEntity.isShearable()) {
                return Crosshair.USE_ITEM;
            }
            return null;
        }
        if (entity.getType() == EntityType.ARMOR_STAND) return Crosshair.USE_ITEM;
        else if (entity instanceof Bucketable) {
            if (handItem instanceof BucketItem bucketItem && ((IBucketItemMixin) bucketItem).getFluid() == Fluids.WATER) {
                return Crosshair.USE_ITEM;
            }
            return null;
        } else if (entity.getType() == EntityType.BOAT
                || entity.getType() == EntityType.MINECART
                || (entity.getType() == EntityType.FURNACE_MINECART && IFurnaceMinecartEntityMixin.getACCEPTABLE_FUEL().test(context.getItemStack()))
                || entity.getType() == EntityType.CHEST_MINECART
                || entity.getType() == EntityType.HOPPER_MINECART) {
            return Crosshair.INTERACTABLE;
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
                return Crosshair.USE_ITEM;
            }
            return null;
        } else if (entity.getType() == EntityType.CREEPER) {
            if (handItem == Items.FLINT_AND_STEEL) {
                return Crosshair.USE_ITEM;
            }
            return null;
        } else if (entity.getType() == EntityType.DOLPHIN) {
            if (context.getItemStack().isIn(ItemTags.FISHES)) {
                return Crosshair.USE_ITEM;
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
                    return Crosshair.USE_ITEM;
                }
            }
            if (horse.canBeSaddled() && !horse.isSaddled() && handItem == Items.SADDLE) {
                return Crosshair.USE_ITEM;
            }
            return Crosshair.INTERACTABLE;
        } else if (entity.getType() == EntityType.IRON_GOLEM) {
            if (handItem == Items.IRON_INGOT && (((LivingEntity) entity).getHealth() < ((LivingEntity) entity).getMaxHealth())) {
                return Crosshair.USE_ITEM;
            }
            return null;
        } else if (entity instanceof ItemFrameEntity itemFrame) {
            if (itemFrame.getHeldItemStack().isEmpty()) {
                if (context.getItemStack().isEmpty()) {
                    return null;
                }
                return Crosshair.USE_ITEM;
            }
            return Crosshair.INTERACTABLE;
        } else if (entity.getType() == EntityType.LEASH_KNOT) {
            return Crosshair.USE_ITEM;
        } else if (entity.getType() == EntityType.PANDA) {
            if (((PandaEntity) entity).isLyingOnBack()) {
                return Crosshair.INTERACTABLE;
            }
            return null;
        } else if (entity instanceof ParrotEntity parrot) {
            if (!parrot.isTamed() && IParrotEntityMixin.getTAMING_INGREDIENTS().contains(handItem)) {
                return Crosshair.USE_ITEM;
            }
            if (handItem == Items.COOKIE) {
                // :'(
                return Crosshair.USE_ITEM;
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
                return Crosshair.INTERACTABLE;
            }
            return null;
        }
        return null;
    }
}
