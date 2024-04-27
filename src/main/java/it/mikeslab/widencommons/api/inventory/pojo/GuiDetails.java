package it.mikeslab.widencommons.api.inventory.pojo;

import it.mikeslab.widencommons.api.inventory.GuiType;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@Data
public class GuiDetails {

    private final String[] inventoryLayout;
    private final GuiType guiType;
    private Map<Character, GuiElement> elements;

    // Optional
    private InventoryHolder holder;
    private Component inventoryName = Component.empty();
    private int inventorySize; // calculated or fixed, in case of a chest
    private Map<String, String> placeholders;

    @ApiStatus.Experimental
    private String text; // valid only for anvil menus

    public GuiDetails(String[] inventoryLayout, GuiType guiType) {

        // default size
        inventorySize = inventoryLayout.length * guiType.getRowLength();

        this.guiType = guiType;
        this.inventoryLayout = inventoryLayout;

        this.elements = new HashMap<>();
        this.placeholders = new HashMap<>();

    }


    // Simple methods to add and remove elements
    // Avoiding exposing the map directly

    public void addElement(Character key, GuiElement element) {
        this.elements.put(key, element);
    }

    public void removeElement(Character key) {
        this.elements.remove(key);
    }

    public GuiDetails clone() {

        GuiDetails clone = new GuiDetails(inventoryLayout, guiType);

        clone.setHolder(holder);
        clone.setInventoryName(inventoryName);
        clone.setInventorySize(inventorySize);
        clone.setPlaceholders(new HashMap<>(placeholders));
        clone.setText(text);

        for (Map.Entry<Character, GuiElement> entry : elements.entrySet()) {
            clone.addElement(entry.getKey(), entry.getValue().clone());
        }

        return clone;
    }
}
