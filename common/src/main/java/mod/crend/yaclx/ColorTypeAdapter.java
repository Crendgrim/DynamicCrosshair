package mod.crend.yaclx;

import com.google.gson.*;

import java.awt.Color;
import java.lang.reflect.Type;

public class ColorTypeAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
	public ColorTypeAdapter() {
	}

	public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
		return new Color(jsonElement.getAsInt(), true);
	}

	public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
		return new JsonPrimitive(color.getRGB());
	}
}
