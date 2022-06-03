package mod.crend.dynamiccrosshair.handler;

import mod.crend.dynamiccrosshair.component.Crosshair;
import mod.crend.dynamiccrosshair.mixin.IFurnaceMinecartEntityMixin;
import mod.crend.dynamiccrosshair.mixin.IParrotEntityMixin;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.tag.ItemTags;

public class VanillaEntityHandler implements IEntityHandler {
    @Override
    public Crosshair checkEntity(ClientPlayerEntity player, ItemStack itemStack, Entity entity) {
        Item handItem = itemStack.getItem();
        if (entity instanceof AnimalEntity) {
            if (((AnimalEntity) entity).isBreedingItem(itemStack)) {
                return Crosshair.USE_ITEM;
            }
        }
        if (entity instanceof MobEntity) {
            if (handItem instanceof SpawnEggItem) return Crosshair.USE_ITEM;

            if (handItem == Items.LEAD) {
                if (((MobEntity) entity).canBeLeashedBy(player)) {
                    return Crosshair.USE_ITEM;
                }
                return null;
            }
        }
        if (entity instanceof Shearable && handItem == Items.SHEARS) {
            if (((Shearable) entity).isShearable()) {
                return Crosshair.USE_ITEM;
            }
            return null;
        }
        if (entity.getType() == EntityType.ARMOR_STAND) return Crosshair.USE_ITEM;
        else if (entity.getType() == EntityType.AXOLOTL
                || entity instanceof FishEntity) {
            if (handItem == Items.WATER_BUCKET) {
                return Crosshair.USE_ITEM;
            }
            return null;
        } else if (entity.getType() == EntityType.BOAT
                || entity.getType() == EntityType.MINECART
                || (entity.getType() == EntityType.FURNACE_MINECART && IFurnaceMinecartEntityMixin.getACCEPTABLE_FUEL().test(itemStack))
                || entity.getType() == EntityType.CHEST_MINECART
                || entity.getType() == EntityType.HOPPER_MINECART) {
            return Crosshair.INTERACTABLE;
        } else if (entity.getType() == EntityType.CAT
                || entity.getType() == EntityType.WOLF) {
            TameableEntity pet = (TameableEntity) entity;
            if (pet.isTamed() && pet.isOwner(player)) {
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
            if (itemStack.isIn(ItemTags.FISHES)) {
                return Crosshair.USE_ITEM;
            }
            return null;
        } else if (entity instanceof AbstractDonkeyEntity
                || entity instanceof HorseEntity) {
            HorseBaseEntity horse = (HorseBaseEntity) entity;
            if (horse.isBaby() || !horse.isTame()) {
                return null;
            }
            if (horse.isTame() && player.shouldCancelInteraction()) {
                return Crosshair.INTERACTABLE;
            }
            // horse armor, llama carpets
            if (horse.hasArmorSlot() && !horse.hasArmorInSlot() && horse.isHorseArmor(itemStack)) {
                return Crosshair.USE_ITEM;
            }
            if (entity instanceof AbstractDonkeyEntity) {
                if (!((AbstractDonkeyEntity) entity).hasChest() && itemStack.isOf(Blocks.CHEST.asItem())) {
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
        } else if (entity instanceof ItemFrameEntity) {
            if (((ItemFrameEntity) entity).getHeldItemStack().isEmpty()) {
                if (itemStack.isEmpty()) {
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
            if (!parrot.isInAir() && parrot.isTamed() && parrot.isOwner(player)) {
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
