package mod.crend.dynamiccrosshairapi.internal;

import mod.crend.dynamiccrosshairapi.DynamicCrosshairApi;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApiBlockState;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApiEntityType;
import mod.crend.dynamiccrosshairapi.DynamicCrosshairApiItemStack;

import java.util.function.Function;

public interface ContextedApi extends DynamicCrosshairApiBlockState, DynamicCrosshairApiEntityType, DynamicCrosshairApiItemStack {
	boolean test(Function<DynamicCrosshairApi, Boolean> lambda);
}
