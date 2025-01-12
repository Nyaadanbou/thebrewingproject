package dev.jsinco.brewery.listeners;

import dev.jsinco.brewery.breweries.Barrel;
import dev.jsinco.brewery.breweries.BehaviorHolder;
import dev.jsinco.brewery.breweries.BreweryFactory;
import dev.jsinco.brewery.structure.BreweryStructure;
import dev.jsinco.brewery.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.structure.PlacedStructureRegistry;
import dev.jsinco.brewery.structure.StructureRegistry;
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

import java.util.*;

public class BlockEventListener implements Listener {

    private final StructureRegistry structureRegistry;
    private final PlacedStructureRegistry placedStructureRegistry;

    public BlockEventListener(StructureRegistry structureRegistry, PlacedStructureRegistry placedStructureRegistry) {
        this.structureRegistry = structureRegistry;
        this.placedStructureRegistry = placedStructureRegistry;
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
        if (!placedStructureRegistry.getStructures(placedBreweryStructure.getPositions()).isEmpty()) {
            // Exit if there's an overlapping structure
            return;
        }
        placedStructureRegistry.registerStructure(placedBreweryStructure);
        Barrel destroyable = BreweryFactory.newBarrel(placedBreweryStructure, event.getBlock().getLocation());
        placedBreweryStructure.setHolder(destroyable);
        placedStructureRegistry.registerPosition(event.getBlock().getLocation(), destroyable);
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
        Set<Location> structurePositions = new HashSet<>();
        Set<BehaviorHolder> behaviorHolders = new HashSet<>();
        for (Location location : locationList) {
            Optional<PlacedBreweryStructure> placedBreweryStructure = placedStructureRegistry.getStructure(location);
            placedBreweryStructure.ifPresent(placedBreweryStructures::add);
            placedStructureRegistry.getHolder(location).ifPresent(destroyable -> {
                structurePositions.add(location);
                behaviorHolders.add(destroyable);
            });
        }
        placedBreweryStructures.forEach(placedStructureRegistry::removeStructure);
        placedBreweryStructures.stream()
                .map(PlacedBreweryStructure::getHolder)
                .filter(Objects::nonNull)
                .forEach(behaviorHolders::add);
        structurePositions.forEach(placedStructureRegistry::removePosition);
        for (BehaviorHolder behaviorHolder : behaviorHolders) {
            behaviorHolder.destroy();
            behaviorHolder.getStructure().ifPresent(placedStructureRegistry::removeStructure);
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
        Optional<PlacedBreweryStructure> placedBreweryStructure = placedStructureRegistry.getStructure(block.getLocation());
        placedBreweryStructure.ifPresent(this::destroyBreweryStructure);
        placedStructureRegistry.getHolder(block.getLocation()).ifPresent(destroyable -> {
            placedStructureRegistry.removePosition(block.getLocation());
            destroyable.destroy();
            destroyable.getStructure().ifPresent(placedStructureRegistry::removeStructure);
        });

    }

    private void destroyBreweryStructure(PlacedBreweryStructure structure) {
        placedStructureRegistry.removeStructure(structure);
        BehaviorHolder behaviorHolder = structure.getHolder();
        if (behaviorHolder != null) {
            behaviorHolder.destroy();
        }
    }
}
