package mod.crend.yaclx.auto;

import mod.crend.yaclx.auto.annotation.*;

import java.lang.reflect.Field;
import java.util.Objects;

public class ConfigValidator {
	@SuppressWarnings("unchecked")
	private static <T, U extends Comparable<U>> boolean validateRange(T config, Field field, U min, U max) throws IllegalAccessException {
		U value = (U) field.get(config);
		if (value.compareTo(min) < 0) {
			field.set(config, min);
			return false;
		}
		else if (value.compareTo(max) > 0) {
			field.set(config, max);
			return false;
		}
		return true;
	}

	private static <T> boolean validateOptions(T config, Field field, String[] options, boolean allowEmpty) throws IllegalAccessException {
		String value = (String) field.get(config);
		for (String option : options) {
			if (option.equals(value)) return true;
		}
		if (allowEmpty) field.set(config, "");
		else field.set(config, options[0]);
		return false;
	}

	/**
	 * Validate a config based on its fields' range annotations. Any values that are outside the range (and not the
	 * default value) get adjusted to be inside the range.
	 *
	 * @param configClass the annotated config class
	 * @param config the current config
	 * @return false if anything got adjusted, true otherwise
	 * @param <T> the type of the config class
	 */
	public static <T> boolean validate(Class<T> configClass, T config) {
		boolean configValid = true;
		try {
			T defaults = configClass.getDeclaredConstructor().newInstance();

			for (Field field : configClass.getFields()) {
				if (!validate(field, defaults, config)) {
					configValid = false;
				}
			}
		} catch (ReflectiveOperationException ignored) { }
		return configValid;
	}

	private static <U> boolean validate(Field field, U defaults, U config) throws IllegalAccessException {
		if (!Objects.equals(field.get(config), field.get(defaults))) {
			IntegerRange intRange = field.getAnnotation(IntegerRange.class);
			if (intRange != null) {
				return validateRange(config, field, intRange.min(), intRange.max());
			}
			LongRange longRange = field.getAnnotation(LongRange.class);
			if (longRange != null) {
				return validateRange(config, field, longRange.min(), longRange.max());
			}
			DoubleRange doubleRange = field.getAnnotation(DoubleRange.class);
			if (doubleRange != null) {
				return validateRange(config, field, doubleRange.min(), doubleRange.max());
			}
			FloatRange floatRange = field.getAnnotation(FloatRange.class);
			if (floatRange != null) {
				return validateRange(config, field, floatRange.min(), floatRange.max());
			}
			StringOptions stringOptions = field.getAnnotation(StringOptions.class);
			if (stringOptions != null) {
				return validateOptions(config, field, stringOptions.options(), stringOptions.allowEmpty());
			}
			boolean configValid = true;
			for (Field innerField : field.getType().getFields()) {
				if (!validate(innerField, field.get(defaults), field.get(config))) {
					configValid = false;
				}
			}
			return configValid;
		}
		return true;
	}
}
