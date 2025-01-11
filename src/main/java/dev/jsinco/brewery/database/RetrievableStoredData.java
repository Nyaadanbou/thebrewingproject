package dev.jsinco.brewery.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface RetrievableStoredData<T> {


    List<T> retrieveAll(Connection connection) throws SQLException;
}
