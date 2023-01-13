package mod.crend.dynamiccrosshair.config;

public enum RangedCrosshairPolicy {
    Always,
    IfRangedWeaponFullyDrawn,
    Disabled;

    @Override
    public String toString() {
        return "text.dynamiccrosshair.option.mode." + name();
    }
}

