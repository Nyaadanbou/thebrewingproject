package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.database.InsertableStoredData;
import dev.jsinco.brewery.database.RemovableStoredData;
import dev.jsinco.brewery.database.RetrievableStoredData;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DistilleryDataType implements RetrievableStoredData<Distillery>, InsertableStoredData<Distillery>, RemovableStoredData<Distillery> {
    @Override
    public void insert(Distillery value, Connection connection) throws SQLException {

    }

    @Override
    public void remove(Distillery toRemove, Connection connection) throws SQLException {

    }

    @Override
    public List<Distillery> retrieveAll(Connection connection, UUID world) throws SQLException {
        return List.of();
    }
}
