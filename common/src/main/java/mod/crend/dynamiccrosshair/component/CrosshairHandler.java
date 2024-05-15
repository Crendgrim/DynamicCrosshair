package mod.crend.dynamiccrosshair.component;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.Crosshair;
import mod.crend.dynamiccrosshair.api.CrosshairContext;
import mod.crend.dynamiccrosshair.api.exception.CrosshairContextChange;
import mod.crend.dynamiccrosshair.api.CrosshairVariant;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairBlock;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairEntity;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairItem;
import mod.crend.dynamiccrosshair.api.InteractionType;
import mod.crend.dynamiccrosshair.api.exception.InvalidContextState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CrosshairHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(DynamicCrosshair.MOD_ID);

    private static Crosshair activeCrosshair = new Crosshair();
    private static boolean shouldShowCrosshair = true;
    public static boolean forceShowCrosshair = false;

    public static Crosshair getActiveCrosshair() {
        return activeCrosshair;
    }

    private static boolean isBlockInteractable(CrosshairContext context) {
        // interactable blocks if not sneaking
        if (context.isWithBlock() && context.shouldInteract()) {
            return context.api().test(api -> api.isAlwaysInteractable(context.getBlockState()) || api.isInteractable(context.getBlockState()));
        }
        return false;
    }


    private static Crosshair buildCrosshairAdvancedFromItem(CrosshairContext context) {
        InteractionType interactionType = ((DynamicCrosshairItem) context.getItem()).dynamiccrosshair$compute(context);
        interactionType = switch (interactionType) {
            case MELEE_WEAPON -> context.includeMeleeWeapon() ? interactionType : InteractionType.EMPTY;
            case RANGED_WEAPON -> context.includeRangedWeapon() ? interactionType : InteractionType.EMPTY;
            case PLACE_BLOCK -> context.includeHoldingBlock() ? interactionType : InteractionType.EMPTY;
            case SHIELD -> context.includeShield() ? interactionType : InteractionType.EMPTY;
            case TOOL, CORRECT_TOOL, INCORRECT_TOOL, USABLE_TOOL ->
                    context.includeTool() ? interactionType : InteractionType.EMPTY;
            case USE_ITEM, USE_ITEM_ON_BLOCK, USE_ITEM_ON_ENTITY, CHARGE_ITEM, EQUIP_ITEM, CONSUME_ITEM ->
                    context.includeUsableItem() ? interactionType : InteractionType.EMPTY;
            case THROW_ITEM -> context.includeThrowable() ? interactionType : InteractionType.EMPTY;
            default -> interactionType;
        };

        InteractionType finalInteractionType = interactionType;
        Crosshair override = context.withApisUntilNonNull(api -> api.overrideFromItem(context, finalInteractionType));

        if (interactionType == InteractionType.EMPTY || interactionType == InteractionType.NO_ACTION) {
            return context.withApisUntilNonNull(api -> api.computeFromItem(context));
        }

        if (override != null) return override;

        return Crosshair.of(interactionType);
    }

    private static Crosshair buildCrosshairAdvancedFromEntity(CrosshairContext context) {
        InteractionType interactionType = ((DynamicCrosshairEntity) context.getEntity()).dynamiccrosshair$compute(context);
        if (interactionType != InteractionType.EMPTY) return Crosshair.of(interactionType);

        return context.withApisUntilNonNull(api -> api.computeFromEntity(context));
    }

    private static Crosshair buildCrosshairAdvancedFromBlock(CrosshairContext context) {
        InteractionType interactionType = ((DynamicCrosshairBlock) context.getBlock()).dynamiccrosshair$compute(context);
        if (interactionType != InteractionType.EMPTY) return Crosshair.of(interactionType);

        return context.withApisUntilNonNull(api -> api.computeFromBlock(context));
    }

    private static Crosshair buildCrosshairAdvancedByHand(CrosshairContext context) {
        Crosshair crosshair = null;
        // Targeted block / entity
        if (context.isWithEntity()) {
            crosshair = buildCrosshairAdvancedFromEntity(context);
        } else if (context.isWithBlock() && context.shouldInteract() && DynamicCrosshairMod.config.dynamicCrosshairOnInteractableBlock()) {
            crosshair = buildCrosshairAdvancedFromBlock(context);
        }
        return Crosshair.combine(crosshair, buildCrosshairAdvancedFromItem(context));
    }

    private static Crosshair buildCrosshairDynamic(CrosshairContext context) {
        // Main hand
        Crosshair crosshair = buildCrosshairAdvancedByHand(context);
        if (crosshair != null && crosshair.interactionMode().isRightClick()) {
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

    private static void debug(List<DynamicCrosshairApi> apis, Function<DynamicCrosshairApi, Object> callback) {
        for (DynamicCrosshairApi api : apis) {
            try {
                LOGGER.info("  {}:{}: {}", api.getNamespace(), api.getModId(), callback.apply(api));
            } catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
                if (e instanceof CrosshairContextChange) throw e;
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
        LOGGER.info("MAIN_HAND.computeCrosshair: {}", ((DynamicCrosshairItem) context.getItem()).dynamiccrosshair$compute(context));
        LOGGER.info(".computeFromItem(MAIN_HAND)");
        debug(apis, api -> api.computeFromItem(context));
        context.setHand(Hand.OFF_HAND);
        LOGGER.info("OFF_HAND.computeCrosshair: {}", ((DynamicCrosshairItem) context.getItem()).dynamiccrosshair$compute(context));
        LOGGER.info(".computeFromItem(OFF_HAND)");
        debug(apis, api -> api.computeFromItem(context));
        context.setHand(Hand.MAIN_HAND);
        if (context.isWithEntity()) {
            LOGGER.info(".computeFromEntity(MAIN_HAND)");
            debug(apis, api -> api.computeFromEntity(context));
            context.setHand(Hand.OFF_HAND);
            LOGGER.info(".computeFromEntity(OFF_HAND)");
            debug(apis, api -> api.computeFromEntity(context));
            context.setHand(Hand.MAIN_HAND);
        }
        if (context.isWithBlock()) {
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
                    if (e instanceof CrosshairContextChange) throw e;
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
                    if (DynamicCrosshairMod.config.dynamicCrosshairOnEntity()) {
                        activeCrosshair = activeCrosshair.withVariant(CrosshairVariant.OnEntity);
                    }
                }
                case BLOCK -> {
                    if (DynamicCrosshairMod.config.dynamicCrosshairOnBlock()) {
                        activeCrosshair = activeCrosshair.withVariant(CrosshairVariant.OnBlock);
                    } else if (DynamicCrosshairMod.config.dynamicCrosshairOnInteractableBlock()) {
                        if (isBlockInteractable(state.context)) {
                            activeCrosshair = activeCrosshair.withVariant(CrosshairVariant.OnBlock);
                        }
                    }
                }
            }
            activeCrosshair = activeCrosshair.updateFrom(buildCrosshairDynamic(state.context));
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
        if (player == null || (state != null && state.context.getPlayer() != player)) {
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
        if (DynamicCrosshairMod.config.isHideWithScreen() && MinecraftClient.getInstance().currentScreen != null) {
            state = null;
            return false;
        }

        if (DynamicCrosshairMod.config.isHideWithMap()) {
            if (player.getMainHandStack().getItem() instanceof FilledMapItem
                    && player.getOffHandStack().isEmpty()) {
                state = null;
                return false;
            }
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
        if (DynamicCrosshairMod.config.isDynamicCrosshair()) {
            return false;
        }
        // Dynamic crosshair disabled, no other crosshair computed: make sure to show a crosshair
        activeCrosshair = activeCrosshair.withVariant(CrosshairVariant.Regular);
        return true;
    }

    public static boolean shouldShowCrosshair() {
        return shouldShowCrosshair;
    }

    public static void tick() {
        shouldShowCrosshair = checkShowCrosshair();
    }
}
