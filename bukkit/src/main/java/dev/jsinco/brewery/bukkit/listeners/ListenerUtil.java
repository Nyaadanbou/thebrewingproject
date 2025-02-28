package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.breweries.Distillery;
import dev.jsinco.brewery.bukkit.breweries.*;
import dev.jsinco.brewery.database.Database;
import org.bukkit.block.BrewingStand;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class ListenerUtil {

    public static void removeCauldron(@NotNull BukkitCauldron BUkkitCauldron, BreweryRegistry registry, Database database) {
        registry.removeActiveCauldron(BUkkitCauldron);
        try {
            database.remove(BukkitCauldronDataType.INSTANCE, BUkkitCauldron);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateDistillery(BukkitDistillery bukkitDistillery, BreweryRegistry registry, Database database) {
        Distillery.State state = bukkitDistillery.getState();
        if (state == Distillery.State.INVALID) {
            registry.removeDistillery(bukkitDistillery);
            try {
                database.remove(BukkitDistilleryDataType.INSTANCE, bukkitDistillery);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }
        bukkitDistillery.applyToBlock(state);
    }
}
