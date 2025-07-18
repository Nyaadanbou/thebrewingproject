package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.recipe.RecipeEffect;
import dev.jsinco.brewery.event.step.ApplyPotionEffect;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.moment.Interval;
import dev.jsinco.brewery.util.Holder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class ApplyPotionEffectImpl extends ApplyPotionEffect {

    public ApplyPotionEffectImpl(String potionEffectName, Interval amplifierBounds, Interval durationBounds) {
        super(potionEffectName, amplifierBounds, durationBounds);
    }

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer.value());
        if (player == null) {
            return;
        }
        System.out.println("Applying potion effect: " + getPotionEffectName() + " to player: " + player.getName());

        PotionEffect potionEffect = new RecipeEffect(
                Registry.EFFECT.get(NamespacedKey.fromString(getPotionEffectName())),
                getDurationBounds(),
                getAmplifierBounds()
        ).newPotionEffect();
        player.addPotionEffect(potionEffect);
    }
}
