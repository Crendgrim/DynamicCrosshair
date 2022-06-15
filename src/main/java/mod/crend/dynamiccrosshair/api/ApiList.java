package mod.crend.dynamiccrosshair.api;

import mod.crend.dynamiccrosshair.DynamicCrosshair;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedList;
import java.util.List;

public class ApiList {
	private final List<DynamicCrosshairApi> apis = new LinkedList<>();
	boolean finalized = false;

	ApiList() { }

	public ApiList add(ItemStack itemStack) {
		return add(getNamespace(itemStack));
	}

	public ApiList add(BlockState blockState) {
		return add(getNamespace(blockState));
	}

	public ApiList add(Entity entity) {
		return add(getNamespace(entity));
	}

	public ApiList add(String namespace) {
		if (!Identifier.DEFAULT_NAMESPACE.equals(namespace)) {
			if (DynamicCrosshair.apis.containsKey(namespace)) {
				apis.add(DynamicCrosshair.apis.get(namespace));
			}
		}
		return this;
	}

	public List<DynamicCrosshairApi> get() {
		if (!finalized) {
			for (String namespace : DynamicCrosshair.alwaysCheckedApis) {
				apis.add(DynamicCrosshair.apis.get(namespace));
			}
			apis.add(DynamicCrosshair.vanillaApi);
			finalized = true;
		}
		return apis;
	}

	private static String getNamespace(ItemStack itemStack) {
		return Registry.ITEM.getId(itemStack.getItem()).getNamespace();
	}
	private static String getNamespace(BlockState blockState) {
		return Registry.BLOCK.getId(blockState.getBlock()).getNamespace();
	}
	private static String getNamespace(Entity entity) {
		return Registry.ENTITY_TYPE.getId(entity.getType()).getNamespace();
	}

}
