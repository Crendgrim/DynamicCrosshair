package mod.crend.dynamiccrosshair.config;

import mod.crend.libbamboo.type.NameableEnum;
import net.minecraft.text.Text;

public enum CrosshairMode implements NameableEnum {
	Disabled,
	Simple,
	Advanced;

	@Override
	public Text getDisplayName() {
		return Text.translatable("dynamiccrosshair.mode." + name());
	}
}
