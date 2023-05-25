package mod.crend.yaclx.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.string.IStringController;
import dev.isxander.yacl.gui.controllers.string.StringControllerElement;
import dev.isxander.yacl.gui.utils.GuiUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public abstract class AbstractDropdownController<T> implements IStringController<T> {
	protected final Option<T> option;
	private final List<String> allowedValues;

	/**
	 * Constructs a dropdown controller
	 *
	 * @param option bound option
	 * @param allowedValues possible values
	 */
	protected AbstractDropdownController(Option<T> option, List<String> allowedValues) {
		this.option = option;
		this.allowedValues = allowedValues;
	}

	protected AbstractDropdownController(Option<T> option) {
		this(option, Collections.emptyList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Option<T> option() {
		return option;
	}

	public List<String> getAllowedValues() {
		return allowedValues;
	}

	public boolean isValueValid(String value) {
		return getAllowedValues().contains(value);
	}

	String getValidValue(String value) {
		if (isValueValid(value)) {
			return value;
		}
		return getAllowedValues().stream()
				.filter(val -> val.toLowerCase().contains(value.toLowerCase()))
				.sorted()
				.findFirst()
				.orElseGet(this::getString);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new DropdownControllerElement<>(this, screen, widgetDimension);
	}

	public static class DropdownControllerElement <T> extends StringControllerElement {
		private final AbstractDropdownController<T> dropdownController;
		private boolean wasFocused = false;

		public DropdownControllerElement(AbstractDropdownController<T> control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim, false);
			this.dropdownController = control;
		}


		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (super.mouseClicked(mouseX, mouseY, button)) {
				if (!wasFocused) {
					wasFocused = true;
					doSelectAll();
				}
				return true;
			}
			return false;
		}

		@Override
		public void setFocused(boolean focused) {
			if (focused) super.setFocused(true);
			else unfocus();
		}

		@Override
		public void unfocus() {
			wasFocused = false;
			if (!dropdownController.isValueValid(inputField)) {
				inputField = dropdownController.getValidValue(inputField);
			}
			super.unfocus();
		}


		@Override
		protected int getValueColor() {
			if (inputFieldFocused) {
				if (!dropdownController.isValueValid(inputField)) {
					return 0xFFF06080;
				}
			}
			return super.getValueColor();
		}

		@Override
		protected void drawValueText(DrawContext graphics, int mouseX, int mouseY, float delta) {
			super.drawValueText(graphics, mouseX, mouseY, delta);
		}

		public List<String> getMatchingValues() {
			return dropdownController.getAllowedValues().stream()
					.filter(this::matchingValue)
					.sorted()
					.toList();
		}
		public boolean matchingValue(String value) {
			return value.toLowerCase().contains(inputField.toLowerCase());
		}

		@Override
		public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
			super.render(graphics, mouseX, mouseY, delta);

			if (inputFieldFocused) {
				MatrixStack matrices = graphics.getMatrices();
				matrices.push();
				matrices.translate(0, 0, 200);
				renderDropdown(graphics);
				matrices.pop();
			}
		}

		public void renderDropdown(DrawContext graphics) {
			int n = 1;
			List<String> options = getMatchingValues();
			renderDropdownBackground(graphics, options.size());
			if (options.size() >= 1) {
				graphics.drawBorder(getDimension().x() + 20, getDimension().yLimit() + 2, getDimension().width() - 20, getDimension().height(), -1);
			}
			for (String value : options) {
				Text text = Text.literal(GuiUtils.shortenString(value, textRenderer, getDimension().width() - 20, "..."));
				renderOption(graphics, text, n);
				++n;
			}
		}

		public void renderDropdownBackground(DrawContext graphics, int numberOfItems) {
			graphics.setShaderColor(0.25f, 0.25f, 0.25f, 1.0f);
			graphics.drawTexture(Screen.OPTIONS_BACKGROUND_TEXTURE, getDimension().x() + 20, getDimension().yLimit() + 2, 0, 0.0f, 0.0f, getDimension().width() - 20, getDimension().height() * numberOfItems + 2, 32, 32);
			graphics.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			graphics.drawBorder(getDimension().x() + 20, getDimension().yLimit() + 2, getDimension().width() - 20, getDimension().height() * numberOfItems, -1);
		}

		public void renderOption(DrawContext graphics, Text text, int n) {
			graphics.drawText(textRenderer, text, getDimension().xLimit() - textRenderer.getWidth(text) - getXPadding(), getTextY() + n * getDimension().height() + 2, -1, true);
		}

	}
}
