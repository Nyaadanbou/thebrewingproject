package dev.jsinco.brewery.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface UpdateableStoredData<T> {
    void update(T newValue, Connection connection) throws SQLException;

}
