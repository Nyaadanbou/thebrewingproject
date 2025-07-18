package dev.jsinco.brewery.event.step;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.moment.Interval;
import dev.jsinco.brewery.util.Holder;

import java.util.List;

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
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        throw new UnsupportedOperationException();
    }
}

