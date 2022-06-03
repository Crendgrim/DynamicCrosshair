package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.config.CrosshairPolicy;
import mod.crend.dynamiccrosshair.config.InteractableCrosshairPolicy;
import mod.crend.dynamiccrosshair.handler.VanillaBlockHandler;
import mod.crend.dynamiccrosshair.handler.VanillaEntityHandler;
import mod.crend.dynamiccrosshair.handler.VanillaItemHandler;
import mod.crend.dynamiccrosshair.handler.VanillaUsableItemHandler;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class CrosshairHandler {

    public static final Identifier crosshairTexture = new Identifier("dynamiccrosshair", "textures/gui/crosshairs.png");

    private static Crosshair activeCrosshair = new Crosshair();
    private static boolean shouldShowCrosshair = true;

    public static Crosshair getActiveCrosshair() {
        return activeCrosshair;
    }

    private static boolean policyMatches(CrosshairPolicy policy, boolean isTargeting) {
        return (policy == CrosshairPolicy.Always || (policy == CrosshairPolicy.IfTargeting && isTargeting));
    }
    private static boolean policyMatches(BlockCrosshairPolicy policy, boolean isTargeting) {
        return (policy == BlockCrosshairPolicy.Always || (policy != BlockCrosshairPolicy.Disabled && isTargeting));
    }

    // Return true if main hand item is usable
    private static Crosshair checkHandOnEntity(ClientPlayerEntity player, ItemStack handItemStack, Entity entity) {
        Crosshair crosshair = checkHandUsableItem(player, handItemStack, true);
        if (crosshair != null) {
            return crosshair;
        }
        return new VanillaEntityHandler().checkEntity(player, handItemStack, entity);
    }
    private static Crosshair checkHandsOnEntity(ClientPlayerEntity player, Entity entity) {
        Crosshair crosshair = checkHandOnEntity(player, player.getMainHandStack(), entity);
        if (crosshair == null) {
            crosshair = checkHandOnEntity(player, player.getOffHandStack(), entity);
        }
        return crosshair;
    }
    private static Crosshair checkHandOnBlock(ClientPlayerEntity player, ItemStack handItemStack, BlockPos blockPos, BlockState blockState) {
        Crosshair crosshair = checkHandCommon(player, handItemStack, true);
        crosshair = Crosshair.combine(crosshair, new VanillaUsableItemHandler().checkUsableItemOnBlock(player, handItemStack, blockPos, blockState));
        return crosshair;
    }
    private static Crosshair checkHandsOnBlock(ClientPlayerEntity player, BlockPos blockPos, BlockState blockState) {
        Crosshair crosshair = checkHandOnBlock(player, player.getMainHandStack(), blockPos, blockState);
        crosshair = Crosshair.combine(crosshair, checkHandOnBlock(player, player.getOffHandStack(), blockPos, blockState));
        return crosshair;
    }
    private static Crosshair checkHandOnMiss(ClientPlayerEntity player, ItemStack handItemStack) {
        Crosshair crosshair = checkHandCommon(player, handItemStack, false);
        crosshair = Crosshair.combine(crosshair, new VanillaUsableItemHandler().checkUsableItemOnMiss(player, handItemStack));
        return crosshair;
    }
    private static Crosshair checkHandsOnMiss(ClientPlayerEntity player) {
        Crosshair crosshair = checkHandOnMiss(player, player.getMainHandStack());
        crosshair = Crosshair.combine(crosshair, checkHandOnMiss(player, player.getOffHandStack()));
        return crosshair;
    }
    private static Crosshair checkHandUsableItem(ClientPlayerEntity player, ItemStack handItemStack, boolean isTargeting) {
        switch (DynamicCrosshair.config.dynamicCrosshairHoldingUsableItem()) {
            case Always -> {
                if (new VanillaUsableItemHandler().isUsableItem(handItemStack)) {
                    return Crosshair.USE_ITEM;
                }
            }
            case IfInteractable -> {
                return new VanillaUsableItemHandler().checkUsableItem(player, handItemStack);
            }
            case IfTargeting -> {
                if (isTargeting) {
                    return new VanillaUsableItemHandler().checkUsableItem(player, handItemStack);
                }
            }
        }
        return null;
    }
    private static Crosshair checkHandCommon(ClientPlayerEntity player, ItemStack handItemStack, boolean isTargeting) {
        Crosshair crosshair = checkHandUsableItem(player, handItemStack, isTargeting);
        if (crosshair != null) return crosshair;

        VanillaItemHandler itemHandler = new VanillaItemHandler();
        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingRangedWeapon(), isTargeting)) {
            crosshair = itemHandler.checkRangedWeapon(player, handItemStack);
            if (crosshair != null) return crosshair;
        }

        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingThrowable(), isTargeting)) {
            crosshair = itemHandler.checkThrowable(player, handItemStack);
            if (crosshair != null) return crosshair;
        }

        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingTool(), isTargeting)) {
            crosshair = itemHandler.checkTool(player, handItemStack);
            if (crosshair != null) return crosshair;
        }

        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingBlock(), isTargeting)) {
            crosshair = itemHandler.checkBlock(player, handItemStack);
            if (crosshair != null) return crosshair;
        }
        return null;
    }

    // Tools & Melee Weapons
    private static void checkBreakable(ClientPlayerEntity player, BlockPos blockPos, BlockState blockState) {
        if (DynamicCrosshair.config.dynamicCrosshairHoldingTool() == CrosshairPolicy.Disabled) return;

        activeCrosshair.updateFrom(new VanillaBlockHandler().checkBlockBreaking(player, player.getMainHandStack(), blockPos, blockState));
    }


    private static boolean isBlockInteractable(ClientPlayerEntity player, HitResult hitResult, ItemStack mainHandStack) {
        // interactable blocks if not sneaking
        boolean cancelInteraction = player.shouldCancelInteraction() && !(mainHandStack.isEmpty() && player.getOffHandStack().isEmpty());
        if (DynamicCrosshair.config.dynamicCrosshairOnBlock() != InteractableCrosshairPolicy.Disabled && hitResult.getType() == HitResult.Type.BLOCK && !cancelInteraction) {
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
            if (activeCrosshair.updateFrom(new VanillaBlockHandler().checkBlockInteractable(player, player.getMainHandStack(), blockPos, blockState))) {
                return true;
            }
        }
        return false;
    }


    private static void checkModifiers(ClientPlayerEntity player) {
        // TODO STUB
    }

    // TODO
    // silk touch awareness
    private static boolean checkShowCrosshair() {
        activeCrosshair = new Crosshair();

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return false;

        // Hide crosshair when rendering any screen
        // This makes it not show up when using a transparent GUI resource pack
        if (DynamicCrosshair.config.isHideWithScreen() && MinecraftClient.getInstance().currentScreen != null) return false;

        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        if (hitResult == null) return false; // Failsafe: no target when not in world

        if (!DynamicCrosshair.config.isDynamicCrosshairStyle()) {
            activeCrosshair.setStyle(Style.Regular);
            if (!DynamicCrosshair.config.isDynamicCrosshair()) {
                return true;
            }

            return switch (hitResult.getType()) {
                case ENTITY -> DynamicCrosshair.config.dynamicCrosshairOnEntity();
                case BLOCK -> switch (DynamicCrosshair.config.dynamicCrosshairOnBlock()) {
                    case IfTargeting -> true;
                    case IfInteractable -> isBlockInteractable(player, hitResult, player.getMainHandStack());
                    case Disabled -> false;
                };
                case MISS -> false;
            };
        }

        // Dynamic crosshair style is active
        switch (hitResult.getType()) {
            case ENTITY -> {
                if (DynamicCrosshair.config.dynamicCrosshairOnEntity()) {
                    activeCrosshair.setStyle(Style.OnEntity);
                }
                Entity entity = ((EntityHitResult) hitResult).getEntity();
                if (activeCrosshair.updateFrom(checkHandsOnEntity(player, entity))) {
                    return true;
                }
            }
            case BLOCK -> {
                boolean isInteractable = isBlockInteractable(player, hitResult, player.getMainHandStack());
                switch (DynamicCrosshair.config.dynamicCrosshairOnBlock()) {
                    case IfTargeting -> activeCrosshair.setStyle(Style.OnBlock);
                    case IfInteractable -> {
                        if (isInteractable) {
                            activeCrosshair.setStyle(Style.OnBlock);
                        }
                    }
                }
                BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
                BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
                checkBreakable(player, blockPos, blockState);
                if (isInteractable) {
                    activeCrosshair.setModifierUse(ModifierUse.INTERACTABLE);
                } else {
                    if (activeCrosshair.updateFrom(checkHandsOnBlock(player, blockPos, blockState))) {
                        return true;
                    }
                }
            }
            case MISS -> {
                if (activeCrosshair.updateFrom(checkHandsOnMiss(player))) {
                    return true;
                }
            }
        }
        checkModifiers(player);

        // Force modded items to have a crosshair. This has to be done because modded tools/weapons cannot be distinguished
        // from regular items and thus will hide the crosshair.
        // Hopefully we can do this better in the future.
        if (DynamicCrosshair.config.isDynamicCrosshair() && activeCrosshair.getStyle() == Style.Regular) {
            Item handItem = player.getMainHandStack().getItem();
            if (!Registry.ITEM.getId(handItem).getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
                return true;
            }
        }

        if (activeCrosshair.isChanged()) {
            return true;
        }
        return !DynamicCrosshair.config.isDynamicCrosshair();
    }

    public static boolean shouldShowCrosshair() {
        return shouldShowCrosshair;
    }

    public static void tick() {
        shouldShowCrosshair = checkShowCrosshair();
    }
}
