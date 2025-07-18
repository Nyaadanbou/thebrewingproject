package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.recipe.RecipeEffect;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.EventStepRegistry;
import dev.jsinco.brewery.event.step.ApplyPotionEffect;
import dev.jsinco.brewery.moment.Interval;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

public class ApplyPotionEffectImpl extends ApplyPotionEffect {

    public ApplyPotionEffectImpl(String potionEffectName, Interval amplifierBounds, Interval durationBounds) {
        super(potionEffectName, amplifierBounds, durationBounds);
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer);
        if (player == null) {
            return;
        }

        PotionEffect potionEffect = new RecipeEffect(
                Registry.EFFECT.get(NamespacedKey.fromString(getPotionEffectName())),
                getDurationBounds(),
                getAmplifierBounds()
        ).newPotionEffect();
        player.addPotionEffect(potionEffect);
    }

    @Override
    public void register(EventStepRegistry registry) {
        registry.register(ApplyPotionEffect.class, original -> {
            ApplyPotionEffect event = (ApplyPotionEffect) original;
            return new ApplyPotionEffectImpl(
                    event.getPotionEffectName(),
                    event.getAmplifierBounds(),
                    event.getDurationBounds()
            );
        });
    }
}
