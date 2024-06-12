package it.mikeslab.commons.api.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryType;

@Getter
@RequiredArgsConstructor
public enum GuiType {

    CHEST(9),
    DISPENSER(3),
    DROPPER(3);
    //ANVIL(3);

    private final int rowLength;

    public InventoryType toInventoryType() {

        InventoryType result;

        switch (this) {
            case DISPENSER:
                result = InventoryType.DISPENSER;
                break;
            case DROPPER:
                result = InventoryType.DROPPER;
                break;
            //case ANVIL -> InventoryType.ANVIL;
            default:
                result = InventoryType.CHEST;
                break;
        };

        return result;

    }

}
