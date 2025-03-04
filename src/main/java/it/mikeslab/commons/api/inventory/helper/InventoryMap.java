package it.mikeslab.commons.api.inventory.helper;

import it.mikeslab.commons.api.inventory.CustomInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A map that stores the setup inventories for each player
 * Necessary to keep track of the inventories that are open
 */
public abstract class InventoryMap implements ConcurrentMap<UUID, Map<String, CustomInventory>> {

    private final ConcurrentMap<UUID, Map<String, CustomInventory>> map = new ConcurrentHashMap<>();

    public InventoryMap() {
        super();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Map<String, CustomInventory> get(Object key) {
        return map.get(key);
    }

    @Nullable
    @Override
    public Map<String, CustomInventory> put(UUID key, Map<String, CustomInventory> value) {
        return map.put(key, value);
    }

    @Override
    public Map<String, CustomInventory> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends UUID, ? extends Map<String, CustomInventory>> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<UUID> keySet() {
        return map.keySet();
    }

    @NotNull
    @Override
    public Collection<Map<String, CustomInventory>> values() {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<UUID, Map<String, CustomInventory>>> entrySet() {
        return map.entrySet();
    }


}
