package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.InventoryAccessible;
import dev.jsinco.brewery.structure.StructureType;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.vector.BreweryLocation;

import java.util.*;

public final class BreweryRegistry {

    private final Map<BreweryLocation, BukkitCauldron> activeCauldrons = new HashMap<>();
    private final Map<StructureType<?>, Set<InventoryAccessible>> opened = new HashMap<>();

    public Optional<BukkitCauldron> getActiveCauldron(BreweryLocation position) {
        return Optional.ofNullable(activeCauldrons.get(position));
    }

    public void addActiveCauldron(BukkitCauldron cauldron) {
        activeCauldrons.put(cauldron.position(), cauldron);
    }

    public void removeActiveCauldron(BukkitCauldron cauldron) {
        activeCauldrons.remove(cauldron.position());
    }

    public Collection<BukkitCauldron> getActiveCauldrons() {
        return activeCauldrons.values();
    }

    public <H extends InventoryAccessible> Collection<H> getOpened(StructureType<H> structureType) {
        return (Collection<H>) opened.computeIfAbsent(structureType, ignored -> new HashSet<>());
    }

    public <H extends InventoryAccessible> void registerOpened(H holder) {
        StructureType<H> structureType = getStructureType(holder);
        opened.computeIfAbsent(structureType, ignored -> new HashSet<>()).add(holder);
    }

    public <H extends InventoryAccessible> void unregisterOpened(H holder) {
        StructureType<H> structureType = getStructureType(holder);
        opened.computeIfAbsent(structureType, ignored -> new HashSet<>()).remove(holder);
    }

    private <H> StructureType<H> getStructureType(H holder) {
        for (StructureType<?> structureType : Registry.STRUCTURE_TYPE.values()) {
            if (structureType.tClass().isInstance(holder)) {
                return (StructureType<H>) structureType;
            }
        }
        throw new IllegalArgumentException("Holder does not have a matching structure type!");
    }

}
