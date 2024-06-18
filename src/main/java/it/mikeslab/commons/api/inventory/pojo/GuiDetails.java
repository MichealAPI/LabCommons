package it.mikeslab.commons.api.inventory.pojo;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import it.mikeslab.commons.api.inventory.GuiType;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.util.action.ActionHandler;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Data
public class GuiDetails {

    private final String[] inventoryLayout;
    private final GuiType guiType;
    private Multimap<Character, GuiElement> elements;
    private Map<Character, Map<Integer, GuiElement>> tempPageElements;

    // Optional
    private InventoryHolder holder;
    private Component inventoryName = Component.empty();
    private int inventorySize; // calculated or fixed, in case of a chest
    private Map<String, String> placeholders;

    private Map<String, Consumer<GuiInteractEvent>> clickActions;

    private Map<String, String> injectedConditionPlaceholders;

    // private boolean closeable;

    @ApiStatus.Experimental
    private String text; // valid only for anvil menus

    public GuiDetails(String[] inventoryLayout, GuiType guiType) {

        // default size
        inventorySize = inventoryLayout.length * guiType.getRowLength();

        this.guiType = guiType;
        this.inventoryLayout = inventoryLayout;

        this.elements = ArrayListMultimap.create();
        this.tempPageElements = new HashMap<>();

        this.placeholders = new HashMap<>();

        // this.closeable = true;

        this.clickActions = new HashMap<>();

        this.injectedConditionPlaceholders = new HashMap<>();

    }


    // Simple methods to add and remove elements
    // Avoiding exposing the map directly

    public void addElement(Character key, GuiElement element) {
        this.elements.put(key, element);
    }

    public void removeElement(Character key) {
        this.elements.removeAll(key);
    }

    public GuiDetails clone() {

        GuiDetails clone = new GuiDetails(inventoryLayout, guiType);

        clone.setHolder(holder);
        clone.setInventoryName(inventoryName);
        clone.setInventorySize(inventorySize);
        clone.setPlaceholders(new HashMap<>(placeholders));
        clone.setText(text);

        clone.setInjectedConditionPlaceholders(new HashMap<>(injectedConditionPlaceholders));

        // clone.setCloseable(closeable);

        for (Map.Entry<Character, GuiElement> entry : elements.entries()) {
            clone.addElement(entry.getKey(), entry.getValue().clone());
        }

        for (Map.Entry<Character, Map<Integer, GuiElement>> entry : tempPageElements.entrySet()) {
            Map<Integer, GuiElement> pageElements = new HashMap<>();
            for (Map.Entry<Integer, GuiElement> pageEntry : entry.getValue().entrySet()) {
                pageElements.put(pageEntry.getKey(), pageEntry.getValue().clone());
            }
            clone.tempPageElements.put(entry.getKey(), pageElements);
        }

        return clone;
    }
}
