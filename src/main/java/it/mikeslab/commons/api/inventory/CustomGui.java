package it.mikeslab.commons.api.inventory;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.pojo.InventoryPopulationContext;
import it.mikeslab.commons.api.inventory.util.GuiChecker;
import it.mikeslab.commons.api.inventory.util.PageSystem;
import it.mikeslab.commons.api.logger.LoggerUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

@Data
@RequiredArgsConstructor
public class CustomGui implements InventoryHolder {

    private final GuiFactory guiFactory;
    private final JavaPlugin instance;
    private final int id;

    private GuiDetails guiDetails;

    // Note: the holder will also use this
    private Inventory inventory;

    private Map<Character, List<Integer>> characterListMap = new HashMap<>(); // A mapping of slots to characters

    private Map<Character, PageSystem> pageSystemMap = new HashMap<>();

    public void generateInventory() {

        if (!GuiChecker.isValid(guiDetails)) {
            LoggerUtil.log(
                    LabCommons.PLUGIN_NAME,
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

        if (type != InventoryType.CHEST) {
            inventory = Bukkit.createInventory(this, type, ComponentsUtil.serialize(title));
        } else {

            // We cannot extract to specify
            // the size of the inventory

            inventory = Bukkit.createInventory(this, size, ComponentsUtil.serialize(title));
        }


        // Populating inventory
        this.populateInventory(
                inventory
        );

        this.inventory = inventory;
    }


    public void populateInventory(Inventory inventory) {
        // Get the elements and layout from the GuiDetails
        Multimap<Character, GuiElement> elements = guiDetails.getElements();
        String[] layout = guiDetails.getInventoryLayout();

        // If the layout is not valid, return early
        if (GuiChecker.isLayoutValid(layout)) {
            return;
        }

        // Initialize a map to cache items
        Map<Character, ItemStack> cachedItems = new HashMap<>();

        // Map characters to slots
        this.mapCharToSlot(layout);

        // Populate the inventory
        this.populate(
                new InventoryPopulationContext(
                        elements,
                        cachedItems,
                        inventory
                )
        );

    }

    private void populate(InventoryPopulationContext context) {
        // Iterate over each character and its corresponding slots
        for (Map.Entry<Character, List<Integer>> mappedSlots : characterListMap.entrySet()) {
            char targetChar = mappedSlots.getKey();
            List<Integer> slots = mappedSlots.getValue();

            this.handleGroupElement(context, targetChar, slots);
            this.handleSingleElement(context, targetChar, slots);
        }
    }

    private void handleGroupElement(InventoryPopulationContext context, char targetChar, List<Integer> slots) {
        // Check if the current character is a group element
        boolean isGroupElement = context.getElements().get(targetChar).iterator().next().isGroupElement();
        // If it is a group element and there are more elements than slots, create a new PageSystem
        if (isGroupElement && context.getElements().get(targetChar).size() > slots.size()) {
            this.pageSystemMap.put(
                    targetChar,
                    new PageSystem(
                            guiFactory,
                            instance,
                            id,
                            targetChar,
                            new ArrayList<>(context.getElements().get(targetChar))
                    ));
        }
    }

    private void handleSingleElement(InventoryPopulationContext context, char targetChar, List<Integer> slots) {
        // Get the GuiElement for the current character
        GuiElement element = context.getElements().get(targetChar).iterator().next();
        if (element == null) return;

        // Get the ItemStack for the current character, or create a new one if it doesn't exist
        ItemStack item = context.getCachedItems().get(targetChar);
        if (item == null) {
            item = element.create(guiDetails.getPlaceholders());
            context.getCachedItems().put(targetChar, item);
        }

        // Counter for the number of slots filled
        int slotCounter = 0;
        // Iterate over each row in the inventory layout
        for (String row : guiDetails.getInventoryLayout()) {
            // Iterate over each character in the row
            for (int i = 0; i < row.length(); i++) {
                char c = row.charAt(i);
                // If the character matches the target character, set the item in the corresponding slot
                if (c == targetChar) {
                    // If all slots for this character have been filled, break the loop
                    if (slotCounter >= slots.size()) {
                        break;
                    }

                    // Get the slot for the current item and set the item in the inventory
                    int slot = slots.get(slotCounter);
                    context.getInventory().setItem(slot, item);
                    slotCounter++;
                }
            }
        }
    }


    private void mapCharToSlot(String[] layout) {

        // Map characters to slots
        for (int i = 0; i < layout.length; i++) {
            String row = layout[i];
            for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                if (c != ' ') {
                    if (!characterListMap.containsKey(c)) {
                        characterListMap.put(c, new ArrayList<>());
                    }
                    characterListMap.get(c).add(i * 9 + j);
                }
            }
        }
    }


    public void populatePage(char targetChar, List<GuiElement> subList) {

        boolean isTargetValid = this.getCharacterListMap().containsKey(targetChar);
        if(!isTargetValid) return;

        List<Integer> slots = this.getCharacterListMap().get(targetChar);
        Map<Integer, GuiElement> tempSlots = new HashMap<>();

        for(int i = 0; i < slots.size(); i++) {

            int slot = slots.get(i);
            if(subList.size() <= i) {
                this.getInventory().setItem(slot, null);
                continue;
            }

            GuiElement element = subList.get(i);

            ItemStack item = element.create(
                    guiDetails.getPlaceholders()
            );

            this.getInventory().setItem(slot, item);
            tempSlots.put(slot, element);

        }

        // Register by this way to enable click events for each page element
        guiDetails
                .getTempPageElements()
                .put(targetChar, tempSlots);

    }


}

