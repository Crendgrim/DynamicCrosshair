package mod.crend.yaclx.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.ControllerBuilder;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.impl.controller.AbstractControllerBuilderImpl;
import mod.crend.yaclx.ItemRegistryHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Simple controller that simply runs the button action on press
 * and renders a {@link} Text on the right.
 */
public class ItemController extends AbstractDropdownController<Item> {

	/**
	 * Constructs an item controller
	 *
	 * @param option bound option
	 */
	public ItemController(Option<Item> option) {
		super(option);
	}

	@Override
	public String getString() {
		return Registries.ITEM.getId(option.pendingValue()).toString();
	}

	@Override
	public void setFromString(String value) {
		option.requestSet(ItemRegistryHelper.getItemFromName(value, option.pendingValue()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Text formatValue() {
		return Text.literal(getString());
	}


	@Override
	public boolean isValueValid(String value) {
		return ItemRegistryHelper.isRegisteredItem(value);
	}

	@Override
	String getValidValue(String value, int offset) {
		return ItemRegistryHelper.getMatchingItemIdentifiers(value)
				.skip(offset)
				.findFirst()
				.map(Identifier::toString)
				.orElseGet(this::getString);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new ItemControllerElement(this, screen, widgetDimension);
	}

	public interface ItemControllerBuilder extends ControllerBuilder<Item> {
		static ItemControllerBuilderImpl create(Option<Item> option) {
			return new ItemControllerBuilderImpl(option);
		}
	}

	public static class ItemControllerBuilderImpl extends AbstractControllerBuilderImpl<Item> implements ItemControllerBuilder {
		protected ItemControllerBuilderImpl(Option<Item> option) {
			super(option);
		}

		@Override
		public Controller<Item> build() {
			return new ItemController(option);
		}
	}

	public static class ItemControllerElement extends DropdownControllerElement<Item, Identifier> {
		private final ItemController itemController;

		public ItemControllerElement(ItemController control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
			this.itemController = control;
		}

		@Override
		protected void drawValueText(DrawContext graphics, int mouseX, int mouseY, float delta) {
			var oldDimension = getDimension();
			setDimension(getDimension().withWidth(getDimension().width() - getDecorationPadding()));
			super.drawValueText(graphics, mouseX, mouseY, delta);
			setDimension(oldDimension);
			if (ItemRegistryHelper.isRegisteredItem(inputField)) {
				graphics.drawItemWithoutEntity(new ItemStack(ItemRegistryHelper.getItemFromName(inputField)), getDimension().xLimit() - getXPadding() - getDecorationPadding() + 2, getDimension().y() + 2);
			}
		}

		@Override
		public List<Identifier> getMatchingValues() {
			return ItemRegistryHelper.getMatchingItemIdentifiers(inputField).toList();
		}

		@Override
		protected void renderOption(DrawContext graphics, Identifier identifier, int n) {
			super.renderOption(graphics, identifier, n);
			Item item = Registries.ITEM.get(identifier);
			graphics.drawItemWithoutEntity(new ItemStack(item), getDimension().xLimit() - getDecorationPadding() + 2, getDimension().y() + n * getDimension().height() + 4);
		}

		@Override
		public String getString(Identifier identifier) {
			return Registries.ITEM.get(identifier).toString();
		}

		protected int getDecorationPadding() {
			return 20;
		}

		@Override
		protected int getControlWidth() {
			return super.getControlWidth() + getDecorationPadding();
		}

		@Override
		protected Text getValueText() {
			if (inputField.isEmpty() || itemController == null)
				return super.getValueText();

			if (inputFieldFocused)
				return Text.literal(inputField);

			return itemController.option.pendingValue().getName();
		}
	}
}
