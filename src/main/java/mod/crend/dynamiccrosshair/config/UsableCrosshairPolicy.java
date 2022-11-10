package mod.crend.dynamiccrosshair.config;

public enum UsableCrosshairPolicy {
    Always,
    IfInteractable,
    Disabled;

    @Override
    public String toString() {
        return "text.dynamiccrosshair.option.mode." + name();
    }
}

