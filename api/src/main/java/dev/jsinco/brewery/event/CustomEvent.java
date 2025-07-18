package dev.jsinco.brewery.event;

import com.google.common.base.Preconditions;
import dev.jsinco.brewery.util.BreweryKey;
import dev.jsinco.brewery.util.Holder;

import java.util.ArrayList;
import java.util.List;

public final class CustomEvent implements DrunkEvent {

    private final List<EventStep> steps;
    private final int alcohol;
    private final int toxins;
    private final int probabilityWeight;
    private final String displayName;
    private final BreweryKey key;

    public CustomEvent(List<EventStep> steps, int alcohol, int toxins, int probabilityWeight, String displayName, BreweryKey key) {
        this.steps = steps;
        this.alcohol = alcohol;
        this.toxins = toxins;
        this.probabilityWeight = probabilityWeight;
        this.displayName = displayName;
        this.key = key;
    }

    @Override
    public int alcoholRequirement() {
        return alcohol;
    }

    @Override
    public int toxinsRequirement() {
        return toxins;
    }

    @Override
    public BreweryKey key() {
        return key;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public int probabilityWeight() {
        return probabilityWeight;
    }

    public List<EventStep> getSteps() {
        return List.copyOf(steps);
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException("This method should not be called directly on this class. Your event executor should handle this logic.");
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

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public CustomEvent build() {
            Preconditions.checkArgument(!steps.isEmpty(), "Steps cannot be empty");
            return new CustomEvent(List.copyOf(steps), alcohol, toxins, probabilityWeight, displayName == null ? key.key() : displayName, key);
        }
    }
}
