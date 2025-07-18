package dev.jsinco.brewery.bukkit.effect.named;

import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.named.NauseaNamedDrunkEvent;
import dev.jsinco.brewery.moment.Moment;
import dev.jsinco.brewery.util.Holder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class NauseaNamedDrunkEventImpl extends NauseaNamedDrunkEvent {

    @Override
    public void execute(Holder.Player contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer.value());
        if (player == null) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, RANDOM.nextInt(Moment.MINUTE / 2, Moment.MINUTE * 3 / 2), 1));
    }
}
