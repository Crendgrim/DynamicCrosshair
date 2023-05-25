package mod.crend.yaclx.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.utils.GuiUtils;
import mod.crend.yaclx.YaclxHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
		option.requestSet(YaclxHelper.getItemFromName(value, option.pendingValue()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Text formatValue() {
		return Text.literal(getString());
	}


	public boolean isValueValid(String value) {
		return YaclxHelper.isRegisteredItem(value);
	}

	public static Stream<Identifier> getMatchingIdentifiers(String value) {
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		Predicate<Identifier> filterPredicate;
		if (sep == -1) {
			filterPredicate = identifier ->
					identifier.getPath().contains(value)
					|| Registries.ITEM.get(identifier).getName().getString().toLowerCase().contains(value.toLowerCase());
		} else {
			String namespace = value.substring(0, sep);
			String path = value.substring(sep + 1);
			filterPredicate = identifier -> identifier.getNamespace().equals(namespace) && identifier.getPath().startsWith(path);
		}
		return Registries.ITEM.getIds().stream()
			.filter(filterPredicate)
			.sorted((id1, id2) -> {
				String path = (sep == -1 ? value : value.substring(sep + 1));
				boolean id1StartsWith = id1.getPath().startsWith(path);
				boolean id2StartsWith = id2.getPath().startsWith(path);
				if (id1StartsWith) {
					if (id2StartsWith) {
						return id1.compareTo(id2);
					}
					return -1;
				}
				if (id2StartsWith) {
					return 1;
				}
				return id1.compareTo(id2);
			});
	}

	String getValidValue(String value) {
		if (isValueValid(value)) {
			return value;
		}
		return getMatchingIdentifiers(value)
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

	public static class ItemControllerElement extends DropdownControllerElement<Item> {
		private final ItemController itemController;

		public ItemControllerElement(ItemController control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
			this.itemController = control;
		}

		@Override
		protected void drawValueText(DrawContext graphics, int mouseX, int mouseY, float delta) {
			super.drawValueText(graphics, mouseX, mouseY, delta);
			if (YaclxHelper.isRegisteredItem(inputField)) {
				graphics.drawItemWithoutEntity(new ItemStack(YaclxHelper.getItemFromName(inputField)), getDimension().xLimit() - getControlWidth(), getDimension().y() + 2);
			}
		}

		@Override
		public void renderDropdown(DrawContext graphics) {
			List<Identifier> identifiers = ItemController.getMatchingIdentifiers(inputField).toList();
			renderDropdownBackground(graphics, identifiers.size());
			if (identifiers.size() >= 1) {
				graphics.drawBorder(getDimension().x() + 20, getDimension().yLimit() + 2, getDimension().width() - 20, getDimension().height(), -1);
			}
			int n = 1;
			for (Identifier identifier : identifiers) {
				Item item = Registries.ITEM.get(identifier);
				Text itemName = Text.literal(GuiUtils.shortenString(item.toString(), textRenderer, getDimension().width() - getXPadding() - 44, "..."));
				renderOption(graphics, itemName, n);
				graphics.drawItemWithoutEntity(new ItemStack(item), getDimension().xLimit() - textRenderer.getWidth(itemName) - getXPadding() - 24, getDimension().y() + n * getDimension().height() + 4);
				++n;
			}
		}

		@Override
		protected int getHoveredControlWidth() {
			return super.getHoveredControlWidth() + 24;
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
