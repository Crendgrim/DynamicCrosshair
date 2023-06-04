package mod.crend.yaclx.auto;

import dev.isxander.yacl.api.*;
import dev.isxander.yacl.config.ConfigEntry;
import mod.crend.yaclx.auto.annotation.*;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Automagic config UI generation from a config file based on annotations.
 *
 * @see AutoYacl#parse
 */
public class AutoYacl <T> {

	/**
	 * A wrapper can add fields to the current option builder, but no nested groups.
	 */
	protected static class Wrapper {
		protected final OptionAddable builder;
		protected final Object bDefaults;
		protected final Object bParent;
		protected final @Nullable Object bDummyConfig;
		protected final Map<String, OptionGroup.Builder> groups;
		protected final Map<String, Option<?>> options;
		protected final Map<String, List<EnableIf>> dependencies;
		protected final String modId;
		
		protected Wrapper(String modId, OptionAddable builder, Object bDefaults, Object bParent, @Nullable Object bDummyConfig, Map<String, OptionGroup.Builder> groups, Map<String, Option<?>> options, Map<String, List<EnableIf>> dependencies) {
			this.builder = builder;
			this.bDefaults = bDefaults;
			this.bParent = bParent;
			this.bDummyConfig = bDummyConfig;
			this.groups = groups;
			this.modId = modId;
			this.options = options;
			this.dependencies = dependencies;
		}
		
		OptionAddable getContainingBuilder(Field field) {
			Category category = field.getAnnotation(Category.class);
			if (category != null && !category.group().isBlank()) {
				if (!groups.containsKey(category.group())) {
					groups.put(category.group(), OptionGroup.createBuilder().name(Text.translatable(modId + ".group." + category.group())));
				}
				return groups.get(category.group());
			}
			return builder;
		}

		protected Option<?> findDependantOption(String key, EnableIf enableIf) {
			Option<?> opt = options.get(key.substring(0, key.lastIndexOf('.') + 1) + enableIf.field());
			if (opt != null) return opt;
			opt = options.get(enableIf.field());
			if (opt != null) return opt;
			throw new RuntimeException("Could not find dependant field " + enableIf.field() + " for field " + key + ": Neither '" + enableIf.field() + "' nor '" + key.substring(0, key.lastIndexOf('.') + 1) + enableIf.field() + "' are known.");
		}
		protected void computeDependencies() {
			dependencies.forEach((key, list) -> {
				Option<?> dependingOption = options.get(key);

				var captureList = list.stream()
						.map(enableIf -> {
							Option<?> depend = findDependantOption(key, enableIf);
							try {
								return new Pair<>(depend, enableIf.value().getConstructor().newInstance());
							} catch (ReflectiveOperationException e) {
								throw new RuntimeException(e);
							}
						})
						.toList();
				dependingOption.setAvailable(captureList.stream().allMatch(p -> p.getRight().isEnabled(p.getLeft().pendingValue())));
				for (var pair : captureList) {
					pair.getLeft().addListener((opt, val) -> dependingOption.setAvailable(captureList.stream().allMatch(p -> p.getRight().isEnabled(p.getLeft().pendingValue()))));
				}
			});
		}

		protected void registerMemberFields(Wrapper transitiveWrapper, String key, Field field) {
			Order order = field.getType().getAnnotation(Order.class);
			Set<String> fieldOrder;
			if (order != null) {
				fieldOrder = new HashSet<>(List.of(order.value()));
				for (String fieldName : order.value()) {
					try {
						transitiveWrapper.register(fieldName, field.getType().getField(fieldName));
					} catch (NoSuchFieldException e) {
						throw new RuntimeException(e);
					}
				}
			} else {
				fieldOrder = new HashSet<>();
			}
			for (Field memberField : field.getType().getFields()) {
				if (!fieldOrder.contains(memberField.getName())) {
					transitiveWrapper.register(key + "." + memberField.getName(), memberField);
				}
			}
		}
		
