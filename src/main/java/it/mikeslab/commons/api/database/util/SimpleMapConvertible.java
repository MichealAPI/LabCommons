package it.mikeslab.commons.api.database.util;

import it.mikeslab.commons.api.database.SerializableMapConvertible;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
@ApiStatus.AvailableSince("2.4.1.5")
public abstract class SimpleMapConvertible<T, U> implements SerializableMapConvertible<U> {

    private static Set<String> KNOWN_IDENTIFIERS = null; // Instance field for identifiers
    private static SimpleIdentifiers UNIQUE_IDENTIFIER = null;

    @Setter
    private T uniqueId;



    private final Map<String, Object> data = new HashMap<>();

    protected SimpleMapConvertible(@NotNull Class<? extends SimpleIdentifiers> identifiersClass, @NotNull SimpleIdentifiers uniqueIdentifier) {
        // Initialize final fields
        UNIQUE_IDENTIFIER = uniqueIdentifier;
        KNOWN_IDENTIFIERS = Collections.unmodifiableSet(SimpleIdentifiers.identifiers(identifiersClass)); // Initialize instance identifiers safely

        LogUtils.debug(
                LogUtils.LogSource.DATABASE,
                String.format("SimpleMapConvertible initialized with uniqueIdentifier: %s and identifiers: %s",
                        UNIQUE_IDENTIFIER, KNOWN_IDENTIFIERS)
        );

        // uniqueId starts as null, must be set later
        this.uniqueId = null;
    }

    public SimpleMapConvertible(T uniqueId) {

        // uniqueId starts as null, must be set later
        this.uniqueId = uniqueId;

    }

    @Override
    public U fromMap(@Nullable Map<String, Object> map) {
        this.data.clear(); // Clear previous values if any

        if (map != null) {
            // Defensive copy
            Map<String, Object> tempMap = new HashMap<>(map);

            // Retrieve and set uniqueId from map if structure allows
            Object idFromMap = tempMap.get(this.getUniqueIdentifierName());
            if (idFromMap != null) {
                try {

                    this.uniqueId = (T) idFromMap; // Requires T's type matches map value type
                } catch (ClassCastException e) {
                    LogUtils.warn(LogUtils.LogSource.DATABASE, "Could not cast uniqueId from map: " + e);
                }
            }

            // Remove identifier before storing the rest
            tempMap.remove(this.getUniqueIdentifierName());
            this.data.putAll(tempMap);

            LogUtils.debug(
                    LogUtils.LogSource.DATABASE,
                    "Populated instance from Map. Values: " + this.data
            );
        }

        // Unchecked but standard pattern for builder/fluent style in inheritance
        @SuppressWarnings("unchecked")
        U self = (U) this;
        return self;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        // Handle potential null uniqueId
        String uniqueIdValue = this.getUniqueIdentifierValue(); // Use helper
        if (uniqueIdValue == null) {
            LogUtils.warn(
                    LogUtils.LogSource.DATABASE,
                    String.format("Unique identifier '%s' is null for object %s. Skipping.",
                            this.getUniqueIdentifierName(), this.getClass().getSimpleName())
            );
        } else {
            map.put(this.getUniqueIdentifierName(), uniqueIdValue); // Store String representation
        }


        if (!this.data.isEmpty()) {
            LogUtils.debug(
                    LogUtils.LogSource.DATABASE, // Use specific class name or context if possible
                    String.format("Populating map for %s instance. Values: %s",
                            this.getClass().getSimpleName(), this.data) // Consider structured logging
            );
            map.putAll(this.data);
        }

        return map;
    }

    /**
     * Gets the String representation of the unique ID, handling null.
     * @return String representation or null if uniqueId is null.
     */
    @Nullable
    @Override
    public String getUniqueIdentifierValue() {
        return this.uniqueId != null ? this.uniqueId.toString() : null;
    }

    /**
     * Gets the unique identifier name.
     * @return The name of the unique identifier.
     */
    @Nullable
    @Override
    public String getUniqueIdentifierName() {
        return UNIQUE_IDENTIFIER != null ? UNIQUE_IDENTIFIER.getKey() : null;
    }

    @Nullable
    public Set<String> getIdentifiers() {
        return KNOWN_IDENTIFIERS;
    }

    /**
     * Retrieves a value from the internal map.
     * Performs an unchecked cast.
     *
     * @param key The key corresponding to the value.
     * @param <V> The expected type of the value.
     * @return The value, or null if not found.
     * @throws ClassCastException if the stored value is not assignable to V.
     */
    @Nullable
    protected <V> V getValue(String key) {

        if (!KNOWN_IDENTIFIERS.contains(key)) {
            LogUtils.warn(LogUtils.LogSource.DATABASE, String.format("Key '%s' is not a known identifier for %s", key, this.getClass().getSimpleName()));
             return null;
        }

        Object rawValue = this.data.get(key);
        if (rawValue == null) {
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            V castValue = (V) rawValue;
            return castValue;
        } catch (ClassCastException e) {
            LogUtils.warn(
                    LogUtils.LogSource.DATABASE,
                    String.format("Type mismatch for key '%s' in %s. Expected %s but found %s.",
                            key, this.getClass().getSimpleName(), "V", rawValue.getClass().getName()) +
                    e
            );

            throw e;
        }
    }

    /**
     * Sets a value in the internal map.
     *
     * @param key The key.
     * @param value The value.
     */
    protected void setValue(String key, @Nullable Object value) {

        if (!KNOWN_IDENTIFIERS.contains(key)) {
             LogUtils.warn(LogUtils.LogSource.DATABASE, String.format("Setting value for non-declared identifier key '%s' in %s", key, this.getClass().getSimpleName()));
        }

        if (value == null) {
            this.data.remove(key);
        } else {
            this.data.put(key, value);
        }
    }

    /* Convenience methods using SimpleIdentifiers */

    public void setValue(@NotNull SimpleIdentifiers inst, @Nullable Object value) {
        this.setValue(inst.getKey(), value);
    }

    @Nullable
    public <V> V getValue(@NotNull SimpleIdentifiers inst) {
        return this.getValue(inst.getKey());
    }

    /**
     * Provides a read-only view of the internal values map.
     * @return An unmodifiable map of the current values.
     */
    public Map<String, Object> getValuesView() {
        return Collections.unmodifiableMap(this.data);
    }
}
