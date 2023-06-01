package mod.crend.yaclx;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class ItemRegistryHelper {

	public static boolean isRegisteredItem(String value) {
		String namespace, itemName;
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		value = value.toLowerCase();
		if (sep == -1) {
			namespace = Identifier.DEFAULT_NAMESPACE;
			itemName = value;
		} else {
			namespace = value.substring(0, sep);
			itemName = value.substring(sep + 1);
		}
		try {
			Identifier identifier = new Identifier(namespace, itemName);
			return Registries.ITEM.containsId(identifier);
		} catch (InvalidIdentifierException e) {
			return false;
		}
	}
	public static Item getItemFromName(String value, Item defaultItem) {
		String namespace, itemName;
		int sep = value.indexOf(Identifier.NAMESPACE_SEPARATOR);
		value = value.toLowerCase();
		if (sep == -1) {
			namespace = Identifier.DEFAULT_NAMESPACE;
			itemName = value;
		} else {
			namespace = value.substring(0, sep);
			itemName = value.substring(sep + 1);
		}
		Identifier identifier = new Identifier(namespace, itemName);
		if (Registries.ITEM.containsId(identifier)) {
			return Registries.ITEM.get(identifier);
		} else {
			return defaultItem;
		}
	}
	public static Item getItemFromName(String value) {
		return getItemFromName(value, Items.AIR);
	}

	public static Stream<Identifier> getMatchingItemIdentifiers(String value) {
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
}
