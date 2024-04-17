package it.mikeslab.widencommons.api.database;

import it.mikeslab.widencommons.api.database.pojo.RetrievedEntry;

import java.util.Optional;

public interface Database<T extends SerializableMapConvertible<T>> {

    /**
     * Connect to the database
     * @return true if the connection is successful, false otherwise
     */
    boolean connect(Class<T> pojoClass);

    /**
     * Check if the connection is active
     * @return true if the connection is active, false otherwise
     */
    boolean isConnected();

    /**
     * Disconnect from the database
     * @return true if the disconnection is successful, false otherwise
     */
    boolean disconnect();

    /**
     * Get a pojo object from the database
     * @param id the id of the object
     * @return the object if it exists, null otherwise
     */
    T get(int id, T pojoClass);

    /**
     * Insert a pojo object into the database
     * @param id the id of the object, optional
     * @param pojoObject the object to insert
     * @return true if the insertion is successful, false otherwise
     */
    int upsert(Optional<Integer> id, T pojoObject);

    /**
     * Delete a pojo object from the database
     * @param id the id of the object
     * @return true if the update is successful, false otherwise
     */
    boolean delete(int id);


    /**
     * Find a pojo object from the database
     * @param pojoObject similar to the object to find
     */
    RetrievedEntry find(T pojoObject);

}
