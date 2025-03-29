package dev.jsinco.brewery.bukkit.effect;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.util.MessageUtil;
import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.effect.DrunkEvent;
import dev.jsinco.brewery.effect.DrunkManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

public class DrunkEventAction {
    private static final Random RANDOM = new Random();

    public static void doDrunkEvent(UUID playerUuid, DrunkEvent event) {
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) {
            return;
        }
        if (event == DrunkEvent.PUKE) {
            PukeHandler pukeHandler = new PukeHandler(Config.PUKE_TIME, player);
            Bukkit.getScheduler().runTaskTimer(TheBrewingProject.getInstance(), pukeHandler::doPuke, 0, 1);
        }
        if (event == DrunkEvent.KICK) {
            DrunkManager drunkManager = TheBrewingProject.getInstance().getDrunkManager();
            player.kick(MessageUtil.compilePlayerMessage(Config.KICK_EVENT_MESSAGE == null ? TranslationsConfig.KICK_EVENT_MESSAGE : Config.KICK_EVENT_MESSAGE, player, drunkManager, 0));
            if (Config.KICK_EVENT_SERVER_MESSAGE != null) {
                Component message = MessageUtil.compilePlayerMessage(Config.KICK_EVENT_SERVER_MESSAGE, player, drunkManager, 0);
                Bukkit.getOnlinePlayers().forEach(player1 -> player1.sendMessage(message));
            }
            drunkManager.registerPassedOut(player.getUniqueId());
        }
    }


    private static class PukeHandler {
        private int countDown;
        private final Player player;

        private PukeHandler(int pukingTicks, Player player) {
            this.countDown = pukingTicks;
            this.player = player;
        }

        private void doPuke(BukkitTask task) {
            if (!player.isOnline() || countDown-- <= 0) {
                task.cancel();
                return;
            }
            Location loc = player.getLocation();
            loc.setY(loc.getY() + 1.1);
            loc.setPitch(player.getPitch() + RANDOM.nextInt(-10, 11));
            loc.setYaw(player.getYaw() + RANDOM.nextInt(-10, 11));
            Vector direction = loc.getDirection();
            direction.multiply(0.5);
            loc.add(direction);
            Item item = player.getWorld().dropItem(loc, new ItemStack(Material.SOUL_SAND));
            item.setVelocity(direction);
            item.setPersistent(false);
            item.setPickupDelay(32767);

            World world = loc.getWorld();
            YamlConfiguration spigotConfig = Bukkit.spigot().getConfig();
            int worldDespawnRate = spigotConfig.getInt("world-settings." + world.getName() + ".item-despawn-rate", -1);
            if (worldDespawnRate < 0) {
                worldDespawnRate = spigotConfig.getInt("world-settings.default.item-despawn-rate", 6000);
            }
            int despawnRate = Math.max(Config.PUKE_DESPAWN_RATE, 4);
            item.setTicksLived(worldDespawnRate - despawnRate + RANDOM.nextInt(-despawnRate / 2, despawnRate / 2 + 1));
        }
    }
}
