package dev.jsinco.brewery.bukkit.effect.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import dev.jsinco.brewery.moment.Moment;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.UUID;

public class NauseaNamedExecutable implements ExecutableEventStep {

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer);
        if (player == null) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, RANDOM.nextInt(Moment.MINUTE / 2, Moment.MINUTE * 3 / 2), 1));
    }

}
