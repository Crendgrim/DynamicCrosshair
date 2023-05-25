package mod.crend.dynamiccrosshair.config;

import mod.crend.yaclx.auto.NameableEnum;
import net.minecraft.text.Text;

public enum CrosshairConfigStyle implements NameableEnum {
    Default(0, 0),
    Cross(15, 0),
    DiagonalCross(30, 0),
    Circle(45, 0),
    Square(60, 0),
    Diamond(75, 0),
    Caret(90, 0);

    private final int x;
    private final int y;
    CrosshairConfigStyle(int x, int y) {
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
