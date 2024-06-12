package it.mikeslab.commons.api.database;

import java.util.Map;

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
    String getIdentifierName();

    /**
     * @return Returns the identifier field value
     */
    Object getIdentifierValue();

}
