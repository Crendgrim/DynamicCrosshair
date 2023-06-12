package mod.crend.dynamiccrosshair.config;

import mod.crend.yaclx.type.NameableEnum;
import net.minecraft.text.Text;

public enum InteractableCrosshairPolicy implements NameableEnum {
    IfTargeting,
    IfInteractable,
    Disabled;

    @Override
    public Text getDisplayName() {
        return Text.translatable("dynamiccrosshair.policy." + name());
    }
}
