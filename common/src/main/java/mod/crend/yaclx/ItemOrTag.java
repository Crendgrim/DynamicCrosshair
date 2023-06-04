package mod.crend.yaclx;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Wrapper class that represents either an Item or an ItemTag.
 * In serialized form, item tags gain a # symbol in front of their identifier
 * to differentiate between the two.
 */
public class ItemOrTag {
	private final static List<TagKey<Item>> BUILTIN_ITEM_TAGS = new ArrayList<>();

	@SuppressWarnings("unchecked")
	private static void loadItemTags(Class<?> itemTagsClass) {
		// Manually read item tags based on their fields. This is a hack to allow listing potential item tags without
		// a world being loaded.
		for (Field field : itemTagsClass.getDeclaredFields()) {
			try {
				if (!field.isAnnotationPresent(Deprecated.class)) {
					BUILTIN_ITEM_TAGS.add((TagKey<Item>) field.get(null));
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static {
		loadItemTags(ItemTags.class);
		loadItemTags(PlatformUtils.getModdedItemTagsClass());
	}

	/**
	 * Returns a list of all known item tags.
	 *
	 * When we open a config screen from the main menu, no world is loaded. Item tags are world-based, so we cannot
	 * give any hints here. To circumvent this, we read out the item tags manually to show suggestions that will be
	 * correct under most circumstances. These will show both vanilla and modloader specific tags, though. When opening
	 * from within a world, the list will instead be populated by the server-provided item tags.
	 *
	 * Do note also that, without a world, these item tags are not actually loaded, and we only cache their names here.
	 * Other mods' item tags will also appear here only if a world is loaded.
	 *
	 * @return A list of all known item tags
	 */
	public static List<TagKey<Item>> getItemTags() {
		List<TagKey<Item>> currentlyLoadedTags = Registries.ITEM.streamTags().toList();
		if (currentlyLoadedTags.isEmpty()) {
			// No elements in the stream; use the default tags as read by reflection
			return BUILTIN_ITEM_TAGS;
		}
		return currentlyLoadedTags;
	}

	/**
	 * Static helper to query whether the given String names an identifier to a known item tag.
	 * If the given String contains no namespace, the default namespace ("minecraft") is checked.
	 *
	 * This method follows the same rules as {@link ItemOrTag#getItemTags()}.
	 *
	 * @param value a String; should be of the format "namespace:path" or "path".
	 * @return true if the given String names a known item tag, false otherwise.
	 */
	public static boolean isItemTag(String value) {
		String namespace, itemTag;
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		value = value.toLowerCase();
		if (sep == -1) {
			namespace = Identifier.DEFAULT_NAMESPACE;
			itemTag = value;
		} else {
			namespace = value.substring(0, sep);
			itemTag = value.substring(sep + 1);
		}
		try {
			TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, new Identifier(namespace, itemTag));
			return getItemTags().contains(tagKey);
		} catch (InvalidIdentifierException e) {
			return false;
		}
	}

	/**
	 * Helper to query a collection of ItemOrTag entries whether any of them match the given item.
	 * Short-circuits on the first match.
	 *
	 * @see ItemOrTag#matches(Item)
	 * @param item The item to search.
	 * @param collection A collection of ItemOrTag elements.
	 * @return true if any of them match, false otherwise.
	 */
	public static boolean isContainedIn(Item item, Collection<ItemOrTag> collection) {
		return collection.stream().anyMatch(itemOrTag -> itemOrTag.matches(item));
	}

	/**
	 * Factory helper to build an ItemOrTag from a String identifier.
	 *
	 * <p>The identifier should be of one of the following formats:<br>
	 * - #namespace:path<br>
	 * - #path<br>
	 * - namespace:path<br>
	 * - path<br>
	 *
	 * <p>If namespace is not given, the default namespace ("minecraft") is used.
	 *
	 * <p>If the identifier starts with a #, construct an item tag variant. Otherwise, constructs an item variant.
	 *
	 * @param identifier The String to identify with.
	 * @param ensureItemTagExists If true, only allow construction of an item tag variant if the given variant exists.
	 * @return An ItemOrTag object if the construction was successful (identifier exists, or item tag variant given
	 *         ensureItemTagExists is false); None otherwise.
	 */
	public static Optional<ItemOrTag> fromString(String identifier, boolean ensureItemTagExists) {
		if (identifier.startsWith("#")) {
			if (ensureItemTagExists && !isItemTag(identifier.substring(1))) {
				return Optional.empty();
			}
		} else if (!ItemRegistryHelper.isRegisteredItem(identifier)) {
			return Optional.empty();
		}
		return Optional.of(new ItemOrTag(identifier));
	}

	private Item item = null;
	private TagKey<Item> itemTag = null;
	private final boolean isItem;

	public ItemOrTag(Item item) {
		this.item = item;
		this.isItem = true;
	}
	public ItemOrTag(TagKey<Item> tag) {
		this.itemTag = tag;
		this.isItem = false;
	}
	protected ItemOrTag(String identifier) {
		if (identifier.startsWith("#")) {
			String tagName = identifier.substring(1);
			this.itemTag = TagKey.of(RegistryKeys.ITEM, new Identifier(tagName));
			this.isItem = false;
		} else {
			this.item = ItemRegistryHelper.getItemFromName(identifier);
			this.isItem = true;
		}
	}

	public boolean isItem() {
		return isItem;
	}
	public boolean isItemTag() {
		return !isItem;
	}

	public Item getItem() {
		assert(isItem);
		return item;
	}

	public TagKey<Item> getItemTag() {
		assert(!isItem);
		return itemTag;
	}

	/**
	 * Gets a relevant item from this ItemOrTag object.
	 *
	 * If this is an item variant, returns the item itself.
	 *
	 * Else, returns the first item registered under the tag if it is loaded, or Air otherwise.
	 *
	 * @return a valid item, or Items.AIR
	 */
	public Item getAnyItem() {
		if (isItem) return item;
		var tagEntries = Registries.ITEM.getEntryList(itemTag);
		if (tagEntries.isPresent()) {
			return tagEntries.get().get(0).value();
		}
		return PlatformUtils.getItemsFromTag(itemTag)
				.stream()
				.findFirst()
				.map(Registries.ITEM::get)
				.orElse(Items.AIR);
	}

	public Collection<Item> getAllItems() {
		if (isItem) return List.of(item);
		return Registries.ITEM.getEntryList(itemTag)
				// Extract list of identifiers from loaded tag registry, if present
				.map(registryEntries -> registryEntries.stream().map(RegistryEntry::value).toList())
				// Or, if empty, manually force-load from declaration classes
				.orElseGet(() -> PlatformUtils.getItemsFromTag(itemTag)
				.stream()
				.map(Registries.ITEM::get)
				.toList());
	}

	/**
	 * Checks whether this ItemOrTag matches the given item.
	 *
	 * If this is an item variant, checks for equality.
	 *
	 * Otherwise, checks whether this item is part of this item tag.
	 * Note: This only works if tags are loaded.
	 *
	 * @param other The item to check
	 * @return true if it matches according to above rules, false otherwise
	 */
	public boolean matches(Item other) {
		if (isItem) {
			return other.equals(item);
		} else {
			for (var it : Registries.ITEM.iterateEntries(itemTag)) {
				if (other.equals(it.value())) return true;
			}
			return false;
		}
	}

	public String toString() {
		if (isItem) {
			return id().toString();
		} else {
			return "#" + id().toString();
		}
	}

	public Text getName() {
		if (isItem) {
			return item.getName();
		} else {
			return Text.literal("#" + itemTag.id().toString());
		}
	}

	public Identifier id() {
		if (isItem) {
			return Registries.ITEM.getId(item);
		} else {
			return itemTag.id();
		}
	}
}
