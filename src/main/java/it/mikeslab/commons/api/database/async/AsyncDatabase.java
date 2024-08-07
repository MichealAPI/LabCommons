package it.mikeslab.commons.api.database.async;

import it.mikeslab.commons.api.database.SerializableMapConvertible;
import org.bson.Document;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * An asynchronous database interface
 * @param <T> the type of the pojo object
 */
public interface AsyncDatabase<T extends SerializableMapConvertible<T>> {

    /**
     * Connect to the database
     * @return a {@link CompletableFuture} true if the connection is successful, false otherwise
     */
    CompletableFuture<Boolean> connect(T pojoObject);

    /**
     * Check if the connection is active
     * @return a {@link CompletableFuture} true if the connection is active, false otherwise
     */
    CompletableFuture<Boolean> isConnected();

    /**
     * Disconnect from the database
     * @return a {@link CompletableFuture} true if the disconnection is successful, false otherwise
     */
    CompletableFuture<Boolean> disconnect();

    /**
     * Insert a pojo object into the database
     * @param pojoObject the object to insert
     * @return a {@link CompletableFuture} true if the insertion is successful, false otherwise
     */
    CompletableFuture<Boolean> upsert(T pojoObject);

    /**
     * Delete a pojo object from the database
     * @param pojoObject the object to delete, functions as a filter
     * @return a {@link CompletableFuture} true if the update is successful, false otherwise
     */
    CompletableFuture<Boolean> delete(T pojoObject);


    /**
     * Find a pojo object from the database
     * @param pojoObject similar to the object to find
     */
    CompletableFuture<T> findOne(T pojoObject);

    /**
     * Find all pojo objects from the database
     * @param pojoObject similar to the object to find
     */
    CompletableFuture<List<T>> findMany(T pojoObject);

    /**
     * Find a document from the database
     * @param document similar to the document to find
     * @return the document
     */
    CompletableFuture<Document> findDocument(Document document);

    /**
     * Find all documents from the database
     * @param document similar to the document to find
     * @return the document
     */
    CompletableFuture<List<Document>> findDocuments(Document document);

}
