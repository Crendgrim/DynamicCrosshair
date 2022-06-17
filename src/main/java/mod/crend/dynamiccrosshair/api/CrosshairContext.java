package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import mod.crend.dynamiccrosshair.config.CrosshairPolicy;
import mod.crend.dynamiccrosshair.mixin.IBlockItemMixin;
import mod.crend.dynamiccrosshair.mixin.IItemMixin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
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

	public CrosshairContext(Hand hand) {
		assert MinecraftClient.getInstance().world != null;
		assert MinecraftClient.getInstance().player != null;
		assert MinecraftClient.getInstance().crosshairTarget != null;
		world = MinecraftClient.getInstance().world;
		player = MinecraftClient.getInstance().player;
		hitResult = MinecraftClient.getInstance().crosshairTarget;
		this.hand = hand;
		invalidateHitResult();
	}

	public void invalidateHitResult() {
		assert MinecraftClient.getInstance().crosshairTarget != null;
		hitResult = MinecraftClient.getInstance().crosshairTarget;
		withBlock = false;
		blockPos = null;
		blockState = null;
		blockEntity = null;
		withEntity = false;
		entity = null;
		apiList = null;
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
	public void invalidateItem() {
		itemStack = null;
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
		assert(withBlock);
		if (blockState == null) {
			blockState = world.getBlockState(blockPos);
		}
		return blockState;
	}

	public Block getBlock() {
		return getBlockState().getBlock();
	}

	public BlockEntity getBlockEntity() {
		assert(withBlock);
		if (blockEntity == null) {
			blockEntity = world.getBlockEntity(blockPos);
		}
		return blockEntity;
	}

	public FluidState getFluidState() {
		if (blockPos == null) return null;
		return world.getFluidState(blockPos);
	}

	public BlockHitResult raycastWithFluid(RaycastContext.FluidHandling fluidHandling) {
		return IItemMixin.invokeRaycast(world, player, fluidHandling);
	}
	public BlockHitResult raycastWithFluid() {
		return raycastWithFluid(RaycastContext.FluidHandling.ANY);
	}


	private boolean withEntity = false;
	private Entity entity = null;

	public boolean isWithEntity() {
		return withEntity;
	}
	public Entity getEntity() {
		return entity;
	}


	private final Hand hand;
	private ItemStack itemStack = null;

	public Hand getHand() {
		return hand;
	}

	public ItemStack getItemStack() {
		if (itemStack == null) {
			itemStack = player.getStackInHand(hand);
		}
		return itemStack;
	}

	public Item getItem() {
		return getItemStack().getItem();
	}

	public boolean isActiveItem() {
		return player.getActiveItem().equals(itemStack);
	}

	public boolean canPlaceItemAsBlock() {
		IBlockItemMixin blockItem = (IBlockItemMixin) getItem();
		ItemPlacementContext itemPlacementContext = new ItemPlacementContext(player, hand, getItemStack(), (BlockHitResult) hitResult);
		BlockState blockState = blockItem.invokeGetPlacementState(itemPlacementContext);
		return (blockState != null && blockItem.invokeCanPlace(itemPlacementContext, blockState));
	}

	public boolean canUseWeaponAsTool() {
		return isWithBlock() && DynamicCrosshair.config.dynamicCrosshairHoldingTool() != CrosshairPolicy.Disabled;
	}


	private ApiList apiList = null;

	public List<DynamicCrosshairApi> apis() {
		if (apiList == null) {
			apiList = new ApiList();
			apiList.add(getItemStack());
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
