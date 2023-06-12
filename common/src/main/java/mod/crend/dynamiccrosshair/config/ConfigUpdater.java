package mod.crend.dynamiccrosshair.config;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class ConfigUpdater implements mod.crend.yaclx.opt.ConfigUpdater {
	private Stream<Item> buildAdditionalItemList(String configString) {
		return Arrays.stream(configString.split(";"))
				.filter(s -> !s.isBlank())
				.map(Identifier::tryParse)
				.filter(Objects::nonNull)
				.map(Registries.ITEM::get);
	}

	private boolean updateAdditionalItems(JsonObject json, String name) {
		if (!json.has(name)) {
			return false;
		}
		JsonElement additionalItems = json.get(name);
		if (additionalItems.isJsonPrimitive()) {
			JsonArray items = new JsonArray();
			buildAdditionalItemList(additionalItems.getAsString()).forEach(item -> items.add(Registries.ITEM.getId(item).toString()));
			json.remove(name);
			json.add(name, items);
			return true;
		}
		return false;
	}

	private boolean updateBooleanToEnum(JsonObject json) {
		if (json.has("dynamicCrosshair")) {
			JsonElement dynamicCrosshair = json.get("dynamicCrosshair");
			if (!(dynamicCrosshair instanceof JsonPrimitive dynamicCrosshairP) || dynamicCrosshairP.isBoolean()) {
				json.add("dynamicCrosshair", new JsonPrimitive(CrosshairMode.Advanced.toString()));
				return true;
			}
		}
		return false;
	}

	public boolean updateConfigFile(JsonObject json) {
		boolean result = updateBooleanToEnum(json);
		if (updateAdditionalItems(json, "additionalTools")) {
			result = true;
		}
		if (updateAdditionalItems(json, "additionalMeleeWeapons")) {
			result = true;
		}
		if (updateAdditionalItems(json, "additionalRangedWeapons")) {
			result = true;
		}
		if (updateAdditionalItems(json, "additionalThrowables")) {
			result = true;
		}
		if (updateAdditionalItems(json, "additionalUsableItems")) {
			result = true;
		}
		return result;
	}
}
