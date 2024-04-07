package it.mikeslab.widencommons.api.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryType;

@Getter
@RequiredArgsConstructor
public enum GuiType {

    CHEST(9),
    DISPENSER(3),
    DROPPER(3);

    private final int rowLength;

    public InventoryType toInventoryType() {

        switch (this) {
            case DISPENSER:
                return InventoryType.DISPENSER;
            case DROPPER:
                return InventoryType.DROPPER;
            default:
                return InventoryType.CHEST;
        }


    }

}
