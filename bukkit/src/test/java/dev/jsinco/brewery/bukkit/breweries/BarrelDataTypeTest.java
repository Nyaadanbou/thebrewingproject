package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.bukkit.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.bukkit.structure.StructurePlacerUtils;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.moment.PassedMoment;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.MockBukkitExtension;
import org.mockbukkit.mockbukkit.MockBukkitInject;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockBukkitExtension.class)
class BarrelDataTypeTest {
    @MockBukkitInject
    ServerMock server;
    private @NotNull WorldMock world;
    private Database database;

    @BeforeEach
    void setUp() {
        this.world = server.addSimpleWorld("hello!");
        TheBrewingProject theBrewingProject = MockBukkit.load(TheBrewingProject.class);
        this.database = theBrewingProject.getDatabase();
    }

    @Test
    void checkPersistence() throws IOException, SQLException {
        StructurePlacerUtils.constructSmallOakBarrel(world);
        Location barrelBlock = new Location(world, -3, 1, 2);
        Optional<PlacedBreweryStructure> breweryStructureOptional = TheBrewingProject.getInstance().getStructureRegistry().getPossibleStructures(barrelBlock.getBlock().getType())
                .stream().map(breweryStructure -> PlacedBreweryStructure.findValid(breweryStructure, barrelBlock))
                .filter(Optional::isPresent).map(Optional::get).findFirst();
        BukkitBarrel barrel = new BukkitBarrel(new Location(world, 1, 2, 3), breweryStructureOptional.get(), 9, BarrelType.OAK);
        barrel.setBrews(List.of(
                new Pair<>(new Brew<>(new PassedMoment(10), Map.of(), new Interval(10, 10), 0, CauldronType.WATER, BarrelType.OAK), 4),
                new Pair<>(new Brew<>(new PassedMoment(10), Map.of(), new Interval(10, 10), 0, CauldronType.WATER, BarrelType.OAK), 5)
        ));
        database.insertValue(BukkitBarrelDataType.INSTANCE, barrel);
        List<BukkitBarrel> retrievedBarrels = database.retrieveAll(BukkitBarrelDataType.INSTANCE, world.getUID());
        assertEquals(1, retrievedBarrels.size());
        BukkitBarrel retrievedBarrel = retrievedBarrels.get(0);
        assertEquals(2, retrievedBarrel.getBrews().size());
        database.remove(BukkitBarrelDataType.INSTANCE, barrel);
        assertTrue(database.retrieveAll(BukkitBarrelDataType.INSTANCE, world.getUID()).isEmpty());
    }

}