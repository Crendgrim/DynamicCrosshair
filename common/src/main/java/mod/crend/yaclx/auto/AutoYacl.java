package mod.crend.yaclx.auto;

import com.google.common.collect.Lists;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.api.controller.*;
import dev.isxander.yacl.config.ConfigEntry;
import mod.crend.yaclx.ItemOrTag;
import mod.crend.yaclx.auto.annotation.*;
import mod.crend.yaclx.controller.DecoratedEnumController;
import mod.crend.yaclx.controller.DropdownStringController;
import mod.crend.yaclx.controller.ItemController;
import mod.crend.yaclx.controller.ItemOrTagController;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
		protected static <T> OptionDescription buildDescription(T value, String modId, String key, Field field, boolean group) {
			var description = OptionDescription.createBuilder()
					.text(Text.translatable(getDescriptionTranslationKey(modId, key, field, group)));
			DescriptionImage descriptionImage = field.getAnnotation(DescriptionImage.class);
			if (descriptionImage != null) {
				try {
					DescriptionImage.DescriptionImageRendererFactory<T> factory = (DescriptionImage.DescriptionImageRendererFactory<T>) descriptionImage.value().getConstructor().newInstance();
					description.customImage(CompletableFuture.completedFuture(Optional.of(factory.create(value))));
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			}
			return description.build();
		}
		protected void setCommonAttributes(Option.Builder<?> optionBuilder, String key, Field field) {
			setCommonAttributes(modId, optionBuilder, key, field, dependencies);
		}
		protected static void setCommonAttributes(String modId, Option.Builder<?> optionBuilder, String key, Field field, Map<String, List<EnableIf>> dependencies) {
			String translationKey = getTranslationKey(modId, key, field, false);
			optionBuilder.name(Text.translatable(translationKey));
			optionBuilder.description(value -> buildDescription(value, modId, key, field, false));
			EnableIf[] enableIfList = field.getAnnotationsByType(EnableIf.class);
			if (enableIfList.length > 0) {
				dependencies.put(key, Arrays.asList(enableIfList));
			}
			Listener[] listeners = field.getAnnotationsByType(Listener.class);
			for (Listener listener : listeners) {
				try {
					Listener.Callback callback = listener.value().getConstructor().newInstance();
					optionBuilder.listener((opt, value) -> callback.call(key, value));
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
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
				Wrapper transitiveWrapper = new Wrapper(modId, containingBuilder, field.get(bDefaults), field.get(bParent), bDummyConfig == null ? null : field.get(bDummyConfig), groups, options, dependencies);
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

			Option.Builder<?> optionBuilder = fromType(field, bDefaults, bParent, bDummyConfig);
			if (optionBuilder != null) {
				setCommonAttributes(optionBuilder, key, field);
				options.put(key, optionBuilder.build());
				containingBuilder.option(options.get(key));
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
		protected static <T> BiConsumer<Option<T>, T> makeListener(Field field, @Nullable Object dummy) {
			if (dummy == null) return (opt, val) -> {};
			return (opt, val) -> {
				try {
					field.set(dummy, val);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			};
		}
		protected static <T> BiConsumer<Option<T>, T> makeListener(Field field, @Nullable Object dummy, boolean reversed) {
			if (dummy == null) return (opt, val) -> {};
			return (opt, val) -> {
				try {
					System.err.println("reverse: " + reversed);
					if (reversed) field.set(dummy, Lists.reverse((List<?>) val));
					field.set(dummy, val);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			};
		}
		// Special method to handle null values for e.g. Strings
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
		@SuppressWarnings("unchecked")
		protected static <T> Binding<List<T>> makeListBinding(Field field, Object defaults, Object parent, boolean reverse) {
			try {
				if (reverse)
					return Binding.generic(Lists.reverse((List<T>) field.get(defaults)),
							() -> {
								try {
									List<T> obj = (List<T>) field.get(parent);
									return Lists.reverse(obj == null ? Collections.emptyList() : obj);
								} catch (ReflectiveOperationException e) {
									throw new RuntimeException(e);
								}
							},
							val -> {
								try {
									field.set(parent, Lists.reverse(val));
								} catch (IllegalAccessException e) {
									throw new RuntimeException(e);
								}
							}
					);
				else
					return Binding.generic(((List<T>) field.get(defaults)),
							() -> {
								try {
									List<T> obj = (List<T>) field.get(parent);
									return (obj == null ? Collections.emptyList() : obj);
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

		public static Option.Builder<?> createOptionBuilder(String modId, String key, Field field, Object defaults, Object parent, @Nullable Object dummy) {
			assert(field.getDeclaringClass().isInstance(defaults));
			assert(field.getDeclaringClass().isInstance(parent));
			Option.Builder<?> optionBuilder = fromType(field, defaults, parent, dummy);
			if (optionBuilder != null) {
				setCommonAttributes(modId, optionBuilder, key, field, new LinkedHashMap<>());
			}
			return optionBuilder;
		}

		private static Function<Option<Boolean>, ControllerBuilder<Boolean>> getBooleanController(Field field) {
			return TickBoxControllerBuilder::create;
		}
		private static Function<Option<Integer>, ControllerBuilder<Integer>> getIntegerController(Field field) {
			IntegerRange range = field.getAnnotation(IntegerRange.class);
			if (range != null) {
				return (opt -> IntegerSliderControllerBuilder.create(opt)
						.range(range.min(), range.max())
						.step(range.interval())
				);
			}
			return IntegerFieldControllerBuilder::create;
		}
		private static Function<Option<Long>, ControllerBuilder<Long>> getLongController(Field field) {
			LongRange range = field.getAnnotation(LongRange.class);
			if (range != null) {
				return (opt -> LongSliderControllerBuilder.create(opt)
						.range(range.min(), range.max())
						.step(range.interval())
				);
			}
			return LongFieldControllerBuilder::create;
		}
		private static Function<Option<Float>, ControllerBuilder<Float>> getFloatController(Field field) {
			FloatRange range = field.getAnnotation(FloatRange.class);
			if (range != null) {
				return (opt -> FloatSliderControllerBuilder.create(opt)
						.range(range.min(), range.max())
						.step(range.interval())
				);
			}
			return FloatFieldControllerBuilder::create;
		}
		private static Function<Option<Double>, ControllerBuilder<Double>> getDoubleController(Field field) {
			DoubleRange range = field.getAnnotation(DoubleRange.class);
			if (range != null) {
				return (opt -> DoubleSliderControllerBuilder.create(opt)
						.range(range.min(), range.max())
						.step(range.interval())
				);
			}
			return DoubleFieldControllerBuilder::create;
		}
		private static Function<Option<String>, ControllerBuilder<String>> getStringController(Field field) {
			StringOptions stringOptions = field.getAnnotation(StringOptions.class);

			if (stringOptions != null) {
				return (opt ->
						DropdownStringController.DropdownControllerBuilder.create(opt)
								.allowedValues(stringOptions.options())
				);
			}

			return StringControllerBuilder::create;
		}
		@SuppressWarnings("unchecked")
		private static <T extends Enum<T>> Function<Option<T>, ControllerBuilder<T>> getEnumController(Field field, Class<?> clazz) {
			Decorate decorate = field.getAnnotation(Decorate.class);
			if (decorate != null) {
				if (!DecoratedEnumController.Decorator.class.isAssignableFrom(decorate.decorator())) {
					throw new RuntimeException("Decorator must be of type Decorator<T>!");
				}
				try {
					DecoratedEnumController.Decorator<T> decorator = (DecoratedEnumController.Decorator<T>) decorate.decorator().getConstructor().newInstance();
					return (opt -> DecoratedEnumController.DecoratedEnumControllerBuilder.create(opt)
							.enumClass((Class<T>) clazz)
							.valueFormatter(NameableEnum.getEnumFormatter())
							.decorator(decorator)
					);
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			} else {
				return (opt -> EnumControllerBuilder.create(opt)
						.enumClass((Class<T>) clazz)
						.valueFormatter(NameableEnum.getEnumFormatter())
				);
			}
		}
		private static Function<Option<Color>, ControllerBuilder<Color>> getColorController(Field field) {
			return (opt -> ColorControllerBuilder.create(opt)
					.allowAlpha(true)
			);
		}
		private static Function<Option<Item>, ControllerBuilder<Item>> getItemController(Field field) {
			return ItemController.ItemControllerBuilder::create;
		}
		private static Function<Option<ItemOrTag>, ControllerBuilder<ItemOrTag>> getItemOrTagController(Field field) {
			return ItemOrTagController.ItemOrTagControllerBuilder::create;
		}

		@SuppressWarnings("unchecked")
		private static Option.Builder<?> fromType(Field field, Object defaults, Object parent, @Nullable Object dummy) {
			Class<?> type = field.getType();

			if (type.equals(boolean.class)) {

				return Option.<Boolean>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getBooleanController(field));

			} else if (type.equals(int.class)) {

				return Option.<Integer>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getIntegerController(field));

			} else if (type.equals(long.class)) {

				return Option.<Long>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getLongController(field));

			} else if (type.equals(float.class)) {

				return Option.<Float>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getFloatController(field));

			} else if (type.equals(double.class)) {

				return Option.<Double>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getDoubleController(field));

			} else if (type.equals(String.class)) {

				return Option.<String>createBuilder()
						.binding(makeNullableTypeBinding(String.class, field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getStringController(field));

			} else if (type.isEnum()) {

				return Option.<Enum>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getEnumController(field, field.getType()));

			} else if (type.equals(Color.class)) {

				return Option.<Color>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getColorController(field));

			} else if (type.equals(Item.class)) {

				return Option.<Item>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getItemController(field));

			} else if (type.equals(ItemOrTag.class)) {

				return Option.<ItemOrTag>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getItemOrTagController(field));

			}
			return null;
		}

		@SuppressWarnings("unchecked")
		protected static ListOption.Builder<?> fromListType(Class<?> type, Field field, Object defaults, Object parent, @Nullable Object dummy, boolean reverse) {
			if (type.equals(boolean.class)) {

				return ListOption.<Boolean>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getBooleanController(field));

			} else if (type.equals(int.class)) {

				return ListOption.<Integer>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getIntegerController(field));

			} else if (type.equals(long.class)) {

				return ListOption.<Long>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getLongController(field));

			} else if (type.equals(float.class)) {

				return ListOption.<Float>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getFloatController(field));

			} else if (type.equals(double.class)) {

				return ListOption.<Double>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.controller(getDoubleController(field));

			} else if (type.equals(String.class)) {

				return ListOption.<String>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.initial("")
						.controller(getStringController(field));

			} else if (type.isEnum()) {

				return ListOption.<Enum>createBuilder()
						.binding(makeListBinding(field, defaults, parent, reverse))
						.listener(makeListener(field, dummy, reverse))
						.initial((Enum) type.getEnumConstants()[0])
						.controller(getEnumController(field, (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]));

			} else if (type.equals(Color.class)) {

				return ListOption.<Color>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.initial(Color.BLACK)
						.controller(getColorController(field));

			} else if (type.equals(Item.class)) {

				return ListOption.<Item>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.initial(Items.AIR)
						.controller(getItemController(field));

			} else if (type.equals(ItemOrTag.class)) {

				return ListOption.<ItemOrTag>createBuilder()
						.binding(makeBinding(field, defaults, parent))
						.listener(makeListener(field, dummy))
						.initial(new ItemOrTag(Items.AIR))
						.controller(getItemOrTagController(field));
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
				var builder = fromListType((Class<?>) innerType, field, bDefaults, bParent, bDummyConfig, field.isAnnotationPresent(Reverse.class));
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

	public AutoYacl<T> registerOptionHandler() {
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
	@SuppressWarnings("unchecked")
	public <S> Option.Builder<S> makeOption(String key) {
		try {
			return (Option.Builder<S>) Wrapper.createOptionBuilder(modId, key, configClass.getField(key), defaults, config, dummyConfig);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
