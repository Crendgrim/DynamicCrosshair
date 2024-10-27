package mod.crend.dynamiccrosshair.config;

import com.google.gson.*;
import mod.crend.dynamiccrosshairapi.registry.DynamicCrosshairStyles;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class ConfigUpdater implements mod.crend.libbamboo.opt.ConfigUpdater {
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
			if (!(dynamicCrosshair instanceof JsonPrimitive dynamicCrosshairP) || !dynamicCrosshairP.isBoolean()) {
				if (dynamicCrosshair.getAsString().equals("Disabled")) json.add("dynamicCrosshair", new JsonPrimitive(false));
				else json.add("dynamicCrosshair", new JsonPrimitive(true));
				return true;
			}
		}
		return false;
	}

	private boolean updateColor(JsonObject json) {
		if (json.has("color")) {
			JsonElement colorEl = json.get("color");
			if (!colorEl.isJsonObject()) {
				json.remove("color");
			}
			JsonObject color = colorEl.getAsJsonObject();
			if (color.has("crosshairColor")) {
				// Old config before big rewrite
				boolean useCustomColor = !color.get("crosshairColor").getAsString().equals("Unchanged");
				int customColor = switch (color.get("crosshairColor").getAsString()) {
					case "Red" -> 0xFFFF0000;
					case "Yellow" -> 0xFFAAAA00;
					case "Green" -> 0xFF00FF00;
					case "Cyan" -> 0xFF00AAAA;
					case "Blue" -> 0xFF0000FF;
					case "Purple" -> 0xFFAA00AA;
					default -> 0xFFAABBCC;
				};
				boolean enableBlend = !color.get("forceColor").getAsBoolean();
				JsonObject newColor = new JsonObject();
				newColor.add("overrideColor", new JsonPrimitive(useCustomColor));
				newColor.add("customColor", new JsonPrimitive(customColor));
				newColor.add("enableBlend", new JsonPrimitive(enableBlend));
				json.remove("color");
				json.add("color", newColor);
				updateStyles(json);
				return true;
			}
		}
		return false;
	}

	private JsonObject updateStyle(JsonElement value) {
		if (value instanceof JsonObject obj && obj.has("style")) {
			Identifier identifier = switch (obj.get("style").getAsString()) {
				case "Cross" -> DynamicCrosshairStyles.CROSS_OPEN;
				case "DiagonalCross" -> DynamicCrosshairStyles.CROSS_OPEN_DIAGONAL;
				case "Circle" -> DynamicCrosshairStyles.CIRCLE;
				case "CircleLarge" -> DynamicCrosshairStyles.CIRCLE_LARGE;
				case "Square" -> DynamicCrosshairStyles.SQUARE;
				case "Diamond" -> DynamicCrosshairStyles.DIAMOND_LARGE;
				case "Caret" -> DynamicCrosshairStyles.CARET;
				case "Dot" -> DynamicCrosshairStyles.DOT;
				case "SmallDiagonalCross" -> DynamicCrosshairStyles.CROSS_DIAGONAL_SMALL;
				case "Brackets" -> DynamicCrosshairStyles.BRACKETS;
				case "BracketsBottom" -> DynamicCrosshairStyles.BRACKETS_BOTTOM;
				case "BracketsTop" -> DynamicCrosshairStyles.BRACKETS_TOP;
				case "RoundBrackets" -> DynamicCrosshairStyles.BRACKETS_ROUND;
				case "Lines" -> DynamicCrosshairStyles.LINES;
				case "LineBottom" -> DynamicCrosshairStyles.LINE_BOTTOM;
				default -> DynamicCrosshairStyles.DEFAULT;
			};
			obj.remove("style");
			obj.add("style", new JsonPrimitive(identifier.toString()));
			if (obj.get("color") instanceof JsonObject color) {
				boolean useCustomColor = !color.get("crosshairColor").getAsString().equals("Unchanged");
				int customColor = switch (color.get("crosshairColor").getAsString()) {
					case "Red" -> 0xFFFF0000;
					case "Yellow" -> 0xFFAAAA00;
					case "Green" -> 0xFF00FF00;
					case "Cyan" -> 0xFF00AAAA;
					case "Blue" -> 0xFF0000FF;
					case "Purple" -> 0xFFAA00AA;
					default -> 0xFFAABBCC;
				};
				boolean enableBlend = !color.get("forceColor").getAsBoolean();
				JsonObject newColor = new JsonObject();
				newColor.add("overrideColor", new JsonPrimitive(useCustomColor));
				newColor.add("customColor", new JsonPrimitive(customColor));
				newColor.add("enableBlend", new JsonPrimitive(enableBlend));
				obj.remove("color");
				obj.add("color", newColor);
			}
			return obj;
		}
		return null;
	}
	private void updateStyles(JsonObject json) {
		if (json.get("crosshairStyle") instanceof JsonObject style && json.get("crosshairModifiers") instanceof JsonObject modifiers) {
			style.asMap().forEach((key, value) -> {
				JsonObject newObject = updateStyle(value);
				if (newObject != null) {
					style.add(key, newObject);
				}
			});
			if (modifiers.get("modInteractable") instanceof JsonObject modInteractable) {
				JsonObject newMod = updateStyle(modInteractable);
				if (newMod != null) {
					json.get("crosshairStyle").getAsJsonObject().add("interact", newMod);
				}
				modifiers.remove("modInteractable");
			}
			if (modifiers.get("modUsableItem") instanceof JsonObject modUsableItem) {
				JsonObject newMod = updateStyle(modUsableItem);
				if (newMod != null) {
					json.get("crosshairStyle").getAsJsonObject().add("useItem", newMod);
				}
				modifiers.remove("modUsableItem");
			}
			if (modifiers.get("modShield") instanceof JsonObject modShield) {
				JsonObject newMod = updateStyle(modShield);
				if (newMod != null) {
					json.get("crosshairStyle").getAsJsonObject().add("shield", newMod);
				}
				modifiers.remove("modShield");
			}
			if (modifiers.get("modCorrectTool") instanceof JsonObject modCorrectTool) {
				JsonObject newMod = updateStyle(modCorrectTool);
				if (newMod != null) {
					modifiers.remove("modCorrectTool");
					modifiers.add("modCorrectTool", newMod);
				}
				modifiers.remove("modInteractable");
			}
			if (modifiers.get("modIncorrectTool") instanceof JsonObject modIncorrectTool) {
				JsonObject newMod = updateStyle(modIncorrectTool);
				if (newMod != null) {
					modifiers.remove("modIncorrectTool");
					modifiers.add("modIncorrectTool", newMod);
				}
				modifiers.remove("modInteractable");
			}
		} else {
			json.remove("crosshairStyle");
			json.remove("crosshairModifiers");
		}
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
		if (updateColor(json)) {
			result = true;
		}
		return result;
	}
}
