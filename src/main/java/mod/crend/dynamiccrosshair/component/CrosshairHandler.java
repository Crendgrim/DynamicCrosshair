package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.config.CrosshairPolicy;
import mod.crend.dynamiccrosshair.config.InteractableCrosshairPolicy;
import mod.crend.dynamiccrosshair.config.RangedCrosshairPolicy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;

public class CrosshairHandler {

    public static final Identifier crosshairTexture = new Identifier(DynamicCrosshair.MOD_ID, "textures/gui/crosshairs.png");

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
    private static Crosshair checkHandOnEntity(CrosshairContext context) {
        Crosshair crosshair = null;
        for (DynamicCrosshairApi api : context.apis()) {
            crosshair = checkHandUsableItem(api, context);
            if (crosshair != null) break;
        }
        for (DynamicCrosshairApi api : context.apis()) {
            crosshair = Crosshair.combine(crosshair, api.checkEntity(context));
        }
        return crosshair;
    }
    private static Crosshair checkHandsOnEntity(CrosshairContext mainHandContext, CrosshairContext offHandContext) {
        Crosshair crosshair = checkHandOnEntity(mainHandContext);
        if (crosshair == null) {
            crosshair = checkHandOnEntity(offHandContext);
        }
        return crosshair;
    }
    private static Crosshair checkHandOnBlockOrMiss(CrosshairContext context) {
        Crosshair crosshair = null;
        for (DynamicCrosshairApi api : context.apis()) {
            crosshair = checkHandCommon(api, context);
            if (crosshair != null) break;
        }
        return crosshair;
    }
    private static Crosshair checkHandsOnBlockOrMiss(CrosshairContext mainHandContext, CrosshairContext offHandContext) {
        Crosshair crosshair = checkHandOnBlockOrMiss(mainHandContext);
        crosshair = Crosshair.combine(crosshair, checkHandOnBlockOrMiss(offHandContext));
        return crosshair;
    }

    private static Crosshair checkHandUsableItem(DynamicCrosshairApi api, CrosshairContext context) {
        switch (DynamicCrosshair.config.dynamicCrosshairHoldingUsableItem()) {
            case Always -> {
                if (api.isAlwaysUsableItem(context.getItemStack()) || api.isUsableItem(context.getItemStack())) {
                    return Crosshair.USE_ITEM;
                }
            }
            case IfInteractable -> {
                if (api.isAlwaysUsableItem(context.getItemStack())) {
                    return Crosshair.USE_ITEM;
                }
                Crosshair crosshair = api.checkUsableItem(context);
                if (crosshair != null) return crosshair;
            }
            case IfTargeting -> {
                if (context.isTargeting()) {
                    if (api.isAlwaysUsableItem(context.getItemStack())) {
                        return Crosshair.USE_ITEM;
                    }
                    Crosshair crosshair = api.checkUsableItem(context);
                    if (crosshair != null) return crosshair;
                }
            }
        }
        return null;
    }
    private static Crosshair checkHandCommonCrosshair(DynamicCrosshairApi api, CrosshairContext context) {
        Crosshair crosshair = checkHandUsableItem(api, context);
        if (crosshair != null) return crosshair;

        if (DynamicCrosshair.config.dynamicCrosshairHoldingRangedWeapon() != RangedCrosshairPolicy.Disabled) {
            crosshair = api.checkRangedWeapon(context);
            if (crosshair != null) return crosshair;
        }

        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingThrowable(), context.isTargeting())) {
            crosshair = api.checkThrowable(context);
            if (crosshair != null) return crosshair;
        }

        if (DynamicCrosshair.config.dynamicCrosshairHoldingMeleeWeapon()) {
            crosshair = api.checkMeleeWeapon(context);
            if (crosshair != null) return crosshair;
        }
        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingTool(), context.isTargeting())) {
            crosshair = api.checkTool(context);
            if (crosshair != null) return crosshair;
        }

        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingBlock(), context.isTargeting())) {
            crosshair = api.checkBlockItem(context);
            if (crosshair != null) return crosshair;
        }

        return null;
    }
    private static Crosshair checkHandCommon(DynamicCrosshairApi api, CrosshairContext context) {
        Crosshair crosshair = checkHandCommonCrosshair(api, context);

        if (DynamicCrosshair.config.dynamicCrosshairHoldingShield()) {
            crosshair = Crosshair.combine(crosshair, api.checkShield(context));
        }

        return crosshair;
    }

    // Tools & Melee Weapons
    private static void checkBreakable(CrosshairContext context) {
        if (DynamicCrosshair.config.dynamicCrosshairHoldingTool() == CrosshairPolicy.Disabled) return;

        for (DynamicCrosshairApi api : context.apis()) {
            activeCrosshair.updateFrom(api.checkBlockBreaking(context));
        }
    }


    private static boolean isBlockInteractable(CrosshairContext context) {
        // interactable blocks if not sneaking
        if (DynamicCrosshair.config.dynamicCrosshairOnBlock() != InteractableCrosshairPolicy.Disabled && context.isWithBlock() && context.shouldInteract()) {
            for (DynamicCrosshairApi api : context.apis()) {
                if (activeCrosshair.updateFrom(api.checkBlockInteractable(context))) {
                    return true;
                }
            }
        }
        return false;
    }

    static State state = null;

    // TODO
    // silk touch awareness
    private static boolean checkShowCrosshair() {

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            state = null;
            return false;
        }

        HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        if (hitResult == null) {
            state = null;
            return false; // Failsafe: no target when not in world
        }

        // Hide crosshair when rendering any screen
        // This makes it not show up when using a transparent GUI resource pack
        if (DynamicCrosshair.config.isHideWithScreen() && MinecraftClient.getInstance().currentScreen != null) {
            state = null;
            return false;
        }

        if (state == null) {
            state = new State();
        }

        if (!state.changed(hitResult, player)) {
            return shouldShowCrosshair;
        }

        // State changed, build new crosshair
        activeCrosshair = new Crosshair();

        if (!DynamicCrosshair.config.isDynamicCrosshairStyle()) {
            activeCrosshair.setStyle(Style.Regular);
            if (!DynamicCrosshair.config.isDynamicCrosshair()) {
                return true;
            }

            return switch (hitResult.getType()) {
                case ENTITY -> DynamicCrosshair.config.dynamicCrosshairOnEntity();
                case BLOCK -> switch (DynamicCrosshair.config.dynamicCrosshairOnBlock()) {
                    case IfTargeting -> true;
                    case IfInteractable -> isBlockInteractable(state.mainHandContext);
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
                if (activeCrosshair.updateFrom(checkHandsOnEntity(state.mainHandContext, state.offHandContext))) {
                    return true;
                }
            }
            case BLOCK -> {
                boolean isInteractable = isBlockInteractable(state.mainHandContext);
                switch (DynamicCrosshair.config.dynamicCrosshairOnBlock()) {
                    case IfTargeting -> activeCrosshair.setStyle(Style.OnBlock);
                    case IfInteractable -> {
                        if (isInteractable) {
                            activeCrosshair.setStyle(Style.OnBlock);
                        }
                    }
                }
                checkBreakable(state.mainHandContext);
                if (activeCrosshair.updateFrom(checkHandsOnBlockOrMiss(state.mainHandContext, state.offHandContext))) {
                    return true;
                }
            }
            case MISS -> {
                if (activeCrosshair.updateFrom(checkHandsOnBlockOrMiss(state.mainHandContext, state.offHandContext))) {
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
