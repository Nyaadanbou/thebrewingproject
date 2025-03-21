package dev.jsinco.brewery.bukkit.effect;

import dev.jsinco.brewery.effect.DrunkEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DrunkEventAction {


    public static void doDrunkEvent(UUID playerUuid, DrunkEvent event) {
        Player player = Bukkit.getPlayer(playerUuid);
        if(player == null) {
            return;
        }
        player.sendMessage("Triggered drunk event: " + event);
    }
}
