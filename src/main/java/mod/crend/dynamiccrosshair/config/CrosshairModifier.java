package mod.crend.dynamiccrosshair.config;

public enum CrosshairModifier {
    DISABLED(240, 240),
    DOT(0, 15),
    DIAGONAL_CROSS(15, 15),
    BRACKETS(0, 30),
    BRACKETS_BOTTOM(15, 30),
    BRACKETS_TOP(30, 30),
    ROUND_BRACKETS(0, 45),
    LINES(0, 60),
    LINE_BOTTOM(15, 60);

    private final int x;
    private final int y;
    CrosshairModifier(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public String toString() {
        return "text.dynamiccrosshair.modifierStyle." + name();
    }
}
