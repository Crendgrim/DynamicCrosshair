package mod.crend.yaclx.opt;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.config.GsonConfigInstance;
import mod.crend.yaclx.ItemOrTag;
import mod.crend.yaclx.ItemOrTagTypeAdapter;
import mod.crend.yaclx.ItemTypeAdapter;
import mod.crend.yaclx.auto.AutoYacl;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.Color;
import java.nio.file.Path;

/**
 * Config wrapper for YACL. This class should only be referenced in a context where it is ensured that YACL has been
 * loaded, such as your ConfigScreenFactory. Use YaclX.HAS_YACL or ConfigStore to get a safe context.
 */
public class WithYacl<T> {
	public final GsonConfigInstance<T> instance;
	public final ConfigChangeListener configChangeListener = () -> { };
	public final AutoYacl<T> autoYacl;
	public T dummyConfig = null;

	public WithYacl(Class<T> configClass, Path path) {
		GsonBuilder gsonBuilder = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
				.setPrettyPrinting()
				.registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
				.registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
				.registerTypeHierarchyAdapter(Color.class, new GsonConfigInstance.ColorTypeAdapter())
				.registerTypeHierarchyAdapter(Item.class, new ItemTypeAdapter())
				.registerTypeHierarchyAdapter(ItemOrTag.class, new ItemOrTagTypeAdapter())
				.serializeNulls();
		instance = GsonConfigInstance.createBuilder(configClass)
				.setPath(path)
				//.appendGsonBuilder(GsonBuilder::setPrettyPrinting)
				//.appendGsonBuilder(builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY))
				.overrideGsonBuilder(gsonBuilder)
				.build();

		instance.load();

		autoYacl = new AutoYacl<>(configClass, instance.getDefaults(), instance.getConfig());
	}

	public T getConfig() {
		return instance.getConfig();
	}

	public void save() {
		instance.save();
	}

	public void registerDummyConfig(T dummyConfig) {
		this.dummyConfig = dummyConfig;
		autoYacl.dummyConfig(dummyConfig);
	}

	public YetAnotherConfigLib setupScreen() {
		return YetAnotherConfigLib.create(instance,
				(defaults, config, builder) -> autoYacl
						.parse(builder)
						.save(() -> {
							instance.save();
							configChangeListener.onConfigChange();
						})
		);
	}

	public Screen makeScreen(Screen parent) {
		return setupScreen().generateScreen(parent);
	}

	public interface ConfigChangeListener {
		void onConfigChange();
	}
}
