package mod.crend.yaclx.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.ControllerBuilder;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.impl.controller.AbstractControllerBuilderImpl;
import mod.crend.yaclx.ItemOrTag;
import mod.crend.yaclx.ItemRegistryHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Simple controller that simply runs the button action on press
 * and renders a {@link} Text on the right.
 */
public class ItemOrTagController extends AbstractDropdownController<ItemOrTag> {

	/**
	 * Constructs an item controller
	 *
	 * @param option bound option
	 */
	public ItemOrTagController(Option<ItemOrTag> option) {
		super(option);
	}

	static Stream<Identifier> getMatchingItemTagIdentifiers(String value) {
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		Predicate<TagKey<Item>> filterPredicate;
		if (sep == -1) {
			filterPredicate = tagKey ->
					tagKey.id().getPath().contains(value)
							|| tagKey.id().toString().toLowerCase().contains(value.toLowerCase());
		} else {
			String namespace = value.substring(0, sep);
			String path = value.substring(sep + 1);
			filterPredicate = tagKey -> tagKey.id().getNamespace().equals(namespace) && tagKey.id().getPath().startsWith(path);
		}
		return ItemOrTag.getItemTags().stream()
				.filter(filterPredicate)
				.sorted((t1, t2) -> {
					String path = (sep == -1 ? value : value.substring(sep + 1));
					boolean id1StartsWith = t1.id().getPath().startsWith(path);
					boolean id2StartsWith = t2.id().getPath().startsWith(path);
					if (id1StartsWith) {
						if (id2StartsWith) {
							return t1.id().compareTo(t2.id());
						}
						return -1;
					}
					if (id2StartsWith) {
						return 1;
					}
					return t1.id().compareTo(t2.id());
				})
				.map(TagKey::id);
	}

	@Override
	public String getString() {
		return option.pendingValue().toString();
	}

	@Override
	public void setFromString(String value) {
		ItemOrTag.fromString(value, false).ifPresent(option::requestSet);
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
		if (value.startsWith("#")) {
			return ItemOrTag.isItemTag(value.substring(1));
		} else {
			return ItemRegistryHelper.isRegisteredItem(value);
		}
	}

	@Override
	String getValidValue(String value, int offset) {
		if (value.startsWith("#")) {
			return value;
		} else {
			return ItemRegistryHelper.getMatchingItemIdentifiers(value)
					.skip(offset)
					.findFirst()
					.map(Identifier::toString)
					.orElseGet(this::getString);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new ItemOrTagControllerElement(this, screen, widgetDimension);
	}

	public interface ItemOrTagControllerBuilder extends ControllerBuilder<ItemOrTag> {
		static ItemOrTagControllerBuilderImpl create(Option<ItemOrTag> option) {
			return new ItemOrTagControllerBuilderImpl(option);
		}
	}

	public static class ItemOrTagControllerBuilderImpl extends AbstractControllerBuilderImpl<ItemOrTag> implements ItemOrTagControllerBuilder {
		protected ItemOrTagControllerBuilderImpl(Option<ItemOrTag> option) {
			super(option);
		}

		@Override
		public Controller<ItemOrTag> build() {
			return new ItemOrTagController(option);
		}
	}

	public static class ItemOrTagControllerElement extends DropdownControllerElement<ItemOrTag, Identifier> {
		private final ItemOrTagController itemOrTagController;

		public ItemOrTagControllerElement(ItemOrTagController control, YACLScreen screen, Dimension<Integer> dim) {
			super(control, screen, dim);
			this.itemOrTagController = control;
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (inputFieldFocused && opened && keyCode == InputUtil.GLFW_KEY_ENTER) {
				if (inputField.startsWith("#") && getDropdownLength() > 0) {
					inputField = getMatchingItemTagIdentifiers(inputField.substring(1))
							.skip(selected)
							.findFirst()
							.map(id -> "#" + id)
							.orElseGet(itemOrTagController::getString);
					caretPos = getDefaultCaretPos();
					updateControl();
				}
			}
			return super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public int getDropdownLength() {
			if (inputField.startsWith("#")) {
				return (int) getMatchingItemTagIdentifiers(inputField.substring(1)).count();
			} else {
				return (int) ItemRegistryHelper.getMatchingItemIdentifiers(inputField).count();
			}
		}

		@Override
		protected void drawValueText(DrawContext graphics, int mouseX, int mouseY, float delta) {
			var oldDimension = getDimension();
			setDimension(getDimension().withWidth(getDimension().width() - getDecorationPadding()));
			super.drawValueText(graphics, mouseX, mouseY, delta);
			setDimension(oldDimension);
			ItemOrTag.fromString(inputField, true)
					.ifPresent(itemOrTag -> graphics.drawItemWithoutEntity(
							new ItemStack(itemOrTag.getAnyItem()),
							getDimension().xLimit() - getXPadding() - getDecorationPadding() + 2,
							getDimension().y() + 2)
					);
		}

		@Override
		public List<Identifier> getMatchingValues() {
			if (inputField.startsWith("#")) {
				return getMatchingItemTagIdentifiers(inputField.substring(1)).toList();
			} else {
				return ItemRegistryHelper.getMatchingItemIdentifiers(inputField).toList();
			}
		}

		@Override
		public String getString(Identifier identifier) {
			// If we are filtering for item tags, show item tags
			if (inputField.startsWith("#")) {
				if (identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
					return "#" + identifier.getPath();
				}
				return "#" + identifier;
			} else {
				return Registries.ITEM.get(identifier).toString();
			}
		}

		@Override
		protected void renderOption(DrawContext graphics, Identifier identifier, int n) {
			super.renderOption(graphics, identifier, n);
			Item item;
			if (inputField.startsWith("#")) {
				TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, identifier);
				item = new ItemOrTag(tagKey).getAnyItem();
			} else {
				item = Registries.ITEM.get(identifier);
			}
			graphics.drawItemWithoutEntity(new ItemStack(item), getDimension().xLimit() - getDecorationPadding() + 2, getDimension().y() + n * getDimension().height() + 4);
		}

		@Override
		protected int getControlWidth() {
			return super.getControlWidth() + getDecorationPadding();
		}

		@Override
		protected int getDecorationPadding() {
			return 20;
		}

		@Override
		protected Text getValueText() {
			if (inputField.isEmpty() || itemOrTagController == null)
				return super.getValueText();

			if (inputFieldFocused)
				return Text.literal(inputField);

			ItemOrTag itemOrTag = itemOrTagController.option.pendingValue();
			if (itemOrTag.isItemTag() && itemOrTag.id().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
				return Text.literal("#" + itemOrTag.id().getPath());
			}

			return itemOrTag.getName();
		}
	}
}
