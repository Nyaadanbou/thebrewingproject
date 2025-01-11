package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.database.InsertableStoredData;
import dev.jsinco.brewery.database.RemovableStoredData;
import dev.jsinco.brewery.database.RetrievableStoredData;
import dev.jsinco.brewery.util.FileUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class BarrelDataType implements RetrievableStoredData<Barrel>, RemovableStoredData<Barrel>, InsertableStoredData<Barrel> {
    @Override
    public void insert(Barrel value, Connection connection) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/barrels_insert.sql"))) {

        }
    }

    @Override
    public void remove(Barrel toRemove, Connection connection) throws SQLException {

    }

    @Override
    public List<Barrel> retrieveAll(Connection connection) throws SQLException {
        return List.of();
    }
}
