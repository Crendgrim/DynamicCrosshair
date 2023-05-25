package mod.crend.yaclx.auto;

import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;

import java.util.function.Function;

public interface NameableEnum {
	Text getDisplayName();

	static <T extends Enum<T>> Function<T, Text> getEnumFormatter() {
		return value -> {
			if (value instanceof NameableEnum nameableEnum)
				return nameableEnum.getDisplayName();
			if (value instanceof TranslatableOption translatableOption)
				return translatableOption.getText();
			return Text.literal(value.toString());
		};
	}
}
