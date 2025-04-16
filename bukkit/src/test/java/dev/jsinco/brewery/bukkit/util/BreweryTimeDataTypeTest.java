package dev.jsinco.brewery.bukkit.util;

import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.database.PersistenceException;
import dev.jsinco.brewery.database.sql.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.MockBukkitExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockBukkitExtension.class)
class BreweryTimeDataTypeTest {

    Database database;

    @BeforeEach
    void setUp() {
        this.database = MockBukkit.load(TheBrewingProject.class).getDatabase();
    }

    @Test
    void checkPersistence() throws PersistenceException {
        this.database.setSingleton(BreweryTimeDataType.INSTANCE, 10L);
        assertEquals(10L, this.database.getSingleton(BreweryTimeDataType.INSTANCE));
        this.database.setSingleton(BreweryTimeDataType.INSTANCE, 20L);
        assertEquals(20L, this.database.getSingleton(BreweryTimeDataType.INSTANCE));
    }
}