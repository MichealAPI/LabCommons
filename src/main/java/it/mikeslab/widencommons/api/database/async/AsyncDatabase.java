package it.mikeslab.widencommons.api.database.async;

import it.mikeslab.widencommons.api.database.pojo.RetrievedEntry;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * An asynchronous database interface
 * @param <T> the type of the pojo object
 */
public interface AsyncDatabase<T> {

    /**
     * Connect to the database
     * @return true if the connection is successful, false otherwise
     */
    CompletableFuture<Boolean> connect(Class<?> pojoClass);

    /**
     * Check if the connection is active
     * @return true if the connection is active, false otherwise
     */
    CompletableFuture<Boolean> isConnected();

    /**
     * Disconnect from the database
     * @return true if the disconnection is successful, false otherwise
     */
    CompletableFuture<Boolean> disconnect();

    /**
     * Get a pojo object from the database
     * @param id the id of the object
     * @return the object if it exists, null otherwise
     */
    CompletableFuture<T> get(int id, Class<T> pojoClass);

    /**
     * Insert a pojo object into the database
     * @param id the id of the object, optional
     * @param pojoObject the object to insert
     * @return true if the insertion is successful, false otherwise
     */
    CompletableFuture<Integer> upsert(Optional<Integer> id, Object pojoObject);

    /**
     * Delete a pojo object from the database
     * @param id the id of the object
     * @return true if the update is successful, false otherwise
     */
    CompletableFuture<Boolean> delete(int id);


    /**
     * Find a pojo object from the database
     * @param pojoObject similar to the object to find
     */
    CompletableFuture<RetrievedEntry> find(Object pojoObject);

}
