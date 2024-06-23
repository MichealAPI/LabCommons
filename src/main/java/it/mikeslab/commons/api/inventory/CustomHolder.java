package it.mikeslab.commons.api.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
@RequiredArgsConstructor
public class CustomHolder implements InventoryHolder {

    private final Inventory inventory;

}
