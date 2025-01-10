package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.database.StoredDataType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CauldronDataType implements StoredDataType<Cauldron> {
    @Override
    public void update(Cauldron newValue, Connection connection) throws SQLException {

    }

    @Override
    public void remove(Cauldron toRemove, Connection connection) throws SQLException {

    }

    @Override
    public List<Cauldron> retrieveAll(Connection connection) throws SQLException {
        return List.of();
    }
}
