package it.mikeslab.commons.api.database;

import java.util.Map;
import java.util.Set;

public interface SerializableMapConvertible<T> {

    /**
     * Deserializes the object from a map
     * @param map the map representation of the object
     * @return the object
     */
    T fromMap(Map<String, Object> map);

    /**
     * Serializes the object into a map
     * @return the map representation of the object
     */
    Map<String, Object> toMap();

    /**
     * @return Returns the identifier field name
     */
    String getUniqueIdentifierName();

    /**
     * @return Returns the identifier field value
     */
    Object getUniqueIdentifierValue();

    /**
     * @return Returns the values identifiers
     */
    Set<String> identifiers();

}
