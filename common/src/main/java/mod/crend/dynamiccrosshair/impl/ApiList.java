package mod.crend.dynamiccrosshair.impl;

import mod.crend.dynamiccrosshair.DynamicCrosshairMod;
import mod.crend.dynamiccrosshair.api.DynamicCrosshairApi;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public class ApiList {
	private final List<DynamicCrosshairApi> apis = new LinkedList<>();
	boolean finalized = false;

	public ApiList() { }

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
			if (DynamicCrosshairMod.apis.containsKey(namespace)) {
				DynamicCrosshairApi api = DynamicCrosshairMod.apis.get(namespace);
				if (!apis.contains(api)) {
					apis.add(api);
				}
			}
		}
		return this;
	}

	public List<DynamicCrosshairApi> get() {
		if (!finalized) {
			for (String namespace : DynamicCrosshairMod.alwaysCheckedApis) {
				apis.add(DynamicCrosshairMod.apis.get(namespace));
			}
			apis.add(DynamicCrosshairMod.vanillaApi);
			finalized = true;
		}
		return apis;
	}

	private static String getNamespace(ItemStack itemStack) {
		return Registries.ITEM.getId(itemStack.getItem()).getNamespace();
	}
	private static String getNamespace(BlockState blockState) {
		return Registries.BLOCK.getId(blockState.getBlock()).getNamespace();
	}
	private static String getNamespace(Entity entity) {
		return Registries.ENTITY_TYPE.getId(entity.getType()).getNamespace();
	}

}
