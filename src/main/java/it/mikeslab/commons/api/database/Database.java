package it.mikeslab.commons.api.database;

import org.bson.Document;

import java.util.List;

public interface Database<T extends SerializableMapConvertible<T>> {

    /**
     * Connect to the database
     * @return true if the connection is successful, false otherwise
     */
    boolean connect(T pojoClass);

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
     * Insert a pojo object into the database
     * @param pojoObject the object to insert
     * @return true if the insertion is successful, false otherwise
     */
    boolean upsert(T pojoObject);

    /**
     * Delete a pojo object from the database
     * @param pojoObject the object to delete
     * @return true if the update is successful, false otherwise
     */
    boolean delete(T pojoObject);


    /**
     * Find a pojo object from the database
     * @param pojoObject similar to the object to find
     */
    T findOne(T pojoObject);

    /**
     * Find all pojo objects from the database
     * @param pojoObject similar to the object to find
     */
    List<T> findMany(T pojoObject);

    /**
     * Find all pojo objects from the database
     * @param document similar to the document to find
     * @return the document
     */
    Document findDocument(Document document);

    /**
     * Find all pojo objects from the database
     * @param document similar to the document to find
     * @return the document
     */
    List<Document> findDocuments(Document document);


}
