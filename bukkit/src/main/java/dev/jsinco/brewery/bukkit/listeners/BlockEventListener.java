package dev.jsinco.brewery.bukkit.listeners;

import dev.jsinco.brewery.bukkit.breweries.BreweryRegistry;
import dev.jsinco.brewery.bukkit.breweries.*;
import dev.jsinco.brewery.bukkit.structure.BreweryStructure;
import dev.jsinco.brewery.bukkit.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.bukkit.structure.StructureRegistry;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.sql.SQLException;
import java.util.*;

public class BlockEventListener implements Listener {

    private final StructureRegistry structureRegistry;
    private final PlacedStructureRegistry<PlacedBreweryStructure> placedStructureRegistry;
    private final Database database;
    private final BreweryRegistry breweryRegistry;

    public BlockEventListener(StructureRegistry structureRegistry, PlacedStructureRegistry<PlacedBreweryStructure> placedStructureRegistry, Database database, BreweryRegistry breweryRegistry) {
        this.structureRegistry = structureRegistry;
        this.placedStructureRegistry = placedStructureRegistry;
        this.database = database;
        this.breweryRegistry = breweryRegistry;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChangeEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        if (!Objects.equals(lines[0], "Barrel") || !lines[1].isEmpty() || !lines[2].isEmpty() || !lines[3].isEmpty()) {
            return;
        }
        if (!(event.getBlock().getBlockData() instanceof WallSign wallSign)) {
            return;
        }
        Optional<PlacedBreweryStructure> possibleStructure = getStructure(event.getBlock().getRelative(wallSign.getFacing().getOppositeFace()));
        if (possibleStructure.isEmpty()) {
            return;
        }
        PlacedBreweryStructure placedBreweryStructure = possibleStructure.get();
        if (!placedStructureRegistry.getStructures(placedBreweryStructure.positions()).isEmpty()) {
            // Exit if there's an overlapping structure
            return;
        }
        placedStructureRegistry.registerStructure(placedBreweryStructure);
        BukkitBarrel barrel = BreweryFactory.newBarrel(placedBreweryStructure, event.getBlock().getLocation());
        placedBreweryStructure.setHolder(barrel);
        placedStructureRegistry.registerPosition(BukkitAdapter.toBreweryLocation(event.getBlock()), barrel);
        try {
            database.insertValue(BukkitBarrelDataType.INSTANCE, barrel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        destroyFromBlock(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        onMultiBlockRemove(event.getBlocks().stream()
                .map(block -> block.getRelative(event.getDirection()))
                .map(Block::getLocation)
                .toList());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        onMultiBlockRemove(event.getBlocks().stream()
                .map(block -> block.getRelative(event.getDirection()))
                .map(Block::getLocation)
                .toList()
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        onMultiBlockRemove(event.blockList().stream()
                .map(Block::getLocation)
                .toList()
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        onMultiBlockRemove(event.blockList().stream()
                .map(Block::getLocation)
                .toList()
        );
    }

    private void onMultiBlockRemove(List<Location> locationList) {
        Set<PlacedBreweryStructure> placedBreweryStructures = new HashSet<>();
        Set<BreweryLocation> structurePositions = new HashSet<>();
        Set<BukkitBarrel> barrels = new HashSet<>();
        for (Location location : locationList) {
            BreweryLocation breweryLocation = BukkitAdapter.toBreweryLocation(location);
            Optional<PlacedBreweryStructure> placedBreweryStructure = placedStructureRegistry.getStructure(breweryLocation);
            placedBreweryStructure.ifPresent(placedBreweryStructures::add);
            placedStructureRegistry.getHolder(breweryLocation).ifPresent(barrel -> {
                structurePositions.add(breweryLocation);
                barrels.add((BukkitBarrel) barrel);
            });
            breweryRegistry.getActiveCauldron(breweryLocation).ifPresent(cauldron -> ListenerUtil.removeCauldron(cauldron, breweryRegistry, database));
        }
        placedBreweryStructures.forEach(placedStructureRegistry::removeStructure);
        placedBreweryStructures.stream()
                .map(PlacedBreweryStructure::getHolder)
                .filter(Objects::nonNull)
                .map(BukkitBarrel.class::cast)
                .forEach(barrels::add);
        structurePositions.forEach(placedStructureRegistry::removePosition);
        for (BukkitBarrel barrel : barrels) {
            barrel.destroy();
            placedStructureRegistry.removeStructure(barrel.getStructure());
            try {
                database.remove(BukkitBarrelDataType.INSTANCE, barrel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        destroyFromBlock(event.getBlock());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        destroyFromBlock(event.getBlock());
    }

    /**
     * Assumes only one block has changed in the event, is not safe to use in multi-block changes
     *
     * @param block
     */
    private void destroyFromBlock(Block block) {
        BreweryLocation breweryLocation = BukkitAdapter.toBreweryLocation(block);
        Optional<PlacedBreweryStructure> placedBreweryStructure = placedStructureRegistry.getStructure(breweryLocation);
        placedBreweryStructure.ifPresent(placedStructureRegistry::removeStructure);
        placedStructureRegistry.getHolder(breweryLocation)
                .map(BukkitBarrel.class::cast)
                .ifPresent(barrel -> {
                    placedStructureRegistry.removePosition(breweryLocation);
                    barrel.destroy();
                    placedStructureRegistry.removeStructure(barrel.getStructure());
                    try {
                        database.remove(BukkitBarrelDataType.INSTANCE, barrel);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
        breweryRegistry.getActiveCauldron(breweryLocation).ifPresent(cauldron -> ListenerUtil.removeCauldron(cauldron, breweryRegistry, database));
    }
}
