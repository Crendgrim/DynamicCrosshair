package mod.crend.dynamiccrosshair.fabric.api;

import mod.crend.dynamiccrosshair.api.CrosshairContext;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class CrosshairFluidContext {

	@SuppressWarnings("UnstableApiUsage")
	public static boolean canInteractWithFluidStorage(CrosshairContext context, Storage<FluidVariant> storage) {
		Storage<FluidVariant> handStorage = ContainerItemContext.forPlayerInteraction(context.player, context.getHand()).find(FluidStorage.ITEM);
		if (handStorage == null) return false;

		try (var tx = Transaction.openOuter()) {
			return StorageUtil.move(storage, handStorage, fv -> true, Long.MAX_VALUE, tx) > 0 || StorageUtil.move(handStorage, storage, fv -> true, Long.MAX_VALUE, tx) > 0;
		}
	}

}
