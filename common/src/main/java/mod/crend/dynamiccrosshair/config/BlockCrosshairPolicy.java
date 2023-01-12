package mod.crend.dynamiccrosshair.config;

public enum BlockCrosshairPolicy {
    Always,
    IfTargeting,
    IfInteractable,
    Disabled;

    @Override
    public String toString() {
        return "text.dynamiccrosshair.option.mode." + name();
    }
}