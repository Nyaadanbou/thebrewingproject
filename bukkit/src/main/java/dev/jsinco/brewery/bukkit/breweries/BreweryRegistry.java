package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.util.vector.BreweryLocation;

import java.util.*;

public final class BreweryRegistry {

    private final Map<BreweryLocation, BukkitCauldron> activeCauldrons = new HashMap<>();
    private final Set<BukkitBarrel> openedBarrels = new HashSet<>();
    private final Set<BukkitDistillery> openedDistilleries = new HashSet<>();
    private final Map<BreweryLocation, BukkitDistillery> distilleries = new HashMap<>();

    public Optional<BukkitCauldron> getActiveCauldron(BreweryLocation position) {
        return Optional.ofNullable(activeCauldrons.get(position));
    }

    public void addActiveCauldron(BukkitCauldron cauldron) {
        activeCauldrons.put(cauldron.position(), cauldron);
    }

    public void registerOpenedBarrel(BukkitBarrel barrel) {
        openedBarrels.add(barrel);
    }

    public Collection<BukkitBarrel> getOpenedBarrels() {
        return openedBarrels;
    }

    public void removeActiveCauldron(BukkitCauldron cauldron) {
        activeCauldrons.remove(cauldron.position());
    }

    public void removeOpenedBarrel(BukkitBarrel barrel) {
        openedBarrels.remove(barrel);
    }

    public Collection<BukkitCauldron> getActiveCauldrons() {
        return activeCauldrons.values();
    }

    public Optional<BukkitDistillery> getDistillery(BreweryLocation position) {
        return Optional.ofNullable(distilleries.get(position));
    }

    public boolean isOpened(BreweryLocation position) {
        return openedDistilleries.contains(position);
    }

    public Collection<BukkitDistillery> getOpenedDistilleries() {
        return openedDistilleries;
    }

    public void removeDistillery(BukkitDistillery distillery) {
        distilleries.remove(distillery.getLocation());
        openedDistilleries.remove(distillery);
    }

    public void addOpenDistillery(BukkitDistillery distillery) {
        openedDistilleries.add(distillery);
    }

    public void addDistillery(BukkitDistillery distillery) {
        distilleries.put(distillery.getLocation(), distillery);
    }

    public void removeOpenedDistillery(BukkitDistillery distillery) {
        openedDistilleries.remove(distillery);
    }
}
