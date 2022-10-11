package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.config.BlockCrosshairPolicy;
import mod.crend.dynamiccrosshair.config.CrosshairPolicy;
import mod.crend.dynamiccrosshair.config.InteractableCrosshairPolicy;
import mod.crend.dynamiccrosshair.config.RangedCrosshairPolicy;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrosshairHandler {

    public static final Identifier crosshairTexture = new Identifier(DynamicCrosshair.MOD_ID, "textures/gui/crosshairs.png");
    public static final Logger LOGGER = LoggerFactory.getLogger(DynamicCrosshair.MOD_ID);

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
    private static Crosshair checkHandsOnEntity(CrosshairContext context) {
        Crosshair crosshair = checkHandOnEntity(context);
        if (crosshair == null) {
            context.setHand(Hand.OFF_HAND);
            crosshair = checkHandOnEntity(context);
            context.setHand(Hand.MAIN_HAND);
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
    private static Crosshair checkHandsOnBlockOrMiss(CrosshairContext context) {
        Crosshair crosshair = checkHandOnBlockOrMiss(context);
        context.setHand(Hand.OFF_HAND);
        crosshair = Crosshair.combine(crosshair, checkHandOnBlockOrMiss(context));
        context.setHand(Hand.MAIN_HAND);
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
                if (context.isCoolingDown()) return null;
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

        if (policyMatches(DynamicCrosshair.config.dynamicCrosshairHoldingThrowable(), context.isTargeting()) && !context.isCoolingDown()) {
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

    private static TriState buildCrosshair(HitResult hitResult, ClientPlayerEntity player) {

        if (!state.changed(hitResult, player)) {
            return TriState.of(shouldShowCrosshair);
        }

        // State changed, build new crosshair
        activeCrosshair = new Crosshair();

        try {

            if (!DynamicCrosshair.config.isDynamicCrosshairStyle()) {
                activeCrosshair.setVariant(CrosshairVariant.Regular);
                if (!DynamicCrosshair.config.isDynamicCrosshair()) {
                    return TriState.TRUE;
                }

                return switch (hitResult.getType()) {
                    case ENTITY -> TriState.of(DynamicCrosshair.config.dynamicCrosshairOnEntity());
                    case BLOCK -> switch (DynamicCrosshair.config.dynamicCrosshairOnBlock()) {
                        case IfTargeting -> TriState.TRUE;
                        case IfInteractable -> TriState.of(isBlockInteractable(state.context));
                        case Disabled -> TriState.FALSE;
                    };
                    case MISS -> TriState.FALSE;
                };
            }

            // Dynamic crosshair style is active
            switch (hitResult.getType()) {
                case ENTITY -> {
                    if (DynamicCrosshair.config.dynamicCrosshairOnEntity()) {
                        activeCrosshair.setVariant(CrosshairVariant.OnEntity);
                    }
                    if (activeCrosshair.updateFrom(checkHandsOnEntity(state.context))) {
                        return TriState.TRUE;
                    }
                }
                case BLOCK -> {
                    boolean isInteractable = isBlockInteractable(state.context);
                    switch (DynamicCrosshair.config.dynamicCrosshairOnBlock()) {
                        case IfTargeting -> activeCrosshair.setVariant(CrosshairVariant.OnBlock);
                        case IfInteractable -> {
                            if (isInteractable) {
                                activeCrosshair.setVariant(CrosshairVariant.OnBlock);
                            }
                        }
                    }
                    state.context.withBlock(CrosshairHandler::checkBreakable);
                    if (activeCrosshair.updateFrom(state.context.withBlock(CrosshairHandler::checkHandsOnBlockOrMiss))) {
                        return TriState.TRUE;
                    }
                }
                case MISS -> {
                    if (activeCrosshair.updateFrom(checkHandsOnBlockOrMiss(state.context))) {
                        return TriState.TRUE;
                    }
                }
            }
        } catch (CrosshairContextChange crosshairContextChange) {
            // For some reason, we are being asked to re-evaluate the context.
            return buildCrosshair(crosshairContextChange.newHitResult, player);
        } catch (InvalidContextState invalidContextState) {
            LOGGER.error("Encountered invalid context state: ", invalidContextState);
        }
        return TriState.DEFAULT;
    }

    // TODO
    // silk touch awareness
    private static boolean checkShowCrosshair() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || (state != null && state.context.player != player)) {
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

        TriState result = buildCrosshair(hitResult, player);
        if (result != TriState.DEFAULT) {
            return result.get();
        }

        if (activeCrosshair.isChanged()) {
            return true;
        }
        if (DynamicCrosshair.config.isDynamicCrosshair()) {
            return false;
        }
        // Dynamic crosshair disabled, no other crosshair computed: make sure to show a crosshair
        activeCrosshair.setVariant(CrosshairVariant.Regular);
        return true;
    }

    public static boolean shouldShowCrosshair() {
        return shouldShowCrosshair;
    }

    public static void tick() {
        shouldShowCrosshair = checkShowCrosshair();
    }
}
