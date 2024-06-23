package it.mikeslab.commons.api.inventory.util;

import it.mikeslab.commons.api.inventory.GuiFactory;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@Data
@NoArgsConstructor
public class CustomInventoryContext {

    private JavaPlugin instance;

    private InventoryContext inventoryContext;

    private InventorySettings settings;

    private GuiFactory guiFactory;

    private int id = -1; // Default is -1 to indicate that it is not set

    private boolean completed; // Ignored for the main menu

    public CustomInventoryContext(final JavaPlugin instance, InventorySettings settings) {
        this.instance = instance;
        this.settings = settings;
    }

}
