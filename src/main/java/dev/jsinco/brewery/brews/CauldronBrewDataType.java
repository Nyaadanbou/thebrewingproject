package dev.jsinco.brewery.brews;

import dev.jsinco.brewery.database.StoredDataType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CauldronBrewDataType implements StoredDataType<Brew> {
    @Override
    public void update(Brew newValue, Connection connection) throws SQLException {

    }

    @Override
    public void remove(Brew toRemove, Connection connection) throws SQLException {

    }

    @Override
    public List<Brew> retrieveAll(Connection connection) throws SQLException {
        return List.of();
    }
}
