package mod.crend.yaclx.opt;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import mod.crend.yaclx.*;
import mod.crend.yaclx.auto.ConfigValidator;
import mod.crend.yaclx.auto.annotation.AutoYaclConfig;
import net.minecraft.item.Item;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.Color;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Wraps the config reading by using YetAnotherConfigLib, if present, or directly deserializes it from the config file
 * otherwise. This allows for an optional dependency on YACL for GUI configuration.
 *
 * <p>Example Usage:
 * <pre>
 * static final ConfigStore&lt;Config&gt; CONFIG_STORE = new ConfigStore<>(Config.class);
 * </pre>
 *
 * And then access the config using {@code CONFIG_STORE.config()}.
 *
 * @param <T> config class
 */
public class ConfigStore<T> {

	// Static helpers to set up a GSON deserializer in the same way that YACL does.
	// This allows us to read a config in the absence of YACL.
	public static GsonBuilder getGsonBuilder() {
		return new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
				.setPrettyPrinting()
				.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
				.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
				.registerTypeHierarchyAdapter(Color.class, new ColorTypeAdapter())
				.registerTypeHierarchyAdapter(Item.class, new ItemTypeAdapter())
				.registerTypeHierarchyAdapter(ItemOrTag.class, new ItemOrTagTypeAdapter())
				.serializeNulls();
	}

	// This field is only populated if YACL is not loaded.
	// If YACL is loaded, we defer to its config storage in AutoYacl.
	private T configInstance;
	private WithYacl<T> yaclWrapper;
	private Path path;

	/**
	 * Sets up the config parsing, and takes the filename from the @AutoYaclConfig annotation on the specified config
	 * class.
	 * @param configClass the class referring to T, with fields marked as @ConfigEntry.
	 */
	public ConfigStore(Class<T> configClass) {
		this(configClass, (ConfigUpdater) null);
	}
	/**
	 * Sets up the config parsing, and takes the filename from the @AutoYaclConfig annotation on the specified config
	 * class.
	 * @param configClass the class referring to T, with fields marked as @ConfigEntry.
	 * @param updater a class specifying a config updater to prepare outdated configs before parsing.
	 */
	public ConfigStore(Class<T> configClass, ConfigUpdater updater) {
		AutoYaclConfig ayc = configClass.getAnnotation(AutoYaclConfig.class);
		if (ayc == null) {
			throw new RuntimeException("No file specified for config class " + configClass);
		}
		String filename = (ayc.filename().isBlank() ? ayc.modid() + ".json" : ayc.filename());
		Path path = PlatformUtils.resolveConfigFile(filename);

		init(configClass, path, updater);
		validate(configClass);
	}

	/**
	 * Sets up the config parsing, and takes the filename from the given argument..
	 * @param configClass the class referring to T, with fields marked as @ConfigEntry.
	 * @param path the path to the config file.
	 */
	public ConfigStore(Class<T> configClass, Path path) {
		init(configClass, path, null);
	}

	/**
	 * Sets up the config parsing, and takes the filename from the given argument..
	 * @param configClass the class referring to T, with fields marked as @ConfigEntry.
	 * @param path the path to the config file.
	 * @param updater a class specifying a config updater to prepare outdated configs before parsing.
	 */
	public ConfigStore(Class<T> configClass, Path path, ConfigUpdater updater) {
		init(configClass, path, updater);
	}

	private void createNewConfig(Class<T> configClass) {
		try {
			configInstance = configClass.getDeclaredConstructor().newInstance();
			save();
		} catch (ReflectiveOperationException f) {
			throw new RuntimeException(f);
		}
	}

	private void init(Class<T> configClass, Path path, ConfigUpdater updater) {
		this.path = path;
		if (updater != null) {
			try {
				JsonObject json = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
				if (updater.updateConfigFile(json)) {
					StringWriter stringWriter = new StringWriter();
					JsonWriter jsonWriter = new JsonWriter(stringWriter);
					jsonWriter.setIndent("  ");
					jsonWriter.setLenient(true);
					Streams.write(json, jsonWriter);
					Files.writeString(path, stringWriter.toString());
				}
			} catch (IOException e) {
				createNewConfig(configClass);
			}
		}
		if (YaclX.HAS_YACL) {
			yaclWrapper = new WithYacl<>(configClass, path);
		} else {
			try {
				configInstance = getGsonBuilder().create().fromJson(Files.readString(path), configClass);
			} catch (IOException e) {
				createNewConfig(configClass);
			}
		}
	}

	/**
	 * Access the configuration, either through YACL or static variable
	 * @return Config class object
	 */
	public T config() {
		if (YaclX.HAS_YACL) {
			return yaclWrapper.getConfig();
		} else {
			return configInstance;
		}
	}

	/**
	 * Saves the current configuration, either through YACL or static variable
	 */
	public void save() {
		if (YaclX.HAS_YACL) {
			yaclWrapper.save();
		} else {
			try {
				Files.writeString(path, getGsonBuilder().create().toJson(configInstance));
			} catch (IOException ignored) {
			}
		}
	}

	/**
	 * Validates the current configuration based on field annotations.
	 * Requires AutoYacl annotations to be present.
	 */
	public void validate(Class<T> configClass) {
		if (!ConfigValidator.validate(configClass, config())) {
			save();
		}
	}

	public WithYacl<T> withYacl() {
		assert(YaclX.HAS_YACL);
		return yaclWrapper;
	}
}
