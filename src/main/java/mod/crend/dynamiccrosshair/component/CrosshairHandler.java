package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.config.CrosshairPolicy;
import mod.crend.dynamiccrosshair.config.InteractableCrosshairPolicy;
import mod.crend.dynamiccrosshair.config.RangedCrosshairPolicy;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

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

    private static String getNamespace(ItemStack itemStack) {
        return Registry.ITEM.getId(itemStack.getItem()).getNamespace();
    }
    private static String getNamespace(BlockState blockState) {
        return Registry.BLOCK.getId(blockState.getBlock()).getNamespace();
    }
    private static String getNamespace(Entity entity) {
        return Registry.ENTITY_TYPE.getId(entity.getType()).getNamespace();
    }

    // Return true if main hand item is usable
    private static Crosshair checkHandOnEntity(ClientPlayerEntity player, ItemStack handItemStack, Entity entity) {
        Crosshair crosshair = checkHandUsableItem(player, handItemStack, true);
        Set<String> namespaces = new HashSet<>();
        namespaces.add(getNamespace(handItemStack));
        namespaces.add(getNamespace(entity));
        for (String ns : namespaces) {
            if (DynamicCrosshair.apis.containsKey(ns)) {
                DynamicCrosshairApi api = DynamicCrosshair.apis.get(ns);
                crosshair = Crosshair.combine(crosshair, api.getEntityHandler().checkEntity(player, handItemStack, entity));
            }
        }
        return crosshair;
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
        Set<String> namespaces = new HashSet<>();
        namespaces.add(getNamespace(handItemStack));
        namespaces.add(getNamespace(blockState));
        for (String ns : namespaces) {
            if (DynamicCrosshair.apis.containsKey(ns)) {
                DynamicCrosshairApi api = DynamicCrosshair.apis.get(ns);
                crosshair = Crosshair.combine(crosshair, api.getUsableItemHandler().checkUsableItemOnBlock(player, handItemStack, blockPos, blockState));
            }
        }
        return crosshair;
    }
    private static Crosshair checkHandsOnBlock(ClientPlayerEntity player, BlockPos blockPos, BlockState blockState) {
        Crosshair crosshair = checkHandOnBlock(player, player.getMainHandStack(), blockPos, blockState);
        crosshair = Crosshair.combine(crosshair, checkHandOnBlock(player, player.getOffHandStack(), blockPos, blockState));
        return crosshair;
    }
    private static Crosshair checkHandOnMiss(ClientPlayerEntity player, ItemStack handItemStack) {
        Crosshair crosshair = checkHandCommon(player, handItemStack, false);
        String ns = getNamespace(handItemStack);
        if (DynamicCrosshair.apis.containsKey(ns)) {
            DynamicCrosshairApi api = DynamicCrosshair.apis.get(ns);
            crosshair = Crosshair.combine(crosshair, api.getUsableItemHandler().checkUsableItemOnMiss(player, handItemStack));
        }
        return crosshair;
    }
    private static Crosshair checkHandsOnMiss(ClientPlayerEntity player) {
        Crosshair crosshair = checkHandOnMiss(player, player.getMainHandStack());
        crosshair = Crosshair.combine(crosshair, checkHandOnMiss(player, player.getOffHandStack()));
        return crosshair;
    }
    private static Crosshair checkHandUsableItem(ClientPlayerEntity player, ItemStack handItemStack, boolean isTargeting) {
        Set<String> namespaces = new HashSet<>();
        String nsItem = getNamespace(handItemStack);
        namespaces.add(nsItem);
        // Hack: use default namespace for usable items so food and drink items get automatically picked up
        namespaces.add(Identifier.DEFAULT_NAMESPACE);
        for (String ns : namespaces) {
            if (DynamicCrosshair.apis.containsKey(ns)) {
                DynamicCrosshairApi api = DynamicCrosshair.apis.get(ns);
                switch (DynamicCrosshair.config.dynamicCrosshairHoldingUsableItem()) {
                    case Always -> {
                        if (api.getUsableItemHandler().isUsableItem(handItemStack)) {
                            return Crosshair.USE_ITEM;
                        }
                    }
                    case IfInteractable -> {
                        Crosshair crosshair = api.getUsableItemHandler().checkUsableItem(player, handItemStack);
                        if (crosshair != null) return crosshair;
                    }
                    case IfTargeting -> {
                        if (isTargeting) {
                            Crosshair crosshair = api.getUsableItemHandler().checkUsableItem(player, handItemStack);
                            if (crosshair != null) return crosshair;
                        }
                    }
                }
            }
        }
        if (!DynamicCrosshair.apis.containsKey(nsItem)) {
            // Force modded items to have a crosshair. This has to be done because modded tools/weapons cannot be distinguished
            // from regular items and thus will hide the crosshair.
            // These only take effect if mod compatibility isn't set up.
            return Crosshair.REGULAR;
        }
        return null;
    }
    private static Crosshair checkHandCommon(ClientPlayerEntity player, ItemStack handItemStack, boolean isTargeting) {
        Crosshair crosshair = checkHandUsableItem(player, handItemStack, isTargeting);
        if (crosshair != null) return crosshair;

        String ns = getNamespace(handItemStack);
        if (!DynamicCrosshair.apis.containsKey(ns)) {
            return null;
        }
        DynamicCrosshairApi api = DynamicCrosshair.apis.get(ns);

        if (DynamicCrosshair.config.dynamicCrosshairHoldingRangedWeapon() != RangedCrosshairPolicy.Disabled) {
            crosshair = api.getRangedWeaponHandler().checkRangedWeapon(player, handItemStack);
            if (crosshair != null) return crosshair;
        }

        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingThrowable(), isTargeting)) {
            crosshair = api.getThrowableItemHandler().checkThrowable(player, handItemStack);
            if (crosshair != null) return crosshair;
        }

        if (DynamicCrosshair.config.dynamicCrosshairHoldingMeleeWeapon()) {
            crosshair = api.getMeleeWeaponHandler().checkMeleeWeapon(player, handItemStack, isTargeting && DynamicCrosshair.config.dynamicCrosshairHoldingTool() != CrosshairPolicy.Disabled);
            if (crosshair != null) return crosshair;
        }
        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingTool(), isTargeting)) {
            crosshair = api.getToolItemHandler().checkTool(player, handItemStack);
            if (crosshair != null) return crosshair;
        }

        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingBlock(), isTargeting)) {
            crosshair = api.getBlockItemHandler().checkBlock(player, handItemStack);
            if (crosshair != null) return crosshair;
        }

        return null;
    }

    // Tools & Melee Weapons
    private static void checkBreakable(ClientPlayerEntity player, BlockPos blockPos, BlockState blockState) {
        if (DynamicCrosshair.config.dynamicCrosshairHoldingTool() == CrosshairPolicy.Disabled) return;

        String ns = getNamespace(blockState);
        if (DynamicCrosshair.apis.containsKey(ns)) {
            DynamicCrosshairApi api = DynamicCrosshair.apis.get(ns);
            activeCrosshair.updateFrom(api.getBlockBreakHandler().checkBlockBreaking(player, player.getMainHandStack(), blockPos, blockState));
        }
    }


    private static boolean isBlockInteractable(ClientPlayerEntity player, HitResult hitResult, ItemStack mainHandStack) {
        // interactable blocks if not sneaking
        boolean cancelInteraction = player.shouldCancelInteraction() && !(mainHandStack.isEmpty() && player.getOffHandStack().isEmpty());
        if (DynamicCrosshair.config.dynamicCrosshairOnBlock() != InteractableCrosshairPolicy.Disabled && hitResult.getType() == HitResult.Type.BLOCK && !cancelInteraction) {
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            BlockState blockState = MinecraftClient.getInstance().world.getBlockState(blockPos);
            Set<String> namespaces = new HashSet<>();
            namespaces.add(getNamespace(blockState));
            namespaces.add(getNamespace(mainHandStack));
            for (String ns : namespaces) {
                if (DynamicCrosshair.apis.containsKey(ns)) {
                    DynamicCrosshairApi api = DynamicCrosshair.apis.get(ns);
                    if (activeCrosshair.updateFrom(api.getBlockInteractHandler().checkBlockInteractable(player, mainHandStack, blockPos, blockState))) {
                        return true;
                    }
                }
            }
        }
        return false;
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
                if (activeCrosshair.updateFrom(checkHandsOnBlock(player, blockPos, blockState))) {
                    return true;
                }
            }
            case MISS -> {
                if (activeCrosshair.updateFrom(checkHandsOnMiss(player))) {
                    return true;
                }
            }
        }

        if (activeCrosshair.isChanged()) {
            return true;
        }
        if (DynamicCrosshair.config.isDynamicCrosshair()) {
            return false;
        }
        // Dynamic crosshair disabled, no other crosshair computed: make sure to show a crosshair
        activeCrosshair.setStyle(Style.Regular);
        return true;
    }

    public static boolean shouldShowCrosshair() {
        return shouldShowCrosshair;
    }

    public static void tick() {
        shouldShowCrosshair = checkShowCrosshair();
    }
}
