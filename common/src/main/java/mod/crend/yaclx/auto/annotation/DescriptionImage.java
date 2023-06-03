package mod.crend.yaclx.auto.annotation;

import dev.isxander.yacl.gui.ImageRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DescriptionImage {
	Class<? extends DescriptionImageRendererFactory<?>> value();

	interface DescriptionImageRendererFactory<T> {
		ImageRenderer create(T value);
	}
}
