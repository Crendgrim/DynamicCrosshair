package mod.crend.dynamiccrosshair.impl;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshairapi.crosshair.CrosshairContext;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.exception.CrosshairContextChange;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import mod.crend.dynamiccrosshairapi.exception.InvalidContextState;
import mod.crend.dynamiccrosshair.component.CrosshairHandler;
import mod.crend.dynamiccrosshair.config.CrosshairPolicy;
import mod.crend.dynamiccrosshair.config.UsableCrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.BlockItemAccessor;
import mod.crend.dynamiccrosshair.mixin.ItemAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
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

//? if <=1.21.4 {
import net.minecraft.item.MiningToolItem;
//?} else
/*import net.minecraft.registry.tag.ItemTags;*/

//? if fabric {
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
//?}

public class CrosshairContextImpl implements CrosshairContext {

	@NotNull
	public ClientWorld world;
	@NotNull
	public final ClientPlayerEntity player;
	@NotNull
	public HitResult hitResult;

	private final ContextedApiImpl api;

	public CrosshairContextImpl() {
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

	@Override
	public @NotNull ClientWorld getWorld() {
		return world;
	}

	@Override
	public @NotNull ClientPlayerEntity getPlayer() {
		return player;
	}

	@Override
	public @NotNull HitResult getHitResult() {
		return hitResult;
	}

	@Override
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

	@Override
	public void invalidateItem(Hand hand) {
		switch (hand) {
			case MAIN_HAND -> itemStackMainHand = null;
			case OFF_HAND -> itemStackOffHand = null;
		}
	}

	@Override
	public boolean isTargeting() {
		return hitResult.getType() != HitResult.Type.MISS;
	}

	@Override
	public boolean isEmptyHanded() {
		return (player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty());
	}

	/**
	 * @return true if player is not crouching, or if they are crouching but both hands are empty
	 */
	@Override
	public boolean shouldInteract() {
		return (!player.shouldCancelInteraction() || isEmptyHanded());
	}

	@Override
	public boolean isFlying() {
		//? if <1.21.2 {
		return player.isFallFlying();
		//?} else
		/*return player.isGliding();*/
	}

	private boolean withBlock = false;
	private BlockPos blockPos = null;
	private BlockState blockState = null;
	private BlockEntity blockEntity = null;

	@Override
	public boolean isWithBlock() {
		return withBlock;
	}

	@Override
	public BlockPos getBlockPos() {
		return blockPos;
	}

	@Override
	public BlockState getBlockState() {
		if (!withBlock) throw new InvalidContextState("Called getBlockState() without a targeted block!");
		if (blockPos == null) throw new InvalidContextState("In getBlockState(): blockPos is null despite targeted block!");
		if (blockState == null) {
			blockState = world.getBlockState(blockPos);
		}
		return blockState;
	}

	@Override
	public Block getBlock() {
		return getBlockState().getBlock();
	}

	@Override
	public BlockEntity getBlockEntity() {
		if (!withBlock) throw new InvalidContextState("Called getBlockEntity() without a targeted block!");
		if (blockPos == null) throw new InvalidContextState("In getBlockEntity(): blockPos is null despite targeted block!");
		if (blockEntity == null) {
			blockEntity = world.getBlockEntity(blockPos);
		}
		return blockEntity;
	}

	@Override
	public FluidState getFluidState() {
		if (!withBlock) throw new InvalidContextState("Called getFluidState() without a targeted block!");
		if (blockPos == null) throw new InvalidContextState("In getFluidState(): blockPos is null despite targeted block!");
		return world.getFluidState(blockPos);
	}

	@Override
	public BlockHitResult getBlockHitResult() {
		if (!withBlock) throw new InvalidContextState("Called getFluidState() without a targeted block!");
		return (BlockHitResult) hitResult;
	}

	@Override
	public Direction getBlockHitSide() {
		return getBlockHitResult().getSide();
	}

	@Override
	public BlockHitResult raycastWithFluid(RaycastContext.FluidHandling fluidHandling) {
		return ItemAccessor.invokeRaycast(world, player, fluidHandling);
	}
	@Override
	public BlockHitResult raycastWithFluid() {
		return raycastWithFluid(RaycastContext.FluidHandling.ANY);
	}

	@Override
	public EntityHitResult raycastForEntity(double d) {
		Vec3d vCamPos = player.getCameraPosVec(1.0f);
		Vec3d vRotation = player.getRotationVec(1.0f);
		Vec3d vRaycast = vCamPos.add(vRotation.x * d, vRotation.y * d, vRotation.z * d);
		Box box = player.getBoundingBox().stretch(vRotation.multiply(d)).expand(1.0, 1.0, 1.0);
		return ProjectileUtil.raycast(player, vCamPos, vRaycast, box, entity -> !entity.isSpectator() && entity.isCollidable(/*? if >=1.21.6 {*//*player*//*?}*/), d * d);
	}

	private boolean withEntity = false;
	private Entity entity = null;

	@Override
	public boolean isWithEntity() {
		return withEntity;
	}
	@Override
	public Entity getEntity() {
		if (!withEntity) throw new InvalidContextState("Called getEntity() without a targeted entity!");
		if (entity == null) throw new InvalidContextState("In getEntity(): entity is null despite targeted entity!");
		return entity;
	}

	private Hand hand;
	private ItemStack itemStackMainHand = null;
	private ItemStack itemStackOffHand = null;

	@Override
	public Hand getHand() {
		return hand;
	}
	@Override
	public void setHand(Hand hand) {
		this.hand = hand;
	}
	@Override
	public boolean isMainHand() {
		return hand == Hand.MAIN_HAND;
	}
	@Override
	public boolean isOffHand() {
		return hand == Hand.OFF_HAND;
	}

	@Override
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
	@Override
	public ItemStack getItemStack() {
		return getItemStack(hand);
	}

	@Override
	public Item getItem() {
		return getItemStack().getItem();
	}

	@Override
	public boolean isActiveItem() {
		return player.getActiveItem().equals(getItemStack());
	}

	@Override
	public boolean isCoolingDown() {
		return player.getItemCooldownManager().isCoolingDown(
				//? if <1.21.2 {
				getItem()
				//?} else
				/*getItemStack()*/
		);
	}

	@Override
	public boolean canPlaceItemAsBlock() {
		if (!withBlock) return false;
		if (!(getItem() instanceof BlockItem)) return true;
		BlockItemAccessor blockItem = (BlockItemAccessor) getItem();
		ItemPlacementContext itemPlacementContext = new ItemPlacementContext(player, hand, getItemStack(), (BlockHitResult) hitResult);
		try {
			BlockState blockState = blockItem.invokeGetPlacementState(itemPlacementContext);
			return (blockState != null && blockItem.invokeCanPlace(itemPlacementContext, blockState));
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public boolean canUseWeaponAsTool() {
		return isWithBlock() && DynamicCrosshairMod.config.dynamicCrosshairHoldingTool() != CrosshairPolicy.Disabled;
	}

	//? if fabric {
	public boolean canInteractWithFluidStorage(Storage<FluidVariant> storage) {
		Storage<FluidVariant> handStorage = ContainerItemContext.forPlayerInteraction(getPlayer(), getHand()).find(FluidStorage.ITEM);
		if (handStorage == null) return false;

		try (var tx = Transaction.openOuter()) {
			return StorageUtil.move(storage, handStorage, fv -> true, Long.MAX_VALUE, tx) > 0 || StorageUtil.move(handStorage, storage, fv -> true, Long.MAX_VALUE, tx) > 0;
		}
	}
	//?}


	@Override
	public boolean includeUsableItem() {
		return switch (DynamicCrosshairMod.config.dynamicCrosshairHoldingUsableItem()) {
			case Always -> true;
			case IfInteractable -> !isCoolingDown();
			case Disabled -> false;
		};
	}
	@Override
	public boolean includeThrowable() {
		return switch (DynamicCrosshairMod.config.dynamicCrosshairHoldingThrowable()) {
			case Always -> true;
			case IfInteractable -> !isCoolingDown();
			case Disabled -> false;
		};
	}
	@Override
	public boolean includeRangedWeapon() {
		return DynamicCrosshairMod.config.dynamicCrosshairHoldingRangedWeapon() != UsableCrosshairPolicy.Disabled;
	}
	@Override
	public boolean includeMeleeWeapon() {
		return isMainHand() && DynamicCrosshairMod.config.dynamicCrosshairHoldingMeleeWeapon();
	}
	@Override
	public boolean includeTool() {
		return isMainHand() && switch (DynamicCrosshairMod.config.dynamicCrosshairHoldingTool()) {
			case Always -> true;
			case IfTargeting -> isTargeting();
			case Disabled -> false;
		};
	}
	@Override
	public boolean includeShield() {
		return DynamicCrosshairMod.config.dynamicCrosshairHoldingShield() && !isCoolingDown();
	}
	@Override
	public boolean includeHoldingBlock() {
		if (isOffHand() && !DynamicCrosshairMod.config.dynamicCrosshairHoldingBlockInOffhand()) return false;
		return switch (DynamicCrosshairMod.config.dynamicCrosshairHoldingBlock()) {
			case Always -> true;
			case IfTargeting -> isTargeting();
			case IfInteractable -> canPlaceItemAsBlock();
			case Disabled -> false;
		};
	}


	private ApiList apiList = null;

	@Override
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

	@Override
	public ContextedApiImpl api() {
		return api;
	}

	@Override
	public <R> @Nullable R withApisUntilNonNull(Function<DynamicCrosshairApi, R> lambda) {
		for (DynamicCrosshairApi api : apis()) {
			try {
				R result = lambda.apply(api);
				if (result != null) return result;
			} catch (NoSuchMethodError | NoSuchFieldError | NoClassDefFoundError | RuntimeException e) {
				if (e instanceof CrosshairContextChange) throw e;
				CrosshairHandler.LOGGER.error("Exception occurred during evaluation of API {}", api.getModId(), e);
			}
		}
		return null;
	}

	@Override
	public InteractionType checkToolWithBlock() {
		if (!isWithBlock()) {
			return InteractionType.EMPTY;
		}
		BlockState blockState = getBlockState();
		ItemStack handItemStack = getItemStack();
		Item handItem = getItem();
		if (
				//? if <=1.21.4 {
				handItem instanceof MiningToolItem
				//?} else {
				/*handItemStack.isIn(ItemTags.AXES)
				|| handItemStack.isIn(ItemTags.SHOVELS)
				|| handItemStack.isIn(ItemTags.PICKAXES)
				|| handItemStack.isIn(ItemTags.HOES)
				*///?}
		) {
			if (handItemStack.isSuitableFor(blockState)
					&& handItem.canMine(/*? if >1.21.4 {*//*handItemStack, *//*?}*/blockState, getWorld(), blockPos, getPlayer())) {
				return InteractionType.CORRECT_TOOL;
			} else {
				return InteractionType.INCORRECT_TOOL;
			}
		}
		if (handItemStack.getMiningSpeedMultiplier(blockState) > 1.0f
				&& handItem.canMine(/*? if >1.21.4 {*//*handItemStack, *//*?}*/blockState, getWorld(), blockPos, getPlayer())) {
			return InteractionType.CORRECT_TOOL;
		}
		if (handItem instanceof ShearsItem) {
			// (shears item && correct tool) is handled by the getMiningSpeedMultiplier branch
			return InteractionType.INCORRECT_TOOL;
		}
		return InteractionType.EMPTY;
	}
}
