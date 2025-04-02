package dev.jsinco.brewery.effect.event;

import com.google.common.base.Preconditions;
import dev.jsinco.brewery.util.BreweryKey;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public final class CustomEvent implements DrunkEvent {

    @Getter
    private final List<EventStep> steps;
    private final int alcohol;
    private final int toxins;
    private final int probabilityWeight;
    private final String displayName;
    private final BreweryKey key;

    private CustomEvent(List<EventStep> steps, int alcohol, int toxins, int probabilityWeight, String displayName, BreweryKey key) {
        this.steps = steps;
        this.alcohol = alcohol;
        this.toxins = toxins;
        this.probabilityWeight = probabilityWeight;
        this.displayName = displayName;
        this.key = key;
    }

    @Override
    public int getAlcoholRequirement() {
        return alcohol;
    }

    @Override
    public int getToxinsRequirement() {
        return toxins;
    }

    @Override
    public String getTranslation() {
        return displayName;
    }

    @Override
    public BreweryKey key() {
        return key;
    }

    @Override
    public int getProbabilityWeight() {
        return probabilityWeight;
    }

    public static class Builder {
        private final BreweryKey key;
        private List<EventStep> steps = new ArrayList<>();
        private int alcohol = 0;
        private int toxins = 0;
        private int probabilityWeight = 0;
        private String displayName;

        public Builder(BreweryKey key) {
            this.key = Preconditions.checkNotNull(key);
        }

        public Builder addStep(EventStep step) {
            steps.add(step);
            return this;
        }

        public Builder alcoholRequirement(int alcoholRequirement) {
            alcohol = alcoholRequirement;
            return this;
        }

        public Builder toxinsRequirement(int toxinsRequirement) {
            toxins = toxinsRequirement;
            return this;
        }

        public Builder probabilityWeight(int probabilityWeight) {
            this.probabilityWeight = probabilityWeight;
            return this;
        }

        public CustomEvent build() {
            Preconditions.checkArgument(!steps.isEmpty(), "Steps cannot be empty");
            return new CustomEvent(List.copyOf(steps), alcohol, toxins, probabilityWeight, displayName == null ? key.key() : displayName, key);
        }
    }
}
