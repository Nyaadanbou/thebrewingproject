package dev.jsinco.brewery.event.step;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.moment.Interval;

public record ApplyPotionEffect(String potionEffectName, Interval amplifierBounds,
                                Interval durationBounds) implements EventStep {

}

