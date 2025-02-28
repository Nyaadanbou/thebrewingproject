package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.database.Database;
import org.bukkit.block.Block;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockBukkitExtension.class)
class BukkitDistilleryDataTypeTest {

    TheBrewingProject plugin;
    Database database;
    @MockBukkitInject
    ServerMock server;
    WorldMock world;

    @BeforeEach
    void setUp() {
        this.plugin = MockBukkit.load(TheBrewingProject.class);
        this.database = plugin.getDatabase();
        this.world = server.addSimpleWorld("world");
    }

    @Test
    void insertAndFetch() throws SQLException, IOException {
        Block block = world.getBlockAt(0, 1, 0);
        BukkitDistillery distillery = new BukkitDistillery(block, 100);
        database.insertValue(BukkitDistilleryDataType.INSTANCE, distillery);
        List<BukkitDistillery> distilleries = database.retrieveAll(BukkitDistilleryDataType.INSTANCE, world.getUID());
        assertEquals(1, distilleries.size());
        BukkitDistillery found = distilleries.get(0);
        assertEquals(distillery.getLocation(), found.getLocation());
        assertEquals(100, found.getStartTime());
        BukkitDistillery newDistillery = new BukkitDistillery(block, 200);
        database.updateValue(BukkitDistilleryDataType.INSTANCE, newDistillery);
        List<BukkitDistillery> fetchWithUpdatedValue = database.retrieveAll(BukkitDistilleryDataType.INSTANCE, world.getUID());
        assertEquals(1, distilleries.size());
        assertEquals(newDistillery.getLocation(), fetchWithUpdatedValue.get(0).getLocation());
        assertEquals(200, fetchWithUpdatedValue.get(0).getStartTime());
        database.remove(BukkitDistilleryDataType.INSTANCE, newDistillery);
        assertTrue(database.retrieveAll(BukkitDistilleryDataType.INSTANCE, world.getUID()).isEmpty());
    }
}