package dev.jsinco.brewery.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface RetrievableStoredData<T> {


    List<T> retrieveAll(Connection connection, UUID targetWorld) throws SQLException;
}