		protected <T> void registerObject(OptionAddable containingBuilder, String key, Field field) {
			// register object transitively
			try {
				Wrapper transitiveWrapper = new Wrapper(modId, containingBuilder, field.get(bDefaults), field.get(bParent), bDummyConfig == null ? null : field.get(bDummyConfig), groups, options, dependencies);
				registerMemberFields(transitiveWrapper, key, field);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		protected <T> void register(String key, Field field) {
			OptionAddable containingBuilder = getContainingBuilder(field);
			
			Label label = field.getAnnotation(Label.class);
			if (label != null) {
				containingBuilder.option(LabelOption.create(Text.translatable(label.key())));
			}

			FieldParser<T> fieldParser = new FieldParser<>(modId, key, field, bDefaults, bParent, bDummyConfig, false);
			Option.Builder<T> option = fieldParser.optionBuilder(dependencies);
			if (option != null) {
				options.put(key, option.build());
				containingBuilder.option(options.get(key));
			} else {
				registerObject(containingBuilder, key, field);
			}
		}

		public static <T> Option.Builder<T> createOptionBuilder(String modId, String key, Field field, Object defaults, Object parent, @Nullable Object dummy) {
			assert(field.getDeclaringClass().isInstance(defaults));
			assert(field.getDeclaringClass().isInstance(parent));
			FieldParser<T> fieldParser = new FieldParser<>(modId, key, field, defaults, parent, dummy, false);
			return fieldParser.optionBuilder();
		}


	}

	/**
	 * A category wrapper can, in addition to fields, also add objects as and fields to groups.
	 * Use @Category(group=) and @TransitiveObject to fine tune groupings.
	 */
	public static class CategoryWrapper extends Wrapper {
		protected final List<ListOption<?>> listOptions = new ArrayList<>();
		public CategoryWrapper(String modId, OptionAddable builder, Object bDefaults, Object bParent, @Nullable Object bDummyConfig) {
			super(modId, builder, bDefaults, bParent, bDummyConfig, new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
		}
		private CategoryWrapper(String modId, OptionAddable builder, Object bDefaults, Object bParent, @Nullable Object bDummyConfig, Map<String, OptionGroup.Builder> groups, Map<String, Option<?>> options, Map<String, List<EnableIf>> dependencies) {
			super(modId, builder, bDefaults, bParent, bDummyConfig, groups, options, dependencies);
		}

		protected void registerObjectTransitively(OptionAddable containingBuilder, String key, Field field) {
			// register object transitively
			try {
				Wrapper transitiveWrapper = new CategoryWrapper(modId, containingBuilder, field.get(bDefaults), field.get(bParent), bDummyConfig == null ? null : field.get(bDummyConfig), groups, options, dependencies);
				registerMemberFields(transitiveWrapper, key, field);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected <T> void registerObject(OptionAddable containingBuilder, String key, Field field) {
			if (field.isAnnotationPresent(TransitiveObject.class)) {
				registerObjectTransitively(containingBuilder, key, field);
				return;
			}
			if (containingBuilder != builder) {
				super.registerObject(containingBuilder, key, field);
				return;
			}
			if (field.getType().equals(List.class)) {
				ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
				@SuppressWarnings("unchecked")
				Class<T> innerType = (Class<T>) parameterizedType.getActualTypeArguments()[0];
				FieldParser<T> fieldParser = new FieldParser<>(modId, key, field, bDefaults, bParent, bDummyConfig, false);
				ListOption.Builder<T> builder = fieldParser.listOptionBuilder(innerType, field.isAnnotationPresent(Reverse.class));
				if (builder != null) {
					listOptions.add(builder.build());
				}
				return;
			}

			if (!groups.containsKey(key)) {
				groups.put(key, OptionGroup.createBuilder());
			}
			FieldParser<T> fieldParser = new FieldParser<>(modId, key, field, bDefaults, bParent, bDummyConfig, true);
			var groupBuilder = groups.get(key);
			fieldParser.setCommonAttributes(groupBuilder);
			try {
				Wrapper groupWrapper = new Wrapper(modId, groupBuilder, field.get(bDefaults), field.get(bParent), bDummyConfig == null ? null : field.get(bDummyConfig), groups, options, dependencies);
				for (Field memberField : field.getType().getFields()) {
					groupWrapper.register(key + "." + memberField.getName(), memberField);
				}

			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		public ConfigCategory build() {
			ConfigCategory.Builder categoryBuilder = (ConfigCategory.Builder) builder;
			for (var list : listOptions) {
				categoryBuilder.group(list);
			}
			for (var group : groups.values()) {
				categoryBuilder.group(group.build());
			}
			computeDependencies();
			return categoryBuilder.build();
		}
	}

	private static <T> CategoryWrapper wrapBuilder(String modId, String categoryName, T defaults, T config, @Nullable T dummyConfig) {
		return new CategoryWrapper(
				modId,
				ConfigCategory.createBuilder().name(Text.translatable(modId + ".category." + categoryName)),
				defaults,
				config,
				dummyConfig);
	}

	/**
	 * Parses the given config class's annotations and generates a YACL config UI.
	 *
	 * <p>You can use it in your screen factory as follows:
	 * <pre>
	 * public class ConfigScreenFactory {
	 *     public static Screen makeScreen(Screen parent) {
	 *         return YetAnotherConfigLib.create(Config.CONFIG_STORE.withYacl().instance,
	 *             (defaults, config, builder) -> AutoYacl.parse(Config.class, defaults, config, builder)
	 *         ).generateScreen(parent);
	 *     }
	 * }
	 * </pre>
	 *
	 * @param configClass the class referring to T
	 * @param defaults default config, to revert options to default from
	 * @param config current config
	 * @param builder YACL screen builder. Should be empty at first and will return buildable.
	 * @return The builder after every field has been added to it.
	 * @param <T> config class
	 */
	@SuppressWarnings("unused")
	public static <T> YetAnotherConfigLib.Builder parse(Class<?> configClass, T defaults, T config, YetAnotherConfigLib.Builder builder) {
		return new AutoYacl<T>(configClass, defaults, config).parse(builder);
	}

	private final Class<T> configClass;
	private final T defaults;
	private final T config;
	private final String modId;
	private @Nullable T dummyConfig = null;

	/**
	 * Instance this class to get a dynamic builder from which you may create individual options.
	 *
	 * @param configClass the class referring to T
	 * @param defaults default config, to revert options to default from
	 * @param config current config
	 */
	@SuppressWarnings("unchecked")
	public AutoYacl(Class<?> configClass, T defaults, T config) {
		this.configClass = (Class<T>) configClass;
		this.defaults = defaults;
		this.config = config;
		AutoYaclConfig ayc = configClass.getAnnotation(AutoYaclConfig.class);
		this.modId = ayc.modid();
	}

	public AutoYacl<T> dummyConfig(T dummyConfig) {
		this.dummyConfig = dummyConfig;
		return this;
	}

	public YetAnotherConfigLib.Builder parse(YetAnotherConfigLib.Builder builder) {
		AutoYaclConfig ayc = configClass.getAnnotation(AutoYaclConfig.class);
		String modId = ayc.modid();
		Text modTitle = Text.translatable(ayc.translationKey());
		CategoryWrapper categoryMain = wrapBuilder(modId, "general", defaults, config, dummyConfig);
		Map<String, CategoryWrapper> categories = new LinkedHashMap<>();
		for (Field field : configClass.getFields()) {
			if (field.isAnnotationPresent(ConfigEntry.class)) {
				Category category = field.getAnnotation(Category.class);
				if (category == null) {
					categoryMain.register(field.getName(), field);
				} else {
					if (!categories.containsKey(category.name())) {
						categories.put(category.name(), wrapBuilder(modId, category.name(), defaults, config, dummyConfig));
					}
					categories.get(category.name()).register(field.getName(), field);
				}
			}
		}
		builder.category(categoryMain.build());
		for (var entry : categories.values()) {
			builder.category(entry.build());
		}
		return builder.title(modTitle);
	}

	/**
	 * Only create an option from the field with the given key. All annotations except for @Category and @Label will be
	 * used to configure the option builder.
	 *
	 * @param key the name of the field in the config class
	 * @return an option builder that can be built or further configured
	 * @param <S> the type of the field
	 */
	@SuppressWarnings("unused")
	public <S> Option.Builder<S> makeOption(String key) {
		try {
			return Wrapper.createOptionBuilder(modId, key, configClass.getField(key), defaults, config, dummyConfig);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
