package it.mikeslab.widencommons.api.inventory;

import it.mikeslab.widencommons.WidenCommons;
import it.mikeslab.widencommons.api.inventory.pojo.GuiDetails;
import it.mikeslab.widencommons.api.inventory.pojo.GuiElement;
import it.mikeslab.widencommons.api.inventory.pojo.RowPopulationContext;
import it.mikeslab.widencommons.api.inventory.util.GuiChecker;
import it.mikeslab.widencommons.api.logger.LoggerUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

@Data
@RequiredArgsConstructor
public class CustomGui implements InventoryHolder {

    private final GuiDetails guiDetails;

    // Note: the holder will also use this
    private Inventory inventory;

    // todo change integer to a set of slots, amount could be the size of the set
    private Map<String, Set<Integer>> internalValuesSlots; // Amount of items that should be built in a specific way by the plugin, internally
                                                       // This is useful for paged inventories since allow us to know how many items are there for each page

    public void generateInventory() {

        this.internalValuesSlots = new HashMap<>();

        if(!GuiChecker.isValid(guiDetails)) {
            LoggerUtil.log(
                    WidenCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.CONFIG,
                    "Invalid gui details"
            );
            return;
        }

        // Parsing details

        // Required details
        int size = guiDetails.getInventorySize();
        InventoryType type = guiDetails.getGuiType()
                                .toInventoryType();

        // Optional details
        Component title = guiDetails.getInventoryName();

        // Generating inventory

        Inventory inventory;

        if(type != InventoryType.CHEST) {
            inventory = Bukkit.createInventory(this, type, title);
        } else {

            // We cannot extract to specify
            // the size of the inventory

            inventory = Bukkit.createInventory(this, size, title);
        }

        // Populating inventory
        this.populateInventory(
                guiDetails,
                inventory
        );

        this.inventory = inventory;
    }



    public void populateInventory(GuiDetails guiDetails, Inventory inventory) {
        // Get the elements and layout from the GuiDetails
        Map<Character, GuiElement> elements = guiDetails.getElements();
        String[] layout = guiDetails.getInventoryLayout();

        // If the layout is not valid, return early
        if (GuiChecker.isLayoutValid(layout)) {
            return;
        }

        // Initialize a map to cache items and a counter for the row number
        Map<Character, ItemStack> cachedItems = new HashMap<>();
        int row = 0;

        // Get the type and row length from the GuiDetails
        GuiType type = guiDetails.getGuiType();
        int perRowLength = type.getRowLength();

        // Iterate over the layout
        for (String s : layout) {
            // Create a new context for each row
            RowPopulationContext context = new RowPopulationContext(
                    elements,
                    cachedItems,
                    s,
                    row,
                    perRowLength,
                    inventory
            );

            // Populate the row with items
            populateRow(context);
            row++;
        }
    }

    private void populateRow(RowPopulationContext context) {

        // Iterate over the elements in the context
        for (Map.Entry<Character, GuiElement> entry : context.getElements().entrySet()) {
            char key = entry.getKey();
            GuiElement element = entry.getValue();

            // Iterate over the layout of the row
            for (int column = 0; column < context.getRowLayout().length(); column++) {
                char c = context.getRowLayout().charAt(column);

                // If the character in the layout does not match the key, skip this iteration
                if (c != key) {
                    continue;
                }

                if(element.getInternalValue() != null) { // If that element should be built in a specific way by the plugin, internally
                    // Increment the amount of internal value items for this value

                    Set<Integer> internalValues = this.getInternalValuesSlots().getOrDefault(
                            element.getInternalValue(),
                            new HashSet<>()
                    );

                    int slot = context.getRow() * context.getPerRowLength() + column;
                    internalValues.add(slot);
                    System.out.println("Internal value: " + element.getInternalValue() + " - " + internalValues.size() + " - " + context.getRow() + " - " + column);

                    this.getInternalValuesSlots().put(
                            element.getInternalValue(),
                            internalValues
                    );

                    continue;
                }

                // Calculate the slot number
                int slot = context.getRow() * context.getPerRowLength() + column;

                // Get the item from the cache or create a new one if it does not exist
                ItemStack item = context.getCachedItems().getOrDefault(
                        key,
                        element.create(
                                guiDetails.getPlaceholders()
                        )
                );

                // Add the item to the cache and set it in the inventory
                context.getCachedItems().put(key, item);
                context.getInventory().setItem(slot, item);
            }
        }
    }


    /**
     * Populate the inventory with internal elements
     * @param internalValue The internal value to populate
     * @param internalElements The internal elements
     *
     */
    public void populateInternals(String internalValue, List<GuiElement> internalElements) {
        // Iterate over the internal elements

        Set<Integer> internalSlots = this.internalValuesSlots.getOrDefault(
                internalValue,
                new HashSet<>()
        );

        int j = 0;

        // Iterate over the internal slots
        for(int slot : internalSlots) {

            if(j >= internalElements.size()) {
                break;
            }

            // Get the element
            GuiElement element = internalElements.get(j);

            // Create the item and replace the placeholders in it
            ItemStack item = element.create(
                    guiDetails.getPlaceholders()
            );

            // Set the item in the inventory
            inventory.setItem(slot, item);

            // Go to the next element
            j++;
        }

    }




}
