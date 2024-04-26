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
    //ANVIL(3);

    private final int rowLength;

    public InventoryType toInventoryType() {

        return switch (this) {
            case DISPENSER -> InventoryType.DISPENSER;
            case DROPPER -> InventoryType.DROPPER;
            //case ANVIL -> InventoryType.ANVIL;
            default -> InventoryType.CHEST;
        };


    }

}
