package dev.jsinco.brewery.bukkit.recipe;

import dev.jsinco.brewery.recipes.PotionQuality;
import dev.jsinco.brewery.util.moment.Interval;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

// FIXME - needs to convert from seconds to minecraft ticks
public record RecipeEffect(PotionEffectType effect, Interval durationBounds, Interval amplifierBounds) {

    private static final Random RANDOM = new Random();

    public static RecipeEffect of(PotionEffectType effect, Interval durationBounds, Interval amplifierBounds) {
        return new RecipeEffect(effect, durationBounds, amplifierBounds);
    }

    public PotionEffect getPotionEffect(PotionQuality quality) {
        return switch (quality) {
            // Return the lowest (first) bound
            case BAD -> new PotionEffect(effect, (int) durationBounds.start(), (int) amplifierBounds.start());
            // Return a value between the first and second bounds
            case GOOD ->
                    new PotionEffect(effect, RANDOM.nextInt((int) durationBounds.start(), (int) durationBounds.stop()), RANDOM.nextInt((int) amplifierBounds.start(), (int) amplifierBounds.stop()));
            // Return the highest (second) bound
            case EXCELLENT -> new PotionEffect(effect, (int) durationBounds.stop(), (int) amplifierBounds.stop());
        };
    }
}
