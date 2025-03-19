package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.bukkit.breweries.*;
import dev.jsinco.brewery.database.Database;
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
}
