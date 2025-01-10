package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.database.StoredDataType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DistilleryDataType implements StoredDataType<Distillery> {
    @Override
    public void update(Distillery newValue, Connection connection) throws SQLException {

    }

    @Override
    public void remove(Distillery toRemove, Connection connection) throws SQLException {

    }

    @Override
    public List<Distillery> retrieveAll(Connection connection) throws SQLException {
        return List.of();
    }
}
