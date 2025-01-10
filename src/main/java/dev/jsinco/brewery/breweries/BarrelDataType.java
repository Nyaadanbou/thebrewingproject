package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.database.StoredDataType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BarrelDataType implements StoredDataType<Barrel> {
    @Override
    public void update(Barrel newValue, Connection connection) throws SQLException {
        
    }

    @Override
    public void remove(Barrel toRemove, Connection connection) throws SQLException {

    }

    @Override
    public List<Barrel> retrieveAll(Connection connection) throws SQLException {
        return List.of();
    }
}
