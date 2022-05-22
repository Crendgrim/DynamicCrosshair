package mod.crend.dynamiccrosshair.config;

public enum InteractableCrosshairPolicy {
    IfTargeting,
    IfInteractable,
    Disabled;

    @Override
    public String toString() {
        return "text.dynamiccrosshair.option.mode." + name();
    }
}
