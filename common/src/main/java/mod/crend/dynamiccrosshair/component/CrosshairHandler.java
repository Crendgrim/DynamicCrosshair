package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.CrosshairContextChange;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.api.InvalidContextState;
import mod.crend.dynamiccrosshair.config.InteractableCrosshairPolicy;
import mod.crend.dynamiccrosshair.config.UsableCrosshairPolicy;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CrosshairHandler {

    public static final Identifier crosshairTexture = new Identifier(DynamicCrosshair.MOD_ID, "textures/gui/crosshairs.png");
    public static final Logger LOGGER = LoggerFactory.getLogger(DynamicCrosshair.MOD_ID);

    private static Crosshair activeCrosshair = new Crosshair();
    private static boolean shouldShowCrosshair = true;

    public static Crosshair getActiveCrosshair() {
        return activeCrosshair;
    }


    private static boolean isBlockInteractable(CrosshairContext context) {
        // interactable blocks if not sneaking
        if (DynamicCrosshair.config.dynamicCrosshairOnBlock() != InteractableCrosshairPolicy.Disabled && context.isWithBlock() && context.shouldInteract()) {
            for (DynamicCrosshairApi api : context.apis()) {
                try {
                    if (api.isAlwaysInteractableBlock(context.getBlockState()) || api.isInteractableBlock(context.getBlockState())) {
                        return true;
                    }
                } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
                    LOGGER.error("Exception occurred during evaluation of API " + api.getModId(), e);
                }
            }
        }
        return false;
    }

    private static Crosshair buildCrosshairAdvancedFromItem(CrosshairContext context) {
        UsableCrosshairPolicy usableItemPolicy = DynamicCrosshair.config.dynamicCrosshairHoldingUsableItem();
        for (DynamicCrosshairApi api : context.apis()) {
            try {
                if (usableItemPolicy != UsableCrosshairPolicy.Disabled) {
                    ItemStack itemStack = context.getItemStack();
                    if ((usableItemPolicy == UsableCrosshairPolicy.Always || !context.isCoolingDown()) && api.isAlwaysUsableItem(itemStack)) {
                        return Crosshair.USABLE;
                    }
                    if (usableItemPolicy == UsableCrosshairPolicy.Always && api.isUsableItem(itemStack)) {
                        return Crosshair.USABLE;
                    }
                }
                Crosshair crosshair = api.computeFromItem(context);
                if (crosshair != null) return crosshair;
            } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
                LOGGER.error("Exception occurred during evaluation of API " + api.getModId(), e);
            }
        }
        return null;
    }

    private static Crosshair buildCrosshairAdvancedByHand(CrosshairContext context) {
        Crosshair crosshair = null;
        // Targeted block / entity
        if (context.isWithEntity()) {
            for (DynamicCrosshairApi api : context.apis()) {
                try {
                    if (api.isAlwaysInteractableEntity(context.getEntity())) {
                        crosshair = Crosshair.INTERACTABLE;
                    } else {
                        crosshair = api.computeFromEntity(context);
                    }
                    if (crosshair != null) break;
                } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
                    LOGGER.error("Exception occurred during evaluation of API " + api.getModId(), e);
                }
            }
        } else if (context.isWithBlock() && context.shouldInteract()) {
            for (DynamicCrosshairApi api : context.apis()) {
                try {
                    if (api.isAlwaysInteractableBlock(context.getBlockState())) {
                        crosshair = Crosshair.INTERACTABLE;
                    } else {
                        crosshair = api.computeFromBlock(context);
                    }
                    if (crosshair != null) break;
                } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
                    LOGGER.error("Exception occurred during evaluation of API " + api.getModId(), e);
                }
            }
        }
        return Crosshair.combine(crosshair, buildCrosshairAdvancedFromItem(context));
    }

    private static Crosshair buildCrosshairAdvanced(CrosshairContext context) {
        // Main hand
        Crosshair crosshair = buildCrosshairAdvancedByHand(context);
        if (crosshair != null
                && (crosshair.hasStyle() || crosshair.isLockedStyle())
                && (crosshair.hasModifierUse() || crosshair.isLockedModifierUse())
        ) {
            return crosshair;
        }

        // Off hand
        context.setHand(Hand.OFF_HAND);
        Crosshair offhandCrosshair = buildCrosshairAdvancedByHand(context);
        context.setHand(Hand.MAIN_HAND);
        if (offhandCrosshair != null) {
            return Crosshair.combine(crosshair, offhandCrosshair);
        }
        return crosshair;
    }

    private static Crosshair buildCrosshairSimple(CrosshairContext context) {
        Crosshair crosshair = null;
        // Main hand
        for (DynamicCrosshairApi api : context.apis()) {
            try {
                crosshair = switch (api.getItemCategory(context.getItemStack())) {
                    case TOOL -> Crosshair.TOOL;
                    case MELEE_WEAPON -> Crosshair.MELEE_WEAPON;
                    case RANGED_WEAPON -> Crosshair.RANGED_WEAPON;
                    case THROWABLE -> Crosshair.THROWABLE;
                    case BLOCK -> Crosshair.HOLDING_BLOCK;
                    case SHIELD -> Crosshair.SHIELD;
                    case USABLE -> Crosshair.USABLE;
                    default -> null;
                };
                if (crosshair != null) break;
            } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
                LOGGER.error("Exception occurred during evaluation of API " + api.getModId(), e);
            }
        }
        // Offhand
        if (crosshair == null) {
            context.setHand(Hand.OFF_HAND);
            for (DynamicCrosshairApi api : context.apis()) {
                crosshair = switch (api.getItemCategory(context.getItemStack())) {
                    case RANGED_WEAPON -> Crosshair.RANGED_WEAPON;
                    case THROWABLE -> Crosshair.THROWABLE;
                    case BLOCK -> Crosshair.HOLDING_BLOCK;
                    case SHIELD -> Crosshair.SHIELD;
                    case USABLE -> Crosshair.USABLE;
                    default -> null;
                };
                if (crosshair != null) break;
            }
            context.setHand(Hand.MAIN_HAND);
        }

        // Entity
        if (context.isWithEntity()) {
            Entity entity = context.getEntity();
            for (DynamicCrosshairApi api : context.apis()) {
                try {
                    if (api.isAlwaysInteractableEntity(entity) || api.isInteractableEntity(entity)) {
                        return Crosshair.combine(Crosshair.INTERACTABLE, crosshair);
                    }
                } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
                    LOGGER.error("Exception occurred during evaluation of API " + api.getModId(), e);
                }
            }
        // Block
        } else if (context.isWithBlock()) {
            BlockState blockState = context.getBlockState();
            for (DynamicCrosshairApi api : context.apis()) {
                if (api.isAlwaysInteractableBlock(blockState) || api.isInteractableBlock(blockState)) {
                    try {
                        return Crosshair.combine(Crosshair.INTERACTABLE, crosshair);
                    } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
                        LOGGER.error("Exception occurred during evaluation of API " + api.getModId(), e);
                    }
                }
            }
        }
        return crosshair;
    }

    private static Crosshair buildCrosshairDynamic(CrosshairContext context) {
        if (DynamicCrosshair.config.isDynamicCrosshairSimple()) {
            return buildCrosshairSimple(context);
        }
        return buildCrosshairAdvanced(context);
    }

    private static void debug(List<DynamicCrosshairApi> apis, Function<DynamicCrosshairApi, Object> callback) {
        for (DynamicCrosshairApi api : apis) {
            try {
                LOGGER.info("  {}:{}: {}", api.getNamespace(), api.getModId(), callback.apply(api));
            } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
                LOGGER.info("  {}:{}: FAILED", api.getNamespace(), api.getModId());
            }
        }
    }

    @SuppressWarnings("unused")
    public static void debug() {
        CrosshairContext context = state.context;
        var apis = context.apis();
        LOGGER.info("Dynamic Crosshair evaluation");
        LOGGER.info("Context:");
        LOGGER.info("  Main hand:{}", context.getItemStack(Hand.MAIN_HAND));
        LOGGER.info("  Offhand:{}", context.getItemStack(Hand.OFF_HAND));
        LOGGER.info("  Block:{}", context.isWithBlock() ? context.getBlockState() : "null");
        LOGGER.info("  Entity:{}", context.isWithEntity() ? context.getEntity() : "null");
        LOGGER.info("Active APIs: " + apis.stream().map(api -> api.getNamespace() + ":" + api.getModId()).toList());
        LOGGER.info("Forcing invalidation: {}", apis.stream().filter(api -> api.forceInvalidate(context)).collect(Collectors.toList()));
        LOGGER.info(".getItemCategory(MAIN_HAND)");
        debug(apis, api -> api.getItemCategory(context.getItemStack()));
        LOGGER.info(".isAlwaysUsableItem(MAIN_HAND)");
        debug(apis, api -> api.isAlwaysUsableItem(context.getItemStack()));
        LOGGER.info(".isUsableItem(MAIN_HAND)");
        debug(apis, api -> api.isUsableItem(context.getItemStack()));
        LOGGER.info(".computeFromItem(MAIN_HAND)");
        debug(apis, api -> api.computeFromItem(context));
        context.setHand(Hand.OFF_HAND);
        LOGGER.info(".getItemCategory(OFF_HAND)");
        debug(apis, api -> api.getItemCategory(context.getItemStack()));
        LOGGER.info(".isAlwaysUsableItem(OFF_HAND)");
        debug(apis, api -> api.isAlwaysUsableItem(context.getItemStack()));
        LOGGER.info(".isUsableItem(OFF_HAND)");
        debug(apis, api -> api.isUsableItem(context.getItemStack()));
        LOGGER.info(".computeFromItem(OFF_HAND)");
        debug(apis, api -> api.computeFromItem(context));
        context.setHand(Hand.MAIN_HAND);
        if (context.isWithEntity()) {
            LOGGER.info(".isAlwaysInteractableEntity()");
            debug(apis, api -> api.isAlwaysInteractableEntity(context.getEntity()));
            LOGGER.info(".isInteractableEntity()");
            debug(apis, api -> api.isInteractableEntity(context.getEntity()));
            LOGGER.info(".computeFromEntity(MAIN_HAND)");
            debug(apis, api -> api.computeFromEntity(context));
            context.setHand(Hand.OFF_HAND);
            LOGGER.info(".computeFromEntity(OFF_HAND)");
            debug(apis, api -> api.computeFromEntity(context));
            context.setHand(Hand.MAIN_HAND);
        }
        if (context.isWithBlock()) {
            LOGGER.info(".isAlwaysInteractableBlock()");
            debug(apis, api -> api.isAlwaysInteractableBlock(context.getBlockState()));
            LOGGER.info(".isInteractableBlock()");
            debug(apis, api -> api.isInteractableBlock(context.getBlockState()));
            LOGGER.info(".computeFromBlock(MAIN_HAND)");
            debug(apis, api -> api.computeFromBlock(context));
            context.setHand(Hand.OFF_HAND);
            LOGGER.info(".computeFromBlock(OFF_HAND)");
            debug(apis, api -> api.computeFromBlock(context));
            context.setHand(Hand.MAIN_HAND);
        }
    }


    static State state = null;

    private static Optional<Boolean> buildCrosshair(HitResult hitResult, ClientPlayerEntity player) {
        try {
            for (DynamicCrosshairApi api : state.context.apis()) {
                try {
                    hitResult = api.overrideHitResult(state.context, hitResult);
                } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
                    LOGGER.error("Exception occurred during evaluation of API " + api.getModId(), e);
                }
            }

            if (!state.changed(hitResult, player)) {
                return Optional.of(shouldShowCrosshair);
            }

            // State changed, build new crosshair
            activeCrosshair = new Crosshair();

            switch (hitResult.getType()) {
                case ENTITY -> {
                    if (DynamicCrosshair.config.dynamicCrosshairOnEntity()) {
                        activeCrosshair.setVariant(CrosshairVariant.OnEntity);
                    }
                }
                case BLOCK -> {
                    switch (DynamicCrosshair.config.dynamicCrosshairOnBlock()) {
                        case IfTargeting -> activeCrosshair.setVariant(CrosshairVariant.OnBlock);
                        case IfInteractable -> {
                            if (isBlockInteractable(state.context)) {
                                activeCrosshair.setVariant(CrosshairVariant.OnBlock);
                            }
                        }
                    }
                }
            }
            if (activeCrosshair.updateFrom(buildCrosshairDynamic(state.context))) {
                return Optional.of(true);
            }
        } catch (CrosshairContextChange crosshairContextChange) {
            // For some reason, we are being asked to re-evaluate the context.
            return buildCrosshair(crosshairContextChange.newHitResult, player);
        } catch (InvalidContextState invalidContextState) {
            LOGGER.error("Encountered invalid context state: ", invalidContextState);
        } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
            LOGGER.error("Encountered an unexpected error. This usually is due to outdated mod support." + e);
        }
        return Optional.empty();
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

        Optional<Boolean> result = buildCrosshair(hitResult, player);
        if (result.isPresent()) {
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
