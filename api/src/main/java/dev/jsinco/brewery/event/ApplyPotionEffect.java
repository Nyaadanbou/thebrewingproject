package dev.jsinco.brewery.event;

import dev.jsinco.brewery.moment.Interval;

public record ApplyPotionEffect(String potionEffectName, Interval amplifierBounds,
                                Interval durationBounds) implements EventStep {
}
