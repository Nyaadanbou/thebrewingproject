package dev.jsinco.brewery.database;

public interface SingletonStoredData<T, C> {

    T value(C connection) throws PersistenceException;

    void update(T t, C connection) throws PersistenceException;
}
