//? if fabric {
package mod.crend.dynamiccrosshairapi.crosshair;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

@Deprecated
public class CrosshairFluidContext {

	@Deprecated
	public static boolean canInteractWithFluidStorage(CrosshairContext context, Storage<FluidVariant> storage) {
		return context.canInteractWithFluidStorage(storage);
	}

}
//?}
