package mod.crend.yaclx.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.string.IStringController;
import dev.isxander.yacl.gui.controllers.string.StringControllerElement;
import dev.isxander.yacl.gui.utils.GuiUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
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
		return getValidValue(value, 0);
	}
	String getValidValue(String value, int offset) {
		if (offset == -1) return getString();
		return getAllowedValues().stream()
				.filter(val -> val.toLowerCase().contains(value.toLowerCase()))
				.sorted()
				.skip(offset)
				.findFirst()
				.orElseGet(this::getString);
	}


	public static abstract class DropdownControllerElement <T, U> extends StringControllerElement {
		public static final int MAX_SHOWN_NUMBER_OF_ITEMS = 7;
		private final AbstractDropdownController<T> dropdownController;
		protected boolean opened = false;
		protected int selected = 0;

		public DropdownControllerElement(AbstractDropdownController<T> control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim, false);
			this.dropdownController = control;
		}

		public void open() {
			opened = true;
			selected = 0;
		}

		public void close() {
			opened = false;
			ensureValidValue();
		}

		protected int selected() {
			return selected;
		}

		public void ensureValidValue() {
			inputField = dropdownController.getValidValue(inputField, selected);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (super.mouseClicked(mouseX, mouseY, button)) {
				if (!opened) {
					open();
					doSelectAll();
				}
				return true;
			}
			return false;
		}

		@Override
		public void setFocused(boolean focused) {
			if (focused) {
				doSelectAll();
				super.setFocused(true);
			}
			else unfocus();
		}

		@Override
		public void unfocus() {
			close();
			super.unfocus();
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (!inputFieldFocused)
				return false;
			if (opened) {
				switch (keyCode) {
					case InputUtil.GLFW_KEY_DOWN -> {
						int len = getDropdownLength();
						selected = Math.min(selected + 1, len - 1);
						return true;
					}
					case InputUtil.GLFW_KEY_UP -> {
						selected = Math.max(selected - 1, 0);
						return true;
					}
				}
			} else {
				if (keyCode == InputUtil.GLFW_KEY_ENTER) {
					open();
					return true;
				}
			}
			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public boolean charTyped(char chr, int modifiers) {
			if (!opened) {
				open();
			}
			return super.charTyped(chr, modifiers);
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

		public int getDropdownLength() {
			return getMatchingValues().size();
		}

		public abstract List<U> getMatchingValues();

		public boolean matchingValue(String value) {
			return value.toLowerCase().contains(inputField.toLowerCase());
		}

		@Override
		public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
			super.render(graphics, mouseX, mouseY, delta);

			if (inputFieldFocused && opened) {
				MatrixStack matrices = graphics.getMatrices();
				matrices.push();
				matrices.translate(0, 0, 200);
				renderDropdown(graphics);
				matrices.pop();
			}
		}

		public void renderDropdown(DrawContext graphics) {
			List<U> options = getMatchingValues();
			if (options.size() == 0) return;
			// Limit the visible options to allow scrolling through the suggestion list
			int begin = Math.max(0, selected() - MAX_SHOWN_NUMBER_OF_ITEMS/2);
			int end = begin + MAX_SHOWN_NUMBER_OF_ITEMS;
			if (end >= options.size()) {
				end = options.size();
				begin = Math.max(0, end - MAX_SHOWN_NUMBER_OF_ITEMS);
			}

			renderDropdownBackground(graphics, end - begin);
			if (options.size() >= 1) {
				// Highlight the currently selected element
				graphics.drawBorder(
						getDimension().x() + 20,
						getDimension().yLimit() + 2 + getDimension().height() * (selected() - begin),
						getDimension().width() - 20,
						getDimension().height(),
						-1);
			}

			int n = 1;
			for (int i = begin; i < end; ++i) {
				renderOption(graphics, options.get(i), n);
				++n;
			}
		}

		protected void renderOption(DrawContext graphics, U value, int n) {
			Text text = shortenString(getString(value));
			renderOptionText(graphics, text, n);
		}

		public abstract String getString(U object);

		public Text shortenString(String value) {
			return Text.literal(GuiUtils.shortenString(value, textRenderer, getDimension().width() - 20, "..."));
		}

		public void renderDropdownBackground(DrawContext graphics, int numberOfItems) {
			graphics.setShaderColor(0.25f, 0.25f, 0.25f, 1.0f);
			graphics.drawTexture(Screen.OPTIONS_BACKGROUND_TEXTURE, getDimension().x() + 20, getDimension().yLimit() + 2, 0, 0.0f, 0.0f, getDimension().width() - 20, getDimension().height() * numberOfItems + 2, 32, 32);
			graphics.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
			graphics.drawBorder(getDimension().x() + 20, getDimension().yLimit() + 2, getDimension().width() - 20, getDimension().height() * numberOfItems, -1);
		}

		public void renderOptionText(DrawContext graphics, Text text, int n) {
			graphics.drawText(textRenderer, text, getDimension().xLimit() - textRenderer.getWidth(text) - getDecorationPadding(), getTextY() + n * getDimension().height() + 2, -1, true);
		}

		protected int getDecorationPadding() {
			return super.getXPadding();
		}

	}
}
