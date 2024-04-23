package mod.crend.dynamiccrosshair.config;

import mod.crend.libbamboo.type.NameableEnum;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public enum CrosshairConfigModifier implements NameableEnum {
    Dot(CrosshairStyle.DOT),
    DiagonalCross(CrosshairStyle.CROSS_DIAGONAL_SMALL),
    Brackets(CrosshairStyle.BRACKETS),
    BracketsBottom(CrosshairStyle.BRACKETS_BOTTOM),
    BracketsTop(CrosshairStyle.BRACKETS_TOP),
    RoundBrackets(CrosshairStyle.BRACKETS_ROUND),
    Lines(CrosshairStyle.LINES),
    LineBottom(CrosshairStyle.LINE_BOTTOM);

    private final Identifier style;
    CrosshairConfigModifier(Identifier style) {
        this.style = style;
    }

    public Identifier getIdentifier() { return style; }

    @Override
    public Text getDisplayName() {
        return Text.translatable("dynamiccrosshair.style." + name());
    }
}
