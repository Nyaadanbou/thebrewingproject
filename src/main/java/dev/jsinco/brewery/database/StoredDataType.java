package dev.jsinco.brewery.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface StoredDataType<T> {

    void update(T newValue, Connection connection) throws SQLException;

    void remove(T toRemove, Connection connection) throws SQLException;

    List<T> retrieveAll(Connection connection) throws SQLException, IOException;
}
