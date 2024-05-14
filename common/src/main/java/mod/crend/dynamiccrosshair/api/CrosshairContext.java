package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.CrosshairPolicy;
import mod.crend.dynamiccrosshair.config.UsableCrosshairPolicy;
import mod.crend.dynamiccrosshair.impl.ApiList;
import mod.crend.dynamiccrosshair.impl.ContextedApiImpl;
import mod.crend.dynamiccrosshair.mixin.item.BlockItemAccessor;
import mod.crend.dynamiccrosshair.mixin.item.ItemAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class CrosshairContext {

	@NotNull
	public ClientWorld world;
	@NotNull
	public final ClientPlayerEntity player;
	@NotNull
	public HitResult hitResult;

	private final ContextedApiImpl api;

	public CrosshairContext() {
		assert MinecraftClient.getInstance().world != null;
		assert MinecraftClient.getInstance().player != null;
		assert MinecraftClient.getInstance().crosshairTarget != null;
		world = MinecraftClient.getInstance().world;
		player = MinecraftClient.getInstance().player;
		hand = Hand.MAIN_HAND;
		hitResult = MinecraftClient.getInstance().crosshairTarget;
		api = new ContextedApiImpl(this);
		invalidateHitResult(hitResult);
	}

	public void invalidateHitResult(HitResult newHitResult) {
		assert newHitResult != null;
		assert MinecraftClient.getInstance().world != null;
		hitResult = newHitResult;
		withBlock = false;
		blockPos = null;
		blockState = null;
		blockEntity = null;
		withEntity = false;
		entity = null;
		apiList = null;
		itemStackMainHand = null;
		itemStackOffHand = null;
		switch (hitResult.getType()) {
			case BLOCK -> {
				BlockHitResult blockHitResult = (BlockHitResult) hitResult;
				withBlock = true;
				blockPos = blockHitResult.getBlockPos();
			}
			case ENTITY -> {
				EntityHitResult entityHitResult = (EntityHitResult) hitResult;
				withEntity = true;
				entity = entityHitResult.getEntity();
			}
		}
		world = MinecraftClient.getInstance().world;
		for (DynamicCrosshairApi api : apis()) {
			try {
				ClientWorld useWorld = api.overrideWorld();
				if (useWorld != null) {
					world = useWorld;
					break;
				}
			} catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException ignored) { }
		}
	}

	public void invalidateItem(Hand hand) {
		switch (hand) {
			case MAIN_HAND -> itemStackMainHand = null;
			case OFF_HAND -> itemStackOffHand = null;
		}
	}

	public boolean isTargeting() {
		return hitResult.getType() != HitResult.Type.MISS;
	}

	public boolean isEmptyHanded() {
		return (player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty());
	}

	/**
	 * @return true if player is not crouching, or if they are crouching but both hands are empty
	 */
	public boolean shouldInteract() {
		return (!player.shouldCancelInteraction() || isEmptyHanded());
	}


	private boolean withBlock = false;
	private BlockPos blockPos = null;
	private BlockState blockState = null;
	private BlockEntity blockEntity = null;

	public boolean isWithBlock() {
		return withBlock;
	}

	public BlockPos getBlockPos() {
		return blockPos;
	}

	public BlockState getBlockState() {
		if (!withBlock) throw new InvalidContextState("Called getBlockState() without a targeted block!");
		if (blockPos == null) throw new InvalidContextState("In getBlockState(): blockPos is null despite targeted block!");
		if (blockState == null) {
			blockState = world.getBlockState(blockPos);
		}
		return blockState;
	}

	public Block getBlock() {
		return getBlockState().getBlock();
	}

	public BlockEntity getBlockEntity() {
		if (!withBlock) throw new InvalidContextState("Called getBlockEntity() without a targeted block!");
		if (blockPos == null) throw new InvalidContextState("In getBlockEntity(): blockPos is null despite targeted block!");
		if (blockEntity == null) {
			blockEntity = world.getBlockEntity(blockPos);
		}
		return blockEntity;
	}

	public FluidState getFluidState() {
		if (!withBlock) throw new InvalidContextState("Called getFluidState() without a targeted block!");
		if (blockPos == null) throw new InvalidContextState("In getFluidState(): blockPos is null despite targeted block!");
		return world.getFluidState(blockPos);
	}

	public BlockHitResult getBlockHitResult() {
		if (!withBlock) throw new InvalidContextState("Called getFluidState() without a targeted block!");
		return (BlockHitResult) hitResult;
	}

	public Direction getBlockHitSide() {
		return getBlockHitResult().getSide();
	}

	public BlockHitResult raycastWithFluid(RaycastContext.FluidHandling fluidHandling) {
		return ItemAccessor.invokeRaycast(world, player, fluidHandling);
	}
	public BlockHitResult raycastWithFluid() {
		return raycastWithFluid(RaycastContext.FluidHandling.ANY);
	}

	public EntityHitResult raycastForEntity(double d) {
		Vec3d vCamPos = player.getCameraPosVec(1.0f);
		Vec3d vRotation = player.getRotationVec(1.0f);
		Vec3d vRaycast = vCamPos.add(vRotation.x * d, vRotation.y * d, vRotation.z * d);
		Box box = player.getBoundingBox().stretch(vRotation.multiply(d)).expand(1.0, 1.0, 1.0);
		return ProjectileUtil.raycast(player, vCamPos, vRaycast, box, entity -> !entity.isSpectator() && entity.isCollidable(), d * d);
	}

	private boolean withEntity = false;
	private Entity entity = null;

	public boolean isWithEntity() {
		return withEntity;
	}
	public Entity getEntity() {
		if (!withEntity) throw new InvalidContextState("Called getEntity() without a targeted entity!");
		if (entity == null) throw new InvalidContextState("In getEntity(): entity is null despite targeted entity!");
		return entity;
	}


	private Hand hand;
	private ItemStack itemStackMainHand = null;
	private ItemStack itemStackOffHand = null;

	public Hand getHand() {
		return hand;
	}
	public void setHand(Hand hand) {
		this.hand = hand;
	}
	public boolean isMainHand() {
		return hand == Hand.MAIN_HAND;
	}
	public boolean isOffHand() {
		return hand == Hand.OFF_HAND;
	}

	public ItemStack getItemStack(Hand hand) {
		ItemStack itemStack = switch (hand) {
			case MAIN_HAND -> itemStackMainHand;
			case OFF_HAND -> itemStackOffHand;
		};
		if (itemStack == null) {
			itemStack = player.getStackInHand(hand);
			switch (hand) {
				case MAIN_HAND -> itemStackMainHand = itemStack;
				case OFF_HAND -> itemStackOffHand = itemStack;
			}
		}
		return itemStack;
	}
	public ItemStack getItemStack() {
		return getItemStack(hand);
	}

	public Item getItem() {
		return getItemStack().getItem();
	}

	public DynamicCrosshairApiItemStack getItemStackMixin() {
		return (DynamicCrosshairApiItemStack) (Object) getItemStack();
	}

	public boolean isActiveItem() {
		return player.getActiveItem().equals(getItemStack());
	}

	public boolean isCoolingDown() {
		return player.getItemCooldownManager().isCoolingDown(getItem());
	}

	public boolean canPlaceItemAsBlock() {
		if (!withBlock) return false;
		BlockItemAccessor blockItem = (BlockItemAccessor) getItem();
		ItemPlacementContext itemPlacementContext = new ItemPlacementContext(player, hand, getItemStack(), (BlockHitResult) hitResult);
		try {
			BlockState blockState = blockItem.invokeGetPlacementState(itemPlacementContext);
			return (blockState != null && blockItem.invokeCanPlace(itemPlacementContext, blockState));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public boolean canUseWeaponAsTool() {
		return isWithBlock() && DynamicCrosshair.config.dynamicCrosshairHoldingTool() != CrosshairPolicy.Disabled;
	}

	public boolean isRangedWeaponCharged(int bound) {
		return (isActiveItem() && getItem().getMaxUseTime(getItemStack()) - player.getItemUseTimeLeft() >= bound);
	}


	public boolean includeUsableItem() {
		return switch (DynamicCrosshair.config.dynamicCrosshairHoldingUsableItem()) {
			case Always -> true;
			case IfInteractable -> !isCoolingDown();
			case Disabled -> false;
		};
	}
	public boolean includeThrowable() {
		return switch (DynamicCrosshair.config.dynamicCrosshairHoldingThrowable()) {
			case Always -> true;
			case IfInteractable -> !isCoolingDown();
			case Disabled -> false;
		};
	}
	public boolean includeRangedWeapon() {
		return DynamicCrosshair.config.dynamicCrosshairHoldingRangedWeapon() != UsableCrosshairPolicy.Disabled;
	}
	public boolean includeMeleeWeapon() {
		return isMainHand() && DynamicCrosshair.config.dynamicCrosshairHoldingMeleeWeapon();
	}
	public boolean includeTool() {
		return isMainHand() && switch (DynamicCrosshair.config.dynamicCrosshairHoldingTool()) {
			case Always -> true;
			case IfTargeting -> isTargeting();
			case Disabled -> false;
		};
	}
	public boolean includeShield() {
		return DynamicCrosshair.config.dynamicCrosshairHoldingShield() && !isCoolingDown();
	}
	public boolean includeHoldingBlock() {
		return switch (DynamicCrosshair.config.dynamicCrosshairHoldingBlock()) {
			case Always, IfInteractable -> true;
			case IfTargeting -> isTargeting();
			case Disabled -> false;
		};
	}


	private ApiList apiList = null;

	public List<DynamicCrosshairApi> apis() {
		if (apiList == null) {
			apiList = new ApiList();
			apiList.add(getItemStack(Hand.MAIN_HAND));
			apiList.add(getItemStack(Hand.OFF_HAND));
			if (isWithBlock()) {
				apiList.add(getBlockState());
			}
			if (isWithEntity()) {
				apiList.add(getEntity());
			}
		}
		return apiList.get();
	}

	public ContextedApiImpl api() {
		return api;
	}

	public <R> @Nullable R withApis(Function<DynamicCrosshairApi, R> lambda) {
		for (DynamicCrosshairApi api : apis()) {
			try {
				R result = lambda.apply(api);
				if (result != null) return result;
			} catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
				if (e instanceof CrosshairContextChange) throw e;
				CrosshairHandler.LOGGER.error("Exception occurred during evaluation of API " + api.getModId(), e);
			}
		}
		return null;
	}
}
