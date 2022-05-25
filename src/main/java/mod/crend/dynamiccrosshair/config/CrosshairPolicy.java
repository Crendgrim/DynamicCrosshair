package mod.crend.dynamiccrosshair.config;

public enum CrosshairPolicy {
    Always,
    IfTargeting,
    Disabled;

    @Override
    public String toString() {
        return "text.dynamiccrosshair.option.mode." + name();
    }
}

