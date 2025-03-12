package it.mikeslab.commons.api.database.util;

import it.mikeslab.commons.api.database.SerializableMapConvertible;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@ApiStatus.Experimental
public abstract class SimpleMapConvertible<T, U> implements SerializableMapConvertible<U> {

    private final T uniqueId;

    @Getter
    private String uniqueIdentifierName;

    @Getter(AccessLevel.PROTECTED)
    protected Map<String, Object> values;

    private Set<String> identifiers = new HashSet<>();

    public SimpleMapConvertible() {
        this.uniqueId = null;
        this.values = null;

        this.uniqueIdentifierName = null;
    }

    public SimpleMapConvertible(T uniqueId, String uniqueIdentifierName) {
        this.uniqueId = uniqueId;
        this.values = null;

        this.uniqueIdentifierName = uniqueIdentifierName;
    }

    public U fromMap(Map<String, Object> map) {

        if (map != null) {

            this.setValues(new HashMap<>(map));

            LogUtils.debug(
                    LogUtils.LogSource.DATABASE,
                    "Retrieved values from Database: " + this.getValues()
            );

            this.getValues().remove(this.getUniqueIdentifierName());

        }

        return (U) this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put(
                this.getUniqueIdentifierName(),
                this.getUniqueIdentifierValue()
        );

        if (values != null && !values.isEmpty()) {
            LogUtils.debug(
                    LogUtils.LogSource.DATABASE, // todo add a custom name
                    "Populating a map based on a BankAccount POJO instance which contains: " + values.toString()
            );
            map.putAll(values);
        }

        return map;
    }

    public String getUniqueIdentifierValue() {
        return this.uniqueId.toString();
    }

    protected  <V> V getValue(String key) {

        // if it has not been added, skip heavier retrieval
        if (!this.identifiers.contains(key)) {
            return null;
        }

        return (V) this.getValues().get(key);
    }

    protected void addValue(String key, Object value) {

        if (this.values == null) {
            this.values = new HashMap<>();
        }

        this.identifiers.add(key);
        this.values.put(key, value);
    }

    public Set<String> identifiers() {
        return this.identifiers;
    }

}
