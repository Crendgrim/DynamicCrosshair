package mod.crend.yaclx.controller.annotation;

import net.minecraft.client.gui.DrawContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Decorate {
	Class<? extends Decorator<?>> decorator();

	interface Decorator <T> {
		void render(T value, DrawContext context, int x, int y);
	}
}
