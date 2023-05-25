package mod.crend.yaclx;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.awt.Color;
import java.lang.reflect.Type;

public class YaclxHelper {
	public static final boolean HAS_YACL = PlatformUtils.isModLoaded("yet_another_config_lib");

	public static class ColorTypeAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
		public ColorTypeAdapter() {
		}

		public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return new Color(jsonElement.getAsInt(), true);
		}

		public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(color.getRGB());
		}
	}

	public static boolean isRegisteredItem(String value) {
		String namespace, itemName;
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		value = value.toLowerCase();
		if (sep == -1) {
			namespace = Identifier.DEFAULT_NAMESPACE;
			itemName = value;
		} else {
			namespace = value.substring(0, sep);
			itemName = value.substring(sep + 1);
		}
		try {
			Identifier identifier = new Identifier(namespace, itemName);
			return Registries.ITEM.containsId(identifier);
		} catch (InvalidIdentifierException e) {
			return false;
		}
	}
	public static Item getItemFromName(String value, Item defaultItem) {
		String namespace, itemName;
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		value = value.toLowerCase();
		if (sep == -1) {
			namespace = Identifier.DEFAULT_NAMESPACE;
			itemName = value;
		} else {
			namespace = value.substring(0, sep);
			itemName = value.substring(sep + 1);
		}
		Identifier identifier = new Identifier(namespace, itemName);
		if (Registries.ITEM.containsId(identifier)) {
			return Registries.ITEM.get(identifier);
		} else {
			return defaultItem;
		}
	}
	public static Item getItemFromName(String value) {
		return getItemFromName(value, Items.AIR);
	}

	public static class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
		public ItemTypeAdapter() {
		}

		public Item deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			return getItemFromName(jsonElement.getAsString());
		}

		public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
			return new JsonPrimitive(Registries.ITEM.getId(item).toString());
		}
	}
}
