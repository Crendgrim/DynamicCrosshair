package mod.crend.dynamiccrosshair.config;

public enum RangedCrosshairPolicy {
    Always,
    IfFullyDrawn,
    Disabled;

    @Override
    public String toString() {
        return "text.dynamiccrosshair.option.mode." + name();
    }
}

