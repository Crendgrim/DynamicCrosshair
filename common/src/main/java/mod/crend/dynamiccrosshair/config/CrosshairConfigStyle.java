package mod.crend.dynamiccrosshair.config;

import mod.crend.libbamboo.type.NameableEnum;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public enum CrosshairConfigStyle implements NameableEnum {
    Default(CrosshairStyle.DEFAULT),
    Cross(CrosshairStyle.CROSS_OPEN),
    DiagonalCross(CrosshairStyle.CROSS_OPEN_DIAGONAL),
    Circle(CrosshairStyle.CIRCLE),
    CircleLarge(CrosshairStyle.CIRCLE_LARGE),
    Square(CrosshairStyle.SQUARE),
    Diamond(CrosshairStyle.DIAMOND),
    DiamondLarge(CrosshairStyle.DIAMOND_LARGE),
    Caret(CrosshairStyle.CARET),
    Dot(CrosshairStyle.DOT),
    SmallDiagonalCross(CrosshairStyle.CROSS_DIAGONAL_SMALL),
    Brackets(CrosshairStyle.BRACKETS),
    BracketsBottom(CrosshairStyle.BRACKETS_BOTTOM),
    BracketsTop(CrosshairStyle.BRACKETS_TOP),
    RoundBrackets(CrosshairStyle.BRACKETS_ROUND),
    Lines(CrosshairStyle.LINES),
    LineBottom(CrosshairStyle.LINE_BOTTOM);

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
