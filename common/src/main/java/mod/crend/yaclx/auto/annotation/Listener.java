package mod.crend.yaclx.auto.annotation;

import java.lang.annotation.*;

@Repeatable(Listener.Listeners.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Listener {
	Class<? extends Callback> value();

	interface Callback {
		void call(String key, Object value);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface Listeners {
		Listener[] value();
	}
}
