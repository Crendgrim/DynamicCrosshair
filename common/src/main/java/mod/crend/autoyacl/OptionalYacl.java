package mod.crend.autoyacl;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl.config.GsonConfigInstance;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.Color;
import java.nio.file.Path;

/**
 * Config wrapper for YACL. This class should only be referenced in a context where it is ensured that YACL has been
 * loaded, such as your ConfigScreenFactory. Use YaclHelper and ConfigStore to get a safe wrapper.
 */
public class OptionalYacl <T> {
	public GsonConfigInstance<T> instance;

	public OptionalYacl(Class<T> configClass, Path path) {
		GsonBuilder gsonBuilder = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
				.setPrettyPrinting()
				.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
				.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
				.registerTypeHierarchyAdapter(Color.class, new GsonConfigInstance.ColorTypeAdapter())
				.serializeNulls();
		instance = GsonConfigInstance.createBuilder(configClass)
				.setPath(path)
				//.appendGsonBuilder(GsonBuilder::setPrettyPrinting)
				//.appendGsonBuilder(builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY))
				.overrideGsonBuilder(gsonBuilder)
				.build();

		instance.load();
	}

	public T getConfig() {
		return instance.getConfig();
	}

	public void save() {
		instance.save();
	}
}
