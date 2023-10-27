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

	private boolean updateDynamicCrosshairMode(JsonObject json) {
		if (json.has("dynamicCrosshair")) {
			JsonElement dynamicCrosshair = json.get("dynamicCrosshair");
			if (!(dynamicCrosshair instanceof JsonPrimitive dynamicCrosshairP) || dynamicCrosshairP.isBoolean()) {
				json.add("dynamicCrosshair", new JsonPrimitive(CrosshairMode.Advanced.toString()));
				return true;
			}
		}
		return false;
	}
	private boolean updateBlockTargeting(JsonObject json) {
		if (json.get("crosshairConfig") instanceof JsonObject crosshairConfig) {
			if (crosshairConfig.get("onBlock") instanceof JsonPrimitive onBlock && !onBlock.isBoolean()) {
				switch (onBlock.getAsString()) {
					case "IfTargeting" -> {
						crosshairConfig.add("onBlock", new JsonPrimitive(true));
						crosshairConfig.add("onInteractableBlock", new JsonPrimitive(true));
					}
					case "IfInteractable" -> {
						crosshairConfig.add("onBlock", new JsonPrimitive(false));
						crosshairConfig.add("onInteractableBlock", new JsonPrimitive(true));
					}
					case "Disabled" -> {
						crosshairConfig.add("onBlock", new JsonPrimitive(false));
						crosshairConfig.add("onInteractableBlock", new JsonPrimitive(false));
					}
					default -> crosshairConfig.add("onBlock", new JsonPrimitive(true));
				}
				return true;
			}
		}
		return false;
	}

	private boolean updateDisabledModifierStyle(JsonObject json) {
		boolean result = false;
		if (json.get("crosshairModifiers") instanceof JsonObject crosshairModifiers) {
			if (crosshairModifiers.get("modInteractable") instanceof JsonObject modInteractable) {
				if (modInteractable.get("style") instanceof JsonPrimitive style && style.getAsString().equals("Disabled")) {
					if (json.get("crosshairConfig") instanceof JsonObject crosshairConfig) {
						crosshairConfig.add("onInteractableBlock", new JsonPrimitive(false));
					}
					result = true;
				}
			}
			if (crosshairModifiers.get("modCorrectTool") instanceof JsonObject modCorrectTool) {
				if (modCorrectTool.get("style") instanceof JsonPrimitive style && style.getAsString().equals("Disabled")) {
					if (json.get("crosshairConfig") instanceof JsonObject crosshairConfig) {
						crosshairConfig.add("displayCorrectTool", new JsonPrimitive(false));
					}
					result = true;
				}
			}
			if (crosshairModifiers.get("modIncorrectTool") instanceof JsonObject modIncorrectTool) {
				if (modIncorrectTool.get("style") instanceof JsonPrimitive style && style.getAsString().equals("Disabled")) {
					if (json.get("crosshairConfig") instanceof JsonObject crosshairConfig) {
						crosshairConfig.add("displayCorrectTool", new JsonPrimitive(false));
					}
					result = true;
				}
			}
		}
		return result;
	}

	public boolean updateConfigFile(JsonObject json) {
		boolean result = updateDynamicCrosshairMode(json);
		if (updateBlockTargeting(json)) {
			result = true;
		}
		if (updateDisabledModifierStyle(json)) {
			result = true;
		}
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
