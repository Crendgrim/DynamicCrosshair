package mod.crend.dynamiccrosshair.config;

import mod.crend.yaclx.type.NameableEnum;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public enum CrosshairConfigStyle implements NameableEnum {
    Default(CrosshairStyle.DEFAULT),
    Cross(CrosshairStyle.CROSS_OPEN),
    DiagonalCross(CrosshairStyle.CROSS_OPEN_DIAGONAL),
    Circle(CrosshairStyle.CIRCLE),
    Square(CrosshairStyle.SQUARE),
    Diamond(CrosshairStyle.DIAMOND),
    Caret(CrosshairStyle.CARET);

    private final Identifier style;
    CrosshairConfigStyle(Identifier style) {
        this.style = style;
    }

    public Identifier getIdentifier() { return style; }

    @Override
    public Text getDisplayName() {
        return Text.translatable("dynamiccrosshair.style." + name());
    }
}
