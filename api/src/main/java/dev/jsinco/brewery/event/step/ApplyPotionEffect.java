package dev.jsinco.brewery.event.step;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.IllegalEventStepCall;
import dev.jsinco.brewery.moment.Interval;

import java.util.List;
import java.util.UUID;

public class ApplyPotionEffect implements EventStep {

    private final String potionEffectName;
    private final Interval amplifierBounds;
    private final Interval durationBounds;

    public ApplyPotionEffect(String potionEffectName, Interval amplifierBounds, Interval durationBounds) {
        this.potionEffectName = potionEffectName;
        this.amplifierBounds = amplifierBounds;
        this.durationBounds = durationBounds;
    }

    public String getPotionEffectName() {
        return potionEffectName;
    }

    public Interval getAmplifierBounds() {
        return amplifierBounds;
    }

    public Interval getDurationBounds() {
        return durationBounds;
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        throw new IllegalEventStepCall();
    }

    @Override
    public void register(EventStepRegistry registry) {
        throw new IllegalEventStepCall();
    }
}

