package dev.jsinco.brewery.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface InsertableStoredData<T> {
    void insert(T value, Connection connection) throws SQLException;


}
