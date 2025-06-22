package mod.crend.dynamiccrosshairapi.crosshair;

import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import mod.crend.dynamiccrosshairapi.interaction.InteractionType;
import mod.crend.dynamiccrosshairapi.internal.ContextedApi;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public interface CrosshairContext {

	ClientWorld getWorld();
	ClientPlayerEntity getPlayer();
	HitResult getHitResult();

	void invalidateHitResult(HitResult newHitResult);

	void invalidateItem(Hand hand);

	boolean isTargeting();

	boolean isEmptyHanded();

	/**
	 * @return true if player is not crouching, or if they are crouching but both hands are empty
	 */
	boolean shouldInteract();

	boolean isFlying();


	boolean isWithBlock();

	BlockPos getBlockPos();

	BlockState getBlockState();

	default Block getBlock() {
		return getBlockState().getBlock();
	}

	BlockEntity getBlockEntity();

	FluidState getFluidState();

	BlockHitResult getBlockHitResult();

	Direction getBlockHitSide();

	default ItemUsageContext getItemUsageContext() {
		return new ItemUsageContext(getPlayer(), getHand(), getBlockHitResult());
	}

	BlockHitResult raycastWithFluid(RaycastContext.FluidHandling fluidHandling);
	default BlockHitResult raycastWithFluid() {
		return raycastWithFluid(RaycastContext.FluidHandling.ANY);
	}

	EntityHitResult raycastForEntity(double d);

	boolean isWithEntity();
	Entity getEntity();

	Hand getHand();
	void setHand(Hand hand) ;
	boolean isMainHand();
	boolean isOffHand();

	ItemStack getItemStack(Hand hand);
	default ItemStack getItemStack() {
		return getItemStack(getHand());
	}

	default Item getItem() {
		return getItemStack().getItem();
	}

	boolean isActiveItem();

	boolean isCoolingDown();

	boolean canPlaceItemAsBlock();

	boolean canUseWeaponAsTool();

	boolean includeUsableItem();
	boolean includeThrowable();
	boolean includeRangedWeapon();
	boolean includeMeleeWeapon();
	boolean includeTool();
	boolean includeShield();
	boolean includeHoldingBlock();


	List<DynamicCrosshairApi> apis();

	ContextedApi api();

	<R> @Nullable R withApisUntilNonNull(Function<DynamicCrosshairApi, R> lambda);

	InteractionType checkToolWithBlock();
}
