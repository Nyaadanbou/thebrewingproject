package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.bukkit.breweries.*;
import dev.jsinco.brewery.database.PersistenceException;
import dev.jsinco.brewery.database.sql.Database;
import dev.jsinco.brewery.structure.SinglePositionStructure;
import org.jetbrains.annotations.NotNull;

public class ListenerUtil {

    public static void removeActiveSinglePositionStructure(@NotNull SinglePositionStructure structure, BreweryRegistry registry, Database database) {
        registry.removeActiveSinglePositionStructure(structure);
        if (structure instanceof BukkitCauldron cauldron) {
            try {
                database.remove(BukkitCauldronDataType.INSTANCE, cauldron);
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
        }
        if (structure instanceof BukkitMixer mixer) {
            try {
                database.remove(BukkitMixerDataType.INSTANCE, mixer);
            } catch (PersistenceException e) {
                e.printStackTrace();
            }
        }
    }
}
