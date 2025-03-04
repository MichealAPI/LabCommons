package it.mikeslab.commons.api.inventory.helper;

import it.mikeslab.commons.api.inventory.CustomInventory;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InventoryMap {
    private final ConcurrentMap<InventoryKey, CustomInventory> inventoryMap = new ConcurrentHashMap<>();

    public void setInventory(UUID playerUUID, String inventoryName, CustomInventory inventory) {
        inventoryMap.put(new InventoryKey(playerUUID, inventoryName), inventory);
    }

    public CustomInventory getInventory(UUID playerUUID, String inventoryName) {
        return inventoryMap.get(new InventoryKey(playerUUID, inventoryName));
    }

    public boolean containsKey(UUID playerUUID) {
        return inventoryMap.keySet().stream().anyMatch(key -> key.playerUUID.equals(playerUUID));
    }

    public Set<InventoryKey> getInventoryKeys() {
        return inventoryMap.keySet();
    }

    public void destroy(UUID playerUUID) {
        inventoryMap.keySet().removeIf(key -> key.playerUUID.equals(playerUUID));
    }

    public void clear() {
        inventoryMap.clear();
    }

    public void putAll(UUID target, Map<String, CustomInventory> inventoryMap) {
        inventoryMap.forEach((key, value) -> this.inventoryMap.put(new InventoryKey(target, key), value));
    }

    public ConcurrentMap<String, CustomInventory> getCachedInventories(UUID playerUUID) {
        ConcurrentMap<String, CustomInventory> cachedInventories = new ConcurrentHashMap<>();
        inventoryMap.forEach((key, value) -> {
            if (key.playerUUID.equals(playerUUID)) {
                cachedInventories.put(key.inventoryName, value);
            }
        });
        return cachedInventories;
    }

    @Getter
    public static class InventoryKey {
        private final UUID playerUUID;
        private final String inventoryName;

        public InventoryKey(UUID playerUUID, String inventoryName) {
            this.playerUUID = playerUUID;
            this.inventoryName = inventoryName;
        }

        // Override equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InventoryKey that = (InventoryKey) o;
            return playerUUID.equals(that.playerUUID) && inventoryName.equals(that.inventoryName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerUUID, inventoryName);
        }
    }
}