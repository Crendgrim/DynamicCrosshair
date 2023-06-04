package mod.crend.yaclx.auto;

import com.google.common.collect.Lists;
import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.Option;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;

class BindingHelper<T> {
	protected final Field field;

	protected BindingHelper(Field field) {
		this.field = field;
	}

	public static <T> BindingHelper<T> create(Field field) {
		return new BindingHelper<>(field);
	}

	public static <T> BindingHelper<T> create(Field field, Class<T> clazz) {
		return new NullableBindingHelper<>(field, clazz);
	}

	public static <T> BindingHelper<List<T>> create(Field field, boolean reverse) {
		return new ListBindingHelper<>(field, reverse);
	}

	@SuppressWarnings("unchecked")
	protected T get(Object obj) {
		try {
			return (T) field.get(obj);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	protected void set(Object obj, T value) {
		try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public Binding<T> makeBinding(Object defaults, Object parent) {
		return Binding.generic(
				get(defaults),
				() -> get(parent),
				value -> set(parent, value)
		);
	}

	public BiConsumer<Option<T>, T> makeListener(@Nullable Object dummy) {
		if (dummy == null) return (opt, val) -> { };
		return (opt, val) -> set(dummy, val);
	}

	static class NullableBindingHelper<T> extends BindingHelper<T> {
		private final Class<T> clazz;

		NullableBindingHelper(Field field, Class<T> clazz) {
			super(field);
			this.clazz = clazz;
		}

		public Binding<T> makeBinding(Object defaults, Object parent) {
			return Binding.generic(
					get(defaults),
					() -> {
						try {
							T obj = get(parent);
							return (obj == null ? clazz.getConstructor().newInstance() : obj);
						} catch (ReflectiveOperationException e) {
							throw new RuntimeException(e);
						}
					},
					value -> set(parent, value)
			);
		}
	}

	static class ListBindingHelper<T> extends BindingHelper<List<T>> {
		private final boolean reversed;

		ListBindingHelper(Field field, boolean reversed) {
			super(field);
			this.reversed = reversed;
		}

		public Binding<List<T>> makeBinding(Object defaults, Object parent) {
			if (reversed)
				return Binding.generic(
						Lists.reverse(get(defaults)),
						() -> {
							List<T> obj = get(parent);
							return Lists.reverse(obj == null ? List.of() : obj);
						},
						value -> set(parent, Lists.reverse(value))
				);
			else
				return Binding.generic(
						get(defaults),
						() -> {
							List<T> obj = get(parent);
							return (obj == null ? List.of() : obj);
						},
						value -> set(parent, value)
				);
		}

		public BiConsumer<Option<List<T>>, List<T>> makeListener(@Nullable Object dummy) {
			if (dummy == null) return (opt, val) -> { };
			return (opt, val) -> {
				if (reversed) set(dummy, Lists.reverse(val));
				else set(dummy, val);
			};
		}
	}

}
