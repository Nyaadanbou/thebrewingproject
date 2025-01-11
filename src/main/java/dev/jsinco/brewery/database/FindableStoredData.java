package dev.jsinco.brewery.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface FindableStoredData<T, U> {

    List<T> find(U searchObject, Connection connection) throws SQLException;
}
