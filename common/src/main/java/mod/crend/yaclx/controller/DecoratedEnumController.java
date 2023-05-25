package mod.crend.yaclx.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.cycling.CyclingControllerElement;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import mod.crend.yaclx.auto.NameableEnum;
import mod.crend.yaclx.controller.annotation.Decorate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.function.Function;

public class DecoratedEnumController <T extends Enum<T>> extends EnumController<T> {
	Decorate.Decorator<T> renderer;
	public DecoratedEnumController(Option<T> option, Decorate.Decorator<T> renderer) {
		this(option, renderer, NameableEnum.getEnumFormatter());
	}
	public DecoratedEnumController(Option<T> option, Decorate.Decorator<T> renderer, Function<T, Text> valueFormatter) {
		super(option, valueFormatter);
		this.renderer = renderer;
	}

	public void callRenderer(DrawContext graphics, int x, int y) {
		renderer.render(option().pendingValue(), graphics, x, y);
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new DecoratedEnumElement(this, screen, widgetDimension);
	}

	public static class DecoratedEnumElement extends CyclingControllerElement {
		DecoratedEnumController<?> decoratedEnumController;

		public DecoratedEnumElement(DecoratedEnumController<?> control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
			this.decoratedEnumController = control;
		}

		@Override
		public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
			super.render(graphics, mouseX, mouseY, delta);
			decoratedEnumController.callRenderer(graphics, getDimension().xLimit() - getControlWidth(), getDimension().y() + 2);
		}

		@Override
		protected int getHoveredControlWidth() {
			return super.getHoveredControlWidth();
		}

		@Override
		protected int getUnhoveredControlWidth() {
			return super.getUnhoveredControlWidth() + 24;
		}
	}
}
