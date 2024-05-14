package mod.crend.dynamiccrosshair.api.internal;

import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApiBlockState;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApiEntityType;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApiItemStack;

import java.util.function.Function;

public interface ContextedApi extends DynamicCrosshairApiBlockState, DynamicCrosshairApiEntityType, DynamicCrosshairApiItemStack {
	boolean test(Function<DynamicCrosshairApi, Boolean> lambda);
}
