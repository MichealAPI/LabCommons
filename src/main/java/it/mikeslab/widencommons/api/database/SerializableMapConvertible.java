package it.mikeslab.widencommons.api.database;

import java.util.Map;

public interface SerializableMapConvertible<T> {

    T fromMap(Map<String, Object> map);

    Map<String, Object> toMap();



}
