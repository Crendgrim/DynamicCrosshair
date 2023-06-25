package mod.crend.autoyacl;

import com.google.gson.*;
import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;

import java.awt.Color;
import java.lang.reflect.Type;
import java.util.function.Function;

public class YaclHelper {
	public static final String YACL_MOD_ID = "yet_another_config_lib";
	public static final boolean HAS_YACL = PlatformUtils.isModLoaded("yet_another_config_lib");

	public static <T extends Enum<T>> Function<T, Text> getEnumFormatter() {
		return value -> {
			if (value instanceof NameableEnum nameableEnum)
				return nameableEnum.getDisplayName();
			if (value instanceof TranslatableOption translatableOption)
				return translatableOption.getText();
			return Text.literal(value.toString());
		};
	}

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
}
