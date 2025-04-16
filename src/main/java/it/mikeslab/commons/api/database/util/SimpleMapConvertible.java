package it.mikeslab.commons.api.database.util;

import it.mikeslab.commons.api.database.SerializableMapConvertible;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Data
@ApiStatus.AvailableSince("2.4.1.5")
public abstract class SimpleMapConvertible<T, U> implements SerializableMapConvertible<U> {

    private static Set<String> IDENTIFIERS = new HashSet<>();

    @Setter
    private T uniqueId;

    @Getter
    private String uniqueIdentifierName;

    @Getter(AccessLevel.PUBLIC)
    private Map<String, Object> values;

    public SimpleMapConvertible() {
        this.uniqueId = null;
        this.values = null;

        this.uniqueIdentifierName = null;
    }

    public SimpleMapConvertible(T uniqueId, String uniqueIdentifierName) {
        this.uniqueId = uniqueId;
        this.values = new HashMap<>();

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
                    String.format("Populating a map based on a %s POJO instance which contains: " + values,
                            this.getClass().getSimpleName()
                    )
            );
            map.putAll(values);
        }

        return map;
    }

    public String getUniqueIdentifierValue() {
        return this.uniqueId.toString();
    }

    @Nullable
    public Set<String> getIdentifiers() {
        return IDENTIFIERS;
    }

    /**
     * Set the identifiers for this class
     * @param identifiers the identifiers to set
     */
    public void setIdentifiers(Set<String> identifiers) {
        IDENTIFIERS = identifiers;
    }

    /**
     * Set the identifiers for this class
     * @param clazz the class which contains the identifiers in a simple way
     */
    public void setIdentifiers(Class<? extends SimpleIdentifiers> clazz) {
        IDENTIFIERS = SimpleIdentifiers.identifiers(clazz);
    }

    protected <V> V getValue(String key) {

        // if it has not been added, skip heavier retrieval
        if (!IDENTIFIERS.contains(key)) {
            return null;
        }

        return (V) this.getValues().get(key);
    }

    protected void setValue(String key, Object value) {

        if (this.values == null) {
            this.values = new HashMap<>();
        }

        IDENTIFIERS.add(key);
        this.values.put(key, value);
    }

    /*
     * Set the value of a specific identifier
     * @param inst the instance of the identifier
     * @param value the value to set
     *
     * Usage of enum to avoid the need of a string and force
     * an overall better design in all usages
     */
    public void setValue(SimpleIdentifiers inst, Object value) {
        this.setValue(inst.getKey(), value);
    }

    /*
     * Get the value of a specific identifier
     * @param inst the instance of the identifier
     *
     * Usage of enum to avoid the need of a string and force
     * an overall better design in all usages
     */
    public <V> V getValue(SimpleIdentifiers inst) {
        return this.getValue(inst.getKey());
    }

}
