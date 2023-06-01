package mod.crend.yaclx.auto;

import dev.isxander.yacl.api.*;
import dev.isxander.yacl.api.controller.*;
import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.gui.controllers.ColorController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.slider.LongSliderController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import dev.isxander.yacl.gui.controllers.string.number.DoubleFieldController;
import dev.isxander.yacl.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import dev.isxander.yacl.gui.controllers.string.number.LongFieldController;
import dev.isxander.yacl.impl.controller.DoubleSliderControllerBuilderImpl;
import mod.crend.yaclx.ItemOrTag;
import mod.crend.yaclx.auto.annotation.*;
import mod.crend.yaclx.controller.DecoratedEnumController;
import mod.crend.yaclx.controller.DropdownStringController;
import mod.crend.yaclx.controller.ItemController;
import mod.crend.yaclx.controller.ItemOrTagController;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
		protected final Map<String, OptionGroup.Builder> groups;
		protected final Map<String, Option<?>> options;
		protected final String modId;
		
		protected Wrapper(String modId, OptionAddable builder, Object bDefaults, Object bParent, Map<String, OptionGroup.Builder> groups) {
			this.builder = builder;
			this.bDefaults = bDefaults;
			this.bParent = bParent;
			this.groups = groups;
			this.modId = modId;
			this.options = new HashMap<>();
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

		protected static String getTranslationKey(String modId, String key, Field field, boolean group) {
			Translation translationKey = field.getAnnotation(Translation.class);
			return (translationKey == null
					? modId + (group ? ".group." : ".option.") + key
					: translationKey.key());
		}
		protected static String getDescriptionTranslationKey(String modId, String key, Field field, boolean group) {
			Translation translationKey = field.getAnnotation(Translation.class);
			return (translationKey == null || translationKey.description().isBlank()
					? getTranslationKey(modId, key, field, group) + ".description"
					: translationKey.description());
		}
		protected static void setCommonAttributes(String modId, Option.Builder<?> optionBuilder, String key, Field field, Map<String, Option<?>> options) {
			String translationKey = getTranslationKey(modId, key, field, false);
			optionBuilder.name(Text.translatable(translationKey));
			optionBuilder.description(OptionDescription.createBuilder()
					.text(Text.translatable(getDescriptionTranslationKey(modId, key, field, false)))
					.build()
			);
			EnableIf enableIf = field.getAnnotation(EnableIf.class);
			if (enableIf != null) {
				Option<?> depend = options.get(enableIf.field());
				if (depend != null) {
					depend.addListener((option, o) -> {
						try {
							options.get(field.getName()).setAvailable(enableIf.value().getConstructor().newInstance().isEnabled(o));
						} catch (ReflectiveOperationException e) {
							throw new RuntimeException(e);
						}
					});
					try {
						optionBuilder.available(enableIf.value().getConstructor().newInstance().isEnabled(depend.pendingValue()));
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				} else {
					throw new RuntimeException("Could not find dependant field " + enableIf.field() + " for field " + key);
				}
			}
			OnSave onSave = field.getAnnotation(OnSave.class);
			if (onSave != null) {
				if (onSave.gameRestart()) optionBuilder.flag(OptionFlag.GAME_RESTART);
				if (onSave.reloadChunks()) optionBuilder.flag(OptionFlag.RELOAD_CHUNKS);
				if (onSave.worldRenderUpdate()) optionBuilder.flag(OptionFlag.WORLD_RENDER_UPDATE);
				if (onSave.assetReload()) optionBuilder.flag(OptionFlag.ASSET_RELOAD);
			}
		}
		protected static void setCommonAttributes(String modId, ListOption.Builder<?> optionBuilder, String key, Field field) {
			String translationKey = getTranslationKey(modId, key, field, false);
			optionBuilder.name(Text.translatable(translationKey));
			optionBuilder.description(OptionDescription.createBuilder()
					.text(Text.translatable(getDescriptionTranslationKey(modId, key, field, false)))
					.build()
			);
			OnSave onSave = field.getAnnotation(OnSave.class);
			if (onSave != null) {
				if (onSave.gameRestart()) optionBuilder.flag(OptionFlag.GAME_RESTART);
				if (onSave.reloadChunks()) optionBuilder.flag(OptionFlag.RELOAD_CHUNKS);
				if (onSave.worldRenderUpdate()) optionBuilder.flag(OptionFlag.WORLD_RENDER_UPDATE);
				if (onSave.assetReload()) optionBuilder.flag(OptionFlag.ASSET_RELOAD);
			}
		}
		protected void setCommonAttributes(OptionGroup.Builder optionGroupBuilder, String key, Field field) {
			String translationKey = getTranslationKey(modId, key, field, true);
			optionGroupBuilder.name(Text.translatable(translationKey));
			optionGroupBuilder.description(OptionDescription.createBuilder()
					.text(Text.translatable(getDescriptionTranslationKey(modId, key, field, true)))
					.build()
			);
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
		
		protected void registerObject(OptionAddable containingBuilder, String key, Field field) {
			// register object transitively
			try {
				Wrapper transitiveWrapper = new Wrapper(modId, containingBuilder, field.get(bDefaults), field.get(bParent), groups);
				registerMemberFields(transitiveWrapper, key, field);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		protected void register(String key, Field field) {
			OptionAddable containingBuilder = getContainingBuilder(field);
			
			Label label = field.getAnnotation(Label.class);
			if (label != null) {
				containingBuilder.option(LabelOption.create(Text.translatable(label.key())));
			}

			Option.Builder<?> optionBuilder = fromType(field, bDefaults, bParent);
			if (optionBuilder != null) {
				setCommonAttributes(modId, optionBuilder, key, field, options);
				options.put(field.getName(), optionBuilder.build());
				containingBuilder.option(options.get(field.getName()));
			} else {
				registerObject(containingBuilder, key, field);
			}
		}

		@SuppressWarnings("unchecked")
		protected static <T> Binding<T> makeBinding(Field field, Object defaults, Object parent) {
			try {
				return Binding.generic((T) (field.get(defaults)),
					() -> {
						try {
							return (T) (field.get(parent));
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}, val -> {
						try {
							field.set(parent, val);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					});
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		@SuppressWarnings("unchecked")
		protected static <T> Binding<T> makeNullableTypeBinding(Class<T> clazz, Field field, Object defaults, Object parent) {
			try {
				return Binding.generic((T) field.get(defaults),
					() -> {
						try {
							T obj = (T) field.get(parent);
							return (obj == null ? clazz.getConstructor().newInstance() : obj);
						} catch (ReflectiveOperationException e) {
							throw new RuntimeException(e);
						}
					},
					val -> {
						try {
							field.set(parent, val);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
				);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		public static Option.Builder<?> createOptionBuilder(String modId, String key, Field field, Object defaults, Object parent) {
			assert(field.getDeclaringClass().isInstance(defaults));
			assert(field.getDeclaringClass().isInstance(parent));
			Option.Builder<?> optionBuilder = fromType(field, defaults, parent);
			if (optionBuilder != null) {
				setCommonAttributes(modId, optionBuilder, key, field, new HashMap<>());
			}
			return optionBuilder;
		}

		@SuppressWarnings("unchecked")
		private static Option.Builder<?> fromType(Field field, Object defaults, Object parent) {
			Class<?> type = field.getType();

			if (type.equals(boolean.class)) {

				return Option.<Boolean>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.controller(TickBoxControllerBuilder::create);

			} else if (type.equals(int.class)) {

				var builder = Option.<Integer>createBuilder()
						.binding(makeBinding(field, defaults, parent));
				IntegerRange range = field.getAnnotation(IntegerRange.class);
				if (range != null) {
					builder.controller(opt -> IntegerSliderControllerBuilder.create(opt)
							.range(range.min(), range.max())
							.step(range.interval())
					);
				} else {
					builder.controller(IntegerFieldControllerBuilder::create);
				}
				return builder;

			} else if (type.equals(long.class)) {

				var builder = Option.<Long>createBuilder()
						.binding(makeBinding(field, defaults, parent));
				LongRange range = field.getAnnotation(LongRange.class);
				if (range != null) {
					builder.controller(opt -> LongSliderControllerBuilder.create(opt)
							.range(range.min(), range.max())
							.step(range.interval())
					);
				} else {
					builder.controller(LongFieldControllerBuilder::create);
				}
				return builder;

			} else if (type.equals(float.class)) {

				var builder = Option.<Float>createBuilder()
						.binding(makeBinding(field, defaults, parent));
				FloatRange range = field.getAnnotation(FloatRange.class);
				if (range != null) {
					builder.controller(opt -> FloatSliderControllerBuilder.create(opt)
							.range(range.min(), range.max())
							.step(range.interval())
					);
				} else {
					builder.controller(FloatFieldControllerBuilder::create);
				}
				return builder;

			} else if (type.equals(double.class)) {

				var builder = Option.<Double>createBuilder()
						.binding(makeBinding(field, defaults, parent));
				DoubleRange range = field.getAnnotation(DoubleRange.class);
				if (range != null) {
					/*
					builder.controller(opt -> DoubleSliderControllerBuilder.create(opt)
							.range(range.min(), range.max())
							.step(range.interval())
					);
					 */
					builder.controller(opt -> new DoubleSliderControllerBuilderImpl(opt)
							.range(range.min(), range.max())
							.step(range.interval())
					);
				} else {
					builder.controller(DoubleFieldControllerBuilder::create);
				}
				return builder;

			} else if (type.equals(String.class)) {

				var builder = Option.<String>createBuilder()
						.binding(makeNullableTypeBinding(String.class, field, defaults, parent));

				StringOptions stringOptions = field.getAnnotation(StringOptions.class);

				if (stringOptions != null) {
					return builder.controller(opt ->
						DropdownStringController.DropdownControllerBuilder.create(opt)
								.allowedValues(stringOptions.options())
					);
				}

				return builder.controller(StringControllerBuilder::create);

			} else if (type.isEnum()) {

				var builder = Option.<Enum>createBuilder()
						.binding(makeBinding(field, defaults, parent));

				Decorate decorate = field.getAnnotation(Decorate.class);
				if (decorate != null) {
					if (!DecoratedEnumController.Decorator.class.isAssignableFrom(decorate.decorator())) {
						throw new RuntimeException("Decorator must be of type Decorator<T>!");
					}
					try {
						DecoratedEnumController.Decorator decorator = decorate.decorator().getConstructor().newInstance();
						builder.controller(opt -> DecoratedEnumController.DecoratedEnumControllerBuilder.create(opt)
								.enumClass((Class<Enum>) type)
								.valueFormatter(NameableEnum.getEnumFormatter())
								.decorator(decorator)
						);
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				} else {
					builder.controller(opt -> EnumControllerBuilder.create(opt)
							.enumClass((Class<Enum>) type)
							.valueFormatter(NameableEnum.getEnumFormatter())
					);
				}

				return builder;

			} else if (type.equals(Color.class)) {

				return Option.<Color>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.controller(opt -> ColorControllerBuilder.create(opt)
								.allowAlpha(true)
						);

			} else if (type.equals(Item.class)) {

				return Option.<Item>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.controller(ItemController.ItemControllerBuilder::create);

			} else if (type.equals(ItemOrTag.class)) {

				return Option.<ItemOrTag>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.controller(ItemOrTagController.ItemOrTagControllerBuilder::create);

			}
			return null;
		}

		protected static ListOption.Builder<?> fromListType(Class<?> type, Field field, Object defaults, Object parent) {

			if (type.equals(boolean.class)) {

				return ListOption.<Boolean>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.controller(TickBoxController::new);

			} else if (type.equals(int.class)) {

				var builder = ListOption.<Integer>createBuilder()
						.binding(makeBinding(field, defaults, parent));
				IntegerRange range = field.getAnnotation(IntegerRange.class);
				if (range != null) {
					builder.controller(opt -> new IntegerSliderController(opt, range.min(), range.max(), range.interval()));
				} else {
					builder.controller(IntegerFieldController::new);
				}
				return builder;

			} else if (type.equals(long.class)) {

				var builder = ListOption.<Long>createBuilder()
						.binding(makeBinding(field, defaults, parent));
				LongRange range = field.getAnnotation(LongRange.class);
				if (range != null) {
					builder.controller(opt -> new LongSliderController(opt, range.min(), range.max(), range.interval()));
				} else {
					builder.controller(LongFieldController::new);
				}
				return builder;

			} else if (type.equals(float.class)) {

				var builder = ListOption.<Float>createBuilder()
						.binding(makeBinding(field, defaults, parent));
				FloatRange range = field.getAnnotation(FloatRange.class);
				if (range != null) {
					builder.controller(opt -> new FloatSliderController(opt, range.min(), range.max(), range.interval()));
				} else {
					builder.controller(FloatFieldController::new);
				}
				return builder;

			} else if (type.equals(double.class)) {

				var builder = ListOption.<Double>createBuilder()
						.binding(makeBinding(field, defaults, parent));
				DoubleRange range = field.getAnnotation(DoubleRange.class);
				if (range != null) {
					builder.controller(opt -> new DoubleSliderController(opt, range.min(), range.max(), range.interval()));
				} else {
					builder.controller(DoubleFieldController::new);
				}
				return builder;

			} else if (type.equals(String.class)) {

				return ListOption.<String>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.initial("")
						.controller(StringController::new);

			} else if (type.isEnum()) {

				return ListOption.<Enum>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.controller(opt -> EnumControllerBuilder.create(opt)
								.enumClass((Class<Enum>) type)
								.valueFormatter(NameableEnum.getEnumFormatter())
								.build()
						);

			} else if (type.equals(Color.class)) {

				return ListOption.<Color>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.initial(Color.BLACK)
						.controller(opt -> new ColorController(opt, true));

			} else if (type.equals(Item.class)) {

				return ListOption.<Item>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.initial(Items.AIR)
						.controller(ItemController::new);

			} else if (type.equals(ItemOrTag.class)) {

				return ListOption.<ItemOrTag>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.initial(new ItemOrTag(Items.AIR))
						.controller(ItemOrTagController::new);

			}
			return null;
		}

	}

	/**
	 * A category wrapper can, in addition to fields, also add objects as and fields to groups.
	 * Use @Category(group=) and @TransitiveObject to fine tune groupings.
	 */
	public static class CategoryWrapper extends Wrapper {
		protected List<ListOption<?>> listOptions = new ArrayList<>();
		public CategoryWrapper(String modId, OptionAddable builder, Object bDefaults, Object bParent) {
			super(modId, builder, bDefaults, bParent, new LinkedHashMap<>());
		}
		private CategoryWrapper(String modId, OptionAddable builder, Object bDefaults, Object bParent, Map<String, OptionGroup.Builder> groups) {
			super(modId, builder, bDefaults, bParent, groups);
		}

		protected void registerObjectTransitively(OptionAddable containingBuilder, String key, Field field) {
			// register object transitively
			try {
				Wrapper transitiveWrapper = new CategoryWrapper(modId, containingBuilder, field.get(bDefaults), field.get(bParent), groups);
				registerMemberFields(transitiveWrapper, key, field);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		protected void registerObject(OptionAddable containingBuilder, String key, Field field) {
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
				Type innerType = parameterizedType.getActualTypeArguments()[0];
				var builder = fromListType((Class<?>) innerType, field, bDefaults, bParent);
				if (builder != null) {
					setCommonAttributes(modId, builder, key, field);
					listOptions.add(builder.build());
				}
				return;
			}

			if (!groups.containsKey(key)) {
				groups.put(key, OptionGroup.createBuilder());
			}
			var groupBuilder = groups.get(key);
			setCommonAttributes(groupBuilder, key, field);
			try {
				Wrapper groupWrapper = new Wrapper(modId, groupBuilder, field.get(bDefaults), field.get(bParent), groups);
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
			return categoryBuilder.build();
		}
	}

	private static <T> CategoryWrapper wrapBuilder(String modId, String categoryName, T defaults, T config) {
		return new CategoryWrapper(
				modId,
				ConfigCategory.createBuilder().name(Text.translatable(modId + ".category." + categoryName)),
				defaults,
				config);
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
	public static <T> YetAnotherConfigLib.Builder parse(Class<?> configClass, T defaults, T config, YetAnotherConfigLib.Builder builder) {
		AutoYaclConfig ayc = configClass.getAnnotation(AutoYaclConfig.class);
		String modId = ayc.modid();
		Text modTitle = Text.translatable(ayc.translationKey());
		CategoryWrapper categoryMain = wrapBuilder(modId, "general", defaults, config);
		Map<String, CategoryWrapper> categories = new LinkedHashMap<>();
		for (Field field : configClass.getFields()) {
			if (field.isAnnotationPresent(ConfigEntry.class)) {
				Category category = field.getAnnotation(Category.class);
				if (category == null) {
					categoryMain.register(field.getName(), field);
				} else {
					if (!categories.containsKey(category.name())) {
						categories.put(category.name(), wrapBuilder(modId, category.name(), defaults, config));
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

	private final Class<T> configClass;
	private final T defaults;
	private final T config;
	private final String modId;

	/**
	 * Instance this class to get a dynamic builder from which you may create individual options.
	 *
	 * @param configClass the class referring to T
	 * @param defaults default config, to revert options to default from
	 * @param config current config
	 */
	public AutoYacl(Class<T> configClass, T defaults, T config) {
		this.configClass = configClass;
		this.defaults = defaults;
		this.config = config;
		AutoYaclConfig ayc = configClass.getAnnotation(AutoYaclConfig.class);
		this.modId = ayc.modid();
	}

	/**
	 * Only create an option from the field with the given key. All annotations except for @Category and @Label will be
	 * used to configure the option builder.
	 *
	 * @param key the name of the field in the config class
	 * @return an option builder that can be built or further configured
	 * @param <S> the type of the field
	 */
	@SuppressWarnings("unchecked")
	public <S> Option.Builder<S> makeOption(String key) {
		try {
			return (Option.Builder<S>) Wrapper.createOptionBuilder(modId, key, configClass.getField(key), defaults, config);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
