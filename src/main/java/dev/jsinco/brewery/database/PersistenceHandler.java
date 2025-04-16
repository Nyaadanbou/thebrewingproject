package dev.jsinco.brewery.database;

import java.util.Collection;
import java.util.List;

public interface PersistenceHandler<C> {

    <T> List<T> retrieveAll(RetrievableStoredData<T, C> dataType) throws PersistenceException;

    <T> void remove(RemovableStoredData<T, C> dataType, T toRemove) throws PersistenceException;

    <T> void updateValue(UpdateableStoredData<T, C> dataType, T newValue) throws PersistenceException;

    <T> void updateValues(UpdateableStoredData<T, C> dataType, Collection<T> newValues) throws PersistenceException;

    <T> void insertValue(InsertableStoredData<T, C> dataType, T value) throws PersistenceException;

    <T, U> List<T> find(FindableStoredData<T, U, C> dataType, U searchObject) throws PersistenceException;

    <T> T getSingleton(SingletonStoredData<T, C> dataType) throws PersistenceException;

    <T> void setSingleton(SingletonStoredData<T, C> dataType, T t) throws PersistenceException;
}
