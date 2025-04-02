package dev.jsinco.brewery.effect.event;

import dev.jsinco.brewery.util.moment.Interval;

public record ApplyPotionEffect(String potionEffectName, Interval amplifierBounds,
                                Interval durationBounds) implements EventStep {
}
