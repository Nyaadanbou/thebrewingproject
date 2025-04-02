package dev.jsinco.brewery.bukkit.effect.event;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.recipe.RecipeEffect;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.effect.event.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

public class DrunkEventExecutor {

    public static void doDrunkEvent(UUID playerUuid, EventStep event) {
        doDrunkEvents(playerUuid, List.of(event));
    }

    public static void doDrunkEvents(UUID playerUuid, List<EventStep> events) {
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) {
            return;
        }
        for (int i = 0; i < events.size(); i++) {
            EventStep event = events.get(i);
            switch (event) {
                case ApplyPotionEffect applyPotionEffect -> {
                    PotionEffect potionEffect = new RecipeEffect(
                            Registry.EFFECT.get(NamespacedKey.fromString(applyPotionEffect.potionEffectName())),
                            applyPotionEffect.durationBounds(),
                            applyPotionEffect.amplifierBounds()
                    ).newPotionEffect();
                    player.addPotionEffect(potionEffect);
                }
                case NamedDrunkEvent namedDrunkEvent ->
                        NamedDrunkEventExecutor.doDrunkEvent(playerUuid, namedDrunkEvent);
                case CustomEvent customEvent -> doDrunkEvents(playerUuid, customEvent.getSteps());
                case SendCommand sendCommand -> {
                    switch (sendCommand.senderType()) {
                        case PLAYER -> player.performCommand(sendCommand.command());
                        case SERVER ->
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), sendCommand.command());
                    }
                }
                case Teleport teleport -> player.teleport(BukkitAdapter.toLocation(teleport.location()));
                case WaitStep waitStep -> {
                    if (i + 1 >= events.size()) {
                        return;
                    }
                    final List<EventStep> eventsLeft = events.subList(i + 1, events.size());
                    Bukkit.getScheduler().runTaskLater(
                            TheBrewingProject.getInstance(),
                            () -> doDrunkEvents(playerUuid, eventsLeft),
                            waitStep.durationTicks()
                    );
                    return;
                }
            }
        }
    }
}
