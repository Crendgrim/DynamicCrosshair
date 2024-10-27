package mod.crend.dynamiccrosshairapi.crosshair;

import mod.crend.dynamiccrosshairapi.interaction.InteractionType;

public record Crosshair(
        InteractionType primaryInteraction,
        InteractionType secondaryInteraction
){
    public Crosshair() {
        this(InteractionType.EMPTY, InteractionType.EMPTY);
    }
    public Crosshair(InteractionType interactionType) {
        this(interactionType.getPrimaryInteractionType(), interactionType.getSecondaryInteractionType());
    }

    public Crosshair combine(Crosshair other) {
        if (other == null) return this;
        InteractionType newPrimaryInteraction = primaryInteraction;
        InteractionType newSecondaryInteraction = secondaryInteraction;
        if (primaryInteraction == InteractionType.EMPTY) {
            if (other.primaryInteraction() != InteractionType.EMPTY) {
                newPrimaryInteraction = other.primaryInteraction();
            }
        } else if (primaryInteraction == InteractionType.TOOL) {
            if (other.primaryInteraction() == InteractionType.CORRECT_TOOL
                    || other.primaryInteraction() == InteractionType.INCORRECT_TOOL) {
                newPrimaryInteraction = other.primaryInteraction();
            }
        }
        if (secondaryInteraction == InteractionType.EMPTY) {
            if (other.secondaryInteraction() != InteractionType.EMPTY) {
                newSecondaryInteraction = other.secondaryInteraction();
            }
        } else if (other.secondaryInteraction == InteractionType.RANGED_WEAPON_CHARGING
                || other.secondaryInteraction == InteractionType.RANGED_WEAPON_CHARGED) {
            newSecondaryInteraction = other.secondaryInteraction;
        }
        return new Crosshair(newPrimaryInteraction, newSecondaryInteraction);
    }

    public boolean hasInteraction() {
        return primaryInteraction != InteractionType.EMPTY || secondaryInteraction != InteractionType.EMPTY;
    }
    public static Crosshair combine(Crosshair one, Crosshair other) {
        if (one == null) return (other == null ? new Crosshair() : other);
        return one.combine(other);
    }

    public boolean hasSecondaryInteraction() {
        return secondaryInteraction != InteractionType.EMPTY;
    }


    @Override
    public String toString() {
        return "Crosshair{" +
                "primaryInteraction=" + primaryInteraction +
                ", secondaryInteraction=" + secondaryInteraction +
                '}';
    }
}
