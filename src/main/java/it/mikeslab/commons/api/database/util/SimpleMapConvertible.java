// src/main/java/it/mikeslab/commons/api/database/util/SimpleMapConvertible.java
package it.mikeslab.commons.api.database.util;

import it.mikeslab.commons.api.database.SerializableMapConvertible;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@ApiStatus.AvailableSince("2.4.1.5")
public abstract class SimpleMapConvertible<T, U> implements SerializableMapConvertible<U> {

    private final String uniqueIdentifierName;
    private final Set<String> knownIdentifiers;
    private T uniqueId;
    private final Map<String, Object> data = new HashMap<>();

    protected SimpleMapConvertible(
            @UnknownNullability T uniqueId,
            @NotNull Class<? extends SimpleIdentifiers> identifiersClass,
            @NotNull SimpleIdentifiers uniqueIdentifier
    ) {
        this.uniqueId = uniqueId;
        this.uniqueIdentifierName = uniqueIdentifier.getKey();
        this.knownIdentifiers = Collections.unmodifiableSet(
                Arrays.stream(identifiersClass.getEnumConstants())
                        .map(SimpleIdentifiers::getKey)
                        .collect(Collectors.toSet())
        );
        LogUtils.debug(
                LogUtils.LogSource.DATABASE,
                String.format(
                        "SimpleMapConvertible initialized with uniqueIdentifier: %s and identifiers: %s. Unique ID: %s",
                        uniqueIdentifierName,
                        knownIdentifiers,
                        uniqueId
                )
        );
    }

    @Override
    public U fromMap(@Nullable Map<String, Object> map) {
        data.clear();
        if (map != null) {
            Map<String, Object> temp = new HashMap<>(map);
            Object idVal = temp.remove(uniqueIdentifierName);
            if (idVal != null) {
                try {
                    // unchecked cast
                    //noinspection unchecked
                    this.uniqueId = (T) idVal;
                } catch (ClassCastException e) {
                    LogUtils.warn(
                            LogUtils.LogSource.DATABASE,
                            "Could not cast uniqueId from map: " + e
                    );
                }
            }
            data.putAll(temp);
            LogUtils.debug(
                    LogUtils.LogSource.DATABASE,
                    "Populated instance from Map. Values: " + data
            );
        }
        //noinspection unchecked
        return (U) this;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        String idValue = getUniqueIdentifierValue();
        if (idValue != null) {
            map.put(uniqueIdentifierName, idValue);
        } else {
            LogUtils.warn(
                    LogUtils.LogSource.DATABASE,
                    String.format(
                            "Unique identifier '%s' is null for %s. Skipping.",
                            uniqueIdentifierName,
                            getClass().getSimpleName()
                    )
            );
        }
        if (!data.isEmpty()) {
            LogUtils.debug(
                    LogUtils.LogSource.DATABASE,
                    String.format(
                            "Populating map for %s instance. Values: %s",
                            getClass().getSimpleName(),
                            data
                    )
            );
            map.putAll(data);
        }
        return map;
    }

    @Nullable
    @Override
    public String getUniqueIdentifierValue() {
        return uniqueId != null ? uniqueId.toString() : null;
    }

    @Nullable
    @Override
    public String getUniqueIdentifierName() {
        return uniqueIdentifierName;
    }

    @Nullable
    public Set<String> getIdentifiers() {
        return knownIdentifiers;
    }

    @Nullable
    protected <V> V getValue(String key) {
        if (!knownIdentifiers.contains(key)) {
            LogUtils.warn(
                    LogUtils.LogSource.DATABASE,
                    String.format(
                            "Key '%s' is not a known identifier for %s",
                            key, getClass().getSimpleName()
                    )
            );
            return null;
        }
        Object val = data.get(key);
        if (val == null) {
            return null;
        }
        try {
            //noinspection unchecked
            return (V) val;
        } catch (ClassCastException e) {
            LogUtils.warn(
                    LogUtils.LogSource.DATABASE,
                    String.format(
                            "Type mismatch for key '%s' in %s. Found %s.",
                            key, getClass().getSimpleName(), val.getClass().getName()
                    ) + e
            );
            throw e;
        }
    }

    protected void setValue(String key, @Nullable Object value) {
        if (!knownIdentifiers.contains(key)) {
            LogUtils.warn(
                    LogUtils.LogSource.DATABASE,
                    String.format(
                            "Setting value for non‑declared identifier '%s' in %s",
                            key, getClass().getSimpleName()
                    )
            );
        }
        if (value == null) {
            data.remove(key);
        } else {
            data.put(key, value);
        }
    }

    public void setValue(@NotNull SimpleIdentifiers inst, @Nullable Object value) {
        setValue(inst.getKey(), value);
    }

    @Nullable
    public <V> V getValue(@NotNull SimpleIdentifiers inst) {
        return getValue(inst.getKey());
    }

    public Map<String, Object> getValuesView() {
        return Collections.unmodifiableMap(data);
    }

    public interface SimpleIdentifiers {
        String getKey();
    }
}