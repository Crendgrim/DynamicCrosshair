package mod.crend.yaclx;

import com.google.gson.*;
import net.minecraft.item.Items;

import java.lang.reflect.Type;

public class ItemOrTagTypeAdapter implements JsonSerializer<ItemOrTag>, JsonDeserializer<ItemOrTag> {
	public ItemOrTagTypeAdapter() {
	}

	public ItemOrTag deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		return ItemOrTag.fromString(jsonElement.getAsString(), false).orElseGet(() -> new ItemOrTag(Items.AIR));
	}

	public JsonElement serialize(ItemOrTag itemOrTag, Type type, JsonSerializationContext jsonSerializationContext) {
		return new JsonPrimitive(itemOrTag.toString());
	}
}
