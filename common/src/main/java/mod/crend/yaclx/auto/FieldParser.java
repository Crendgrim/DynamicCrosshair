package mod.crend.yaclx.auto;

import dev.isxander.yacl.api.*;
import mod.crend.yaclx.auto.annotation.*;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

record FieldParser<T>(
		String modId,
		String key,
		Field field,
		Object defaults,
		Object parent,
		Object dummy,
		boolean isGroup
) {
	@SuppressWarnings("unchecked")
	public Class<T> getType() {
		return (Class<T>) field.getType();
	}

	public String getTranslationKey() {
		Translation translationKey = field.getAnnotation(Translation.class);
		return (translationKey == null
				? modId + (isGroup ? ".group." : ".option.") + key
				: translationKey.key());
	}

	public String getDescriptionTranslationKey() {
		Translation translationKey = field.getAnnotation(Translation.class);
		return (translationKey == null || translationKey.description().isBlank()
				? getTranslationKey() + ".description"
				: translationKey.description());
	}

	private OptionDescription buildDescription(T value) {
		var description = OptionDescription.createBuilder()
				.text(Text.translatable(getDescriptionTranslationKey()));
		DescriptionImage descriptionImage = field.getAnnotation(DescriptionImage.class);
		if (descriptionImage != null) {
			try {
				@SuppressWarnings("unchecked")
				DescriptionImage.DescriptionImageRendererFactory<T> factory
						= (DescriptionImage.DescriptionImageRendererFactory<T>) descriptionImage.value().getConstructor().newInstance();
				description.customImage(CompletableFuture.completedFuture(Optional.of(factory.create(value))));
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
		return description.build();
	}

	public void setCommonAttributes(Option.Builder<T> optionBuilder, Map<String, List<EnableIf>> dependencies) {
		optionBuilder.name(Text.translatable(getTranslationKey()));
		optionBuilder.description(this::buildDescription);
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

	public void setCommonAttributes(ListOption.Builder<T> optionBuilder) {
		optionBuilder.name(Text.translatable(getTranslationKey()));
		optionBuilder.description(OptionDescription.createBuilder()
				.text(Text.translatable(getDescriptionTranslationKey()))
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

	public void setCommonAttributes(OptionGroup.Builder optionGroupBuilder) {
		optionGroupBuilder.name(Text.translatable(getTranslationKey()));
		optionGroupBuilder.description(OptionDescription.createBuilder()
				.text(Text.translatable(getDescriptionTranslationKey()))
				.build()
		);
	}

	public Option.Builder<T> optionBuilder() {
		return optionBuilder(new LinkedHashMap<>());
	}

	public Option.Builder<T> optionBuilder(Map<String, List<EnableIf>> dependencies) {
		Option.Builder<T> optionBuilder = TypedController.fromType(this);
		if (optionBuilder != null) {
			setCommonAttributes(optionBuilder, dependencies);
			return optionBuilder;
		}
		return null;
	}

	public ListOption.Builder<T> listOptionBuilder(Class<T> clazz, boolean reverse) {
		ListOption.Builder<T> optionBuilder = TypedController.fromListType(clazz, this, reverse);
		if (optionBuilder != null) {
			setCommonAttributes(optionBuilder);
			return optionBuilder;
		}
		return null;
	}

	private BindingHelper<T> getBinding() {
		return BindingHelper.create(field);
	}

	private BindingHelper<T> getNullableBinding() {
		return BindingHelper.create(field, getType());
	}

	private BindingHelper<List<T>> getListBinding(boolean reverse) {
		return BindingHelper.create(field, reverse);
	}

	public Binding<T> makeBinding() {
		return getBinding().makeBinding(defaults, parent);
	}

	public Binding<T> makeNullableBinding() {
		return getNullableBinding().makeBinding(defaults, parent);
	}

	public Binding<List<T>> makeListBinding(boolean reverse) {
		return getListBinding(reverse).makeBinding(defaults, parent);
	}

	public BiConsumer<Option<T>, T> makeListener() {
		return getBinding().makeListener(dummy);
	}

	public BiConsumer<Option<List<T>>, List<T>> makeListListener() {
		return getListBinding(false).makeListener(dummy);
	}

}
