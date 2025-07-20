package dev.jsinco.brewery.bukkit.effect.named;

import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.event.EventStep;
import dev.jsinco.brewery.event.ExecutableEventStep;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class DrunkMessageNamedExecutable implements ExecutableEventStep {

    @Override
    public void execute(UUID contextPlayer, List<EventStep> events, int index) {
        Player player = Bukkit.getPlayer(contextPlayer);
        if (player == null) {
            return;
        }

        List<String> drunkMessages = Config.config().events().drunkMessages();
        if (drunkMessages.isEmpty()) {
            return;
        }
        List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream()
                .filter(Player::isVisibleByDefault)
                .filter(player1 -> !player.equals(player1))
                .map(Player.class::cast)
                .toList();
        if (onlinePlayers.isEmpty()) {
            return;
        }
        Player randomPlayer = onlinePlayers.get(RANDOM.nextInt(onlinePlayers.size()));
        player.chat(drunkMessages.get(RANDOM.nextInt(drunkMessages.size())).replace("<random_player_name>", randomPlayer.getName()));
    }

}
