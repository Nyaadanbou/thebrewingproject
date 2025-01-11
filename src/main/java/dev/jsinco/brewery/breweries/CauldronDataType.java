package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.database.InsertableStoredData;
import dev.jsinco.brewery.database.RemovableStoredData;
import dev.jsinco.brewery.database.RetrievableStoredData;
import dev.jsinco.brewery.database.UpdateableStoredData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CauldronDataType implements RetrievableStoredData<Cauldron>, InsertableStoredData<Cauldron>, UpdateableStoredData<Cauldron>, RemovableStoredData<Cauldron> {
    @Override
    public void insert(Cauldron value, Connection connection) throws SQLException {

    }

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
