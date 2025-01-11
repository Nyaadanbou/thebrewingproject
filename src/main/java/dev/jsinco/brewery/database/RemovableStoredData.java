package dev.jsinco.brewery.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface RemovableStoredData<T> {

    void remove(T toRemove, Connection connection) throws SQLException;

}
