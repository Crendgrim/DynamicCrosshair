package mod.crend.dynamiccrosshair.config;

import mod.crend.yaclx.auto.NameableEnum;
import net.minecraft.text.Text;

@SuppressWarnings("unused")
public enum CrosshairConfigModifier implements NameableEnum {
    Disabled(240, 240),
    Dot(0, 15),
    DiagonalCross(15, 15),
    Brackets(0, 30),
    BracketsBottom(15, 30),
    BracketsTop(30, 30),
    RoundBrackets(0, 45),
    Lines(0, 60),
    LineBottom(15, 60);

    private final int x;
    private final int y;
    CrosshairConfigModifier(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public Text getDisplayName() {
        return Text.translatable("dynamiccrosshair.style." + name());
    }
}
