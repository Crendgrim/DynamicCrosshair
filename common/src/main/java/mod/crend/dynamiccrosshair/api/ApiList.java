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
		if (itemStack != null) {
			add(getNamespace(itemStack));
		}
		return this;
	}

	public ApiList add(BlockState blockState) {
		if (blockState != null) {
			add(getNamespace(blockState));
		}
		return this;
	}

	public ApiList add(Entity entity) {
		if (entity != null) {
			add(getNamespace(entity));
		}
		return this;
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
