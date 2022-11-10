package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.component.InvalidContextState;
import mod.crend.dynamiccrosshair.config.CrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.IBlockItemMixin;
import mod.crend.dynamiccrosshair.mixin.IItemMixin;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class CrosshairContext {

	@NonNull
	public final ClientWorld world;
	@NonNull
	public final ClientPlayerEntity player;
	@NonNull
	public HitResult hitResult;

	public CrosshairContext() {
		assert MinecraftClient.getInstance().world != null;
		assert MinecraftClient.getInstance().player != null;
		assert MinecraftClient.getInstance().crosshairTarget != null;
		world = MinecraftClient.getInstance().world;
		player = MinecraftClient.getInstance().player;
		hand = Hand.MAIN_HAND;
		hitResult = MinecraftClient.getInstance().crosshairTarget;
		invalidateHitResult(hitResult);
	}

	public void invalidateHitResult(HitResult newHitResult) {
		assert newHitResult != null;
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
		return IItemMixin.invokeRaycast(world, player, fluidHandling);
	}
	public BlockHitResult raycastWithFluid() {
		return raycastWithFluid(RaycastContext.FluidHandling.ANY);
	}

	public EntityHitResult raycastForEntity(double d) {
		Vec3d vCamPos = player.getCameraPosVec(1.0f);
		Vec3d vRotation = player.getRotationVec(1.0f);
		Vec3d vRaycast = vCamPos.add(vRotation.x * d, vRotation.y * d, vRotation.z * d);
		Box box = player.getBoundingBox().stretch(vRotation.multiply(d)).expand(1.0, 1.0, 1.0);
		EntityHitResult entityHitResult = ProjectileUtil.raycast(player, vCamPos, vRaycast, box, entity -> !entity.isSpectator() && entity.isCollidable(), d * d);
		return entityHitResult;
	}

	@SuppressWarnings("UnstableApiUsage")
	public boolean canInteractWithFluidStorage(Storage<FluidVariant> storage) {
		Storage<FluidVariant> handStorage = ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM);
		if (handStorage == null) return false;

		try (var tx = Transaction.openOuter()) {
			return StorageUtil.move(storage, handStorage, fv -> true, Long.MAX_VALUE, tx) > 0 || StorageUtil.move(handStorage, storage, fv -> true, Long.MAX_VALUE, tx) > 0;
		}
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

	public boolean isActiveItem() {
		return player.getActiveItem().equals(getItemStack());
	}

	public boolean isCoolingDown() {
		return player.getItemCooldownManager().isCoolingDown(getItem());
	}

	public boolean canPlaceItemAsBlock() {
		if (!withBlock) throw new InvalidContextState("Called canPlaceItemAsBlock() without a targeted block!");
		IBlockItemMixin blockItem = (IBlockItemMixin) getItem();
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

}
