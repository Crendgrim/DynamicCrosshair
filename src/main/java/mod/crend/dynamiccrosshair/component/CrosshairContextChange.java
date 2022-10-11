package mod.crend.dynamiccrosshair.component;

import net.minecraft.util.hit.HitResult;

public class CrosshairContextChange extends RuntimeException {
	HitResult newHitResult;

	public CrosshairContextChange(HitResult hitResult) {
		newHitResult = hitResult;
	}
}
