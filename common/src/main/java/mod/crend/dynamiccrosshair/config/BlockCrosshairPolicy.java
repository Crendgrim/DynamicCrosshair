package mod.crend.dynamiccrosshair.config;

import mod.crend.autoyacl.NameableEnum;
import net.minecraft.text.Text;

public enum BlockCrosshairPolicy implements NameableEnum {
    Always,
    IfTargeting,
    IfInteractable,
    Disabled;

    @Override
    public Text getDisplayName() {
        return Text.translatable("dynamiccrosshair.policy." + name());
    }
}