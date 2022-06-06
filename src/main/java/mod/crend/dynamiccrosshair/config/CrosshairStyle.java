package mod.crend.dynamiccrosshair.config;

public enum CrosshairStyle {
    DEFAULT(0, 0),
    CROSS(15, 0),
    DIAGONAL_CROSS(30, 0),
    CIRCLE(45, 0),
    SQUARE(60, 0),
    DIAMOND(75, 0),
    CARET(90, 0);

    private final int x;
    private final int y;
    CrosshairStyle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public String toString() {
        return "text.dynamiccrosshair.style." + name();
    }
}
