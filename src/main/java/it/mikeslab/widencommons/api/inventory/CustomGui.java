package it.mikeslab.widencommons.api.inventory;

import it.mikeslab.widencommons.api.inventory.pojo.GuiDetails;
import it.mikeslab.widencommons.api.inventory.pojo.GuiElement;
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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Data
@RequiredArgsConstructor
public class CustomGui implements InventoryHolder {

    private final GuiDetails guiDetails;

    // Note: the holder will also use this
    private Inventory inventory;



    public void generateInventory() {

        if(!GuiChecker.isValid(guiDetails)) {
            LoggerUtil.log(
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

        Map<Character, GuiElement> elements = guiDetails.getElements();
        String[] layout = guiDetails.getInventoryLayout();

        if (GuiChecker.isLayoutValid(layout)) {
            return;
        }

        // Populating inventory

        Map<Character, ItemStack> cashedItems = new HashMap<>();

        // Iterating over the layout, for each row
        int row = 0;

        GuiType type = guiDetails.getGuiType();
        int perRowLength = type.getRowLength();


        for (String s : layout) {

            // Iterating over the elements, for each key (character)
            for (Map.Entry<Character, GuiElement> entry : elements.entrySet()) {

                char key = entry.getKey();
                GuiElement element = entry.getValue();

                for (int column = 0; column < s.length(); column++) { // Change this line
                    char c = s.charAt(column); // And this line

                    if (c == key) {

                        // Item position in the row
                        int slot = row * perRowLength + column;

                        ItemStack item;

                        // If the item is already in the cache, we can just add it
                        if(cashedItems.containsKey(key)) {
                            item = cashedItems.get(key);
                        } else {

                            // If not cached
                            item = element.create();
                            cashedItems.put(key, item);

                        }

                        inventory.setItem(slot, item);

                    }
                }

                row++;
            }
        }

    }




}
