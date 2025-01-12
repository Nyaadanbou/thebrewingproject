package dev.jsinco.brewery.listeners;

import dev.jsinco.brewery.breweries.BreweryRegistry;
import dev.jsinco.brewery.breweries.Cauldron;
import dev.jsinco.brewery.breweries.CauldronDataType;
import dev.jsinco.brewery.database.Database;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class ListenerUtil {

    public static void removeCauldron(@NotNull Cauldron cauldron, BreweryRegistry registry, Database database) {
        registry.removeActiveCauldron(cauldron);
        try {
            database.remove(CauldronDataType.DATA_TYPE, cauldron);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
