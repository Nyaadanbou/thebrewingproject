package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.InventoryAccessible;
import dev.jsinco.brewery.structure.StructureType;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class BreweryRegistry {

    private final Map<BreweryLocation, BukkitCauldron> activeCauldrons = new HashMap<>();
    private final Map<StructureType, Set<InventoryAccessible<ItemStack, Inventory>>> opened = new HashMap<>();
    private final Map<Inventory, InventoryAccessible<ItemStack, Inventory>> inventories = new HashMap<>();

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

    public <H extends InventoryAccessible<ItemStack, Inventory>> Collection<H> getOpened(StructureType structureType) {
        return (Collection<H>) opened.computeIfAbsent(structureType, ignored -> new HashSet<>());
    }

    public <H extends InventoryAccessible<ItemStack, Inventory>> void registerOpened(H holder) {
        StructureType structureType = getStructureType(holder);
        opened.computeIfAbsent(structureType, ignored -> new HashSet<>()).add(holder);
    }

    public <H extends InventoryAccessible<ItemStack, Inventory>> void unregisterOpened(H holder) {
        StructureType structureType = getStructureType(holder);
        opened.computeIfAbsent(structureType, ignored -> new HashSet<>()).remove(holder);
    }

    private <H> StructureType getStructureType(H holder) {
        for (StructureType structureType : Registry.STRUCTURE_TYPE.values()) {
            if (structureType.tClass().isInstance(holder)) {
                return structureType;
            }
        }
        throw new IllegalArgumentException("Holder does not have a matching structure type!");
    }

    public @Nullable InventoryAccessible<ItemStack, Inventory> getFromInventory(Inventory inventory) {
        return inventories.get(inventory);
    }

    public void registerInventory(InventoryAccessible<ItemStack, Inventory> inventoryAccessible) {
        inventoryAccessible.getInventories().forEach(inventory -> inventories.put(inventory, inventoryAccessible));
    }

    public void unregisterInventory(InventoryAccessible<ItemStack, Inventory> inventoryAccessible) {
        inventoryAccessible.getInventories().forEach(inventories::remove);
    }
}
