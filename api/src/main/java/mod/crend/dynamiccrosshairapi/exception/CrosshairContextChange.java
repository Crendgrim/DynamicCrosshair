package mod.crend.dynamiccrosshairapi.exception;

import net.minecraft.util.hit.HitResult;

public class CrosshairContextChange extends RuntimeException {
	public final HitResult newHitResult;

	public CrosshairContextChange(HitResult hitResult) {
		newHitResult = hitResult;
	}
}
