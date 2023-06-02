package mod.crend.yaclx.auto.annotation;

import java.lang.annotation.*;

@Repeatable(EnableIf.EnableIfList.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnableIf {
	String field();
	Class<? extends Predicate> value();

	interface Predicate {
		boolean isEnabled(Object value);
	}

	class BooleanPredicate implements Predicate {
		@Override
		public boolean isEnabled(Object value) {
			return value == Boolean.valueOf(true);
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface EnableIfList {
		EnableIf[] value();
	}

}

