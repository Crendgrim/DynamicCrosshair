package mod.crend.yaclx.auto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnableIf {
	String field();
	Class<? extends Predicate> value();

	interface Predicate {
		boolean isEnabled(Object value);
	}
}
