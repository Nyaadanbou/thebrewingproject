package dev.jsinco.brewery.bukkit.effect.event;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.bukkit.util.MessageUtil;
import dev.jsinco.brewery.configuration.Config;
import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.effect.DrunkState;
import dev.jsinco.brewery.effect.DrunksManager;
import dev.jsinco.brewery.effect.event.NamedDrunkEvent;
import dev.jsinco.brewery.util.moment.Moment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NamedDrunkEventExecutor {
    private static final Random RANDOM = new Random();
    private static final int STUMBLE_DURATION = 10;

    public static void doDrunkEvent(UUID playerUuid, NamedDrunkEvent event) {
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) {
            return;
        }
        switch (event) {
            case PUKE -> {
                PukeHandler pukeHandler = new PukeHandler(Config.PUKE_TIME, player);
                Bukkit.getScheduler().runTaskTimer(TheBrewingProject.getInstance(), pukeHandler::doPuke, 0, 1);
            }
            case PASS_OUT -> {
                DrunksManager drunksManager = TheBrewingProject.getInstance().getDrunksManager();
                player.kick(MessageUtil.compilePlayerMessage(Config.KICK_EVENT_MESSAGE == null ? TranslationsConfig.KICK_EVENT_MESSAGE : Config.KICK_EVENT_MESSAGE, player, drunksManager, 0));
                if (Config.KICK_EVENT_SERVER_MESSAGE != null) {
                    Component message = MessageUtil.compilePlayerMessage(Config.KICK_EVENT_SERVER_MESSAGE, player, drunksManager, 0);
                    Bukkit.getOnlinePlayers().forEach(player1 -> player1.sendMessage(message));
                }
                drunksManager.registerPassedOut(player.getUniqueId());
            }
            case STUMBLE -> {
                DrunksManager drunksManager = TheBrewingProject.getInstance().getDrunksManager();
                StumbleHandler stumbleHandler = new StumbleHandler(RANDOM.nextInt(STUMBLE_DURATION / 2, STUMBLE_DURATION * 3 / 2 + 1), player, drunksManager);
                Bukkit.getScheduler().runTaskTimer(TheBrewingProject.getInstance(), stumbleHandler::doStumble, 0, 1);
            }
            case CHICKEN -> {
                player.getWorld().spawn(player.getLocation(), Chicken.class, chicken -> {
                    chicken.setEggLayTime(Integer.MAX_VALUE);
                    chicken.setPersistent(false);
                    chicken.setAge(0);
                    chicken.setLootTable(new LootTable() {
                        @Override
                        public @NotNull Collection<ItemStack> populateLoot(@Nullable Random random, @NotNull LootContext context) {
                            return List.of();
                        }

                        @Override
                        public void fillInventory(@NotNull Inventory inventory, @Nullable Random random, @NotNull LootContext context) {

                        }

                        @Override
                        public @NotNull NamespacedKey getKey() {
                            return NamespacedKey.fromString("brewery:empty");
                        }
                    });
                    chicken.setBreed(false);
                });
                player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.CHICKEN_MESSAGE, MessageUtil.getPlayerTagResolver(player)));
            }
            case DRUNK_MESSAGE -> {
                List<String> drunkMessages = Config.DRUNK_MESSAGES;
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
            case TELEPORT -> {
                String teleport = Config.TELEPORT_DESTINATIONS.get(RANDOM.nextInt(Config.TELEPORT_DESTINATIONS.size()));
                Location location = BukkitAdapter.parseLocation(teleport);
                if (location == null) {
                    return;
                }
                player.teleport(location);
                player.sendMessage(MiniMessage.miniMessage().deserialize(TranslationsConfig.TELEPORT_MESSAGE, MessageUtil.getPlayerTagResolver(player)));
            }
            case NAUSEA -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, RANDOM.nextInt(Moment.MINUTE / 2, Moment.MINUTE * 3 / 2), 1));
            }
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

    private static class StumbleHandler {
        private final Vector pushDirection2;
        private int countDown;
        private final int duration;
        private final Player player;
        private final Vector pushDirection1;

        public StumbleHandler(int duration, Player player, DrunksManager drunksManager) {
            this.countDown = duration;
            this.duration = duration;
            this.player = player;
            double radians1 = RANDOM.nextDouble(Math.PI * 2);
            DrunkState drunkState = drunksManager.getDrunkState(player.getUniqueId());
            double maxMagnitude = Math.max(0.1, drunkState == null ? 0 : Math.sqrt(drunkState.walkSpeedSquared()));
            this.pushDirection1 = new Vector(Math.cos(radians1), 0, Math.sin(radians1))
                    .multiply(RANDOM.nextDouble(maxMagnitude));
            double radians2 = RANDOM.nextDouble(Math.PI * 2);
            this.pushDirection2 = new Vector(Math.cos(radians2), 0, Math.sin(radians2))
                    .multiply(RANDOM.nextDouble(maxMagnitude));
        }

        public void doStumble(BukkitTask task) {
            if (!player.isOnline() || countDown-- < 0) {
                task.cancel();
                return;
            }
            if (!player.isOnGround()) {
                return;
            }
            double progress = ((double) duration - (double) countDown) / duration;
            Vector pushDirection = pushDirection2.clone()
                    .multiply(progress)
                    .add(pushDirection1.clone().multiply(1 - progress));
            player.setVelocity(pushDirection);
        }
    }
}
