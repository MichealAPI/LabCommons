package it.mikeslab.commons.api.database.util;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SimpleMapConvertible<K, E extends Enum<E> & SimpleMapConvertible.SimpleIdentifiers> {

    private final E uniqueField;

    @Getter
    private final Set<String> identifiers;

    private final Map<String, Object> data = new HashMap<>();

    /** No-arg instance: identifiers only, no initial unique ID set */
    protected SimpleMapConvertible(Class<E> enumClass, E uniqueField) {
        this.uniqueField = uniqueField;
        this.identifiers = Collections.unmodifiableSet(
                Arrays.stream(enumClass.getEnumConstants())
                        .map(SimpleIdentifiers::getKey)
                        .collect(Collectors.toSet())
        );
    }

    /** Store or remove the unique ID in the map */
    public void setUnique(K id) {
        if (id == null) data.remove(uniqueField.getKey());
        else data.put(uniqueField.getKey(), id);
    }

    /** Retrieve the unique ID from the map */
    @SuppressWarnings("unchecked")
    public K getUnique() {
        return (K) data.get(uniqueField.getKey());
    }

    public void set(E field, Object value) {
        if (value == null) data.remove(field.getKey());
        else data.put(field.getKey(), value);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(E field) {
        return (V) data.get(field.getKey());
    }

    public Map<String, Object> toMap() {
        return new HashMap<>(data);
    }

    public void fromMap(Map<String, Object> map) {
        data.clear();
        if (map != null) data.putAll(map);
    }

    public interface SimpleIdentifiers {
        String getKey();
    }
}