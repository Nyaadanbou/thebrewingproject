package dev.jsinco.brewery.listeners;

import dev.jsinco.brewery.structure.BreweryStructure;
import dev.jsinco.brewery.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import dev.jsinco.brewery.structure.StructureRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Optional;
import java.util.Set;

public class BlockEventListener implements Listener {

    private final StructureRegistry structureRegistry;
    private final PlacedStructureRegistry placedStructureRegistry;

    public BlockEventListener(StructureRegistry structureRegistry, PlacedStructureRegistry placedStructureRegistry) {
        this.structureRegistry = structureRegistry;
        this.placedStructureRegistry = placedStructureRegistry;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Optional<PlacedBreweryStructure> possibleStructure = getStructure(event.getBlock());
        if (possibleStructure.isEmpty()) {
            return;
        }
        PlacedBreweryStructure placedBreweryStructure = possibleStructure.get();
        if (!placedStructureRegistry.getStructures(placedBreweryStructure.getPositions()).isEmpty()) {
            // Exit if there's an overlapping structure
            return;
        }
        placedStructureRegistry.addStructure(possibleStructure.get());
    }

    private Optional<PlacedBreweryStructure> getStructure(Block block) {
        Location placedLocation = block.getLocation();
        Material material = block.getType();
        Set<BreweryStructure> possibleStructures = structureRegistry.getPossibleStructures(material);
        for (BreweryStructure structure : possibleStructures) {
            Optional<PlacedBreweryStructure> placedBreweryStructure = PlacedBreweryStructure.findValid(structure, placedLocation);
            if (placedBreweryStructure.isPresent()) {
                return placedBreweryStructure;
            }
        }
        return Optional.empty();
    }
}
