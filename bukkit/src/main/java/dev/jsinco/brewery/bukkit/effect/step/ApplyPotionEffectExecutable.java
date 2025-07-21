package dev.jsinco.brewery.bukkit.effect.step;

import dev.jsinco.brewery.bukkit.recipe.RecipeEffect;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.moment.Interval;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;


public class ApplyPotionEffectExecutable implements ExecutableEventStep {

    private final String potionEffectName;
    private final Interval amplifierBounds;
    private final Interval durationBounds;

    public ApplyPotionEffectExecutable(String potionEffectName, Interval amplifierBounds, Interval durationBounds) {
        this.potionEffectName = potionEffectName;
        this.amplifierBounds = amplifierBounds;
        this.durationBounds = durationBounds;
    }

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer);
        if (player == null) {
            return;
        }

        PotionEffect potionEffect = new RecipeEffect(
                Registry.EFFECT.get(NamespacedKey.fromString(potionEffectName)),
                durationBounds,
                amplifierBounds
        ).newPotionEffect();
        player.addPotionEffect(potionEffect);
    }

}
