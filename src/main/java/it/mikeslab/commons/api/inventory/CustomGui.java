package it.mikeslab.commons.api.inventory;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.pojo.*;
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
        GuiElement element = getGuiElement(context, targetChar);
        if (element == null) return;

        ItemStack item = getItem(context, targetChar, element);
        populateSlots(context, targetChar, slots, item);
    }

    private GuiElement getGuiElement(InventoryPopulationContext context, char targetChar) {
        Iterator<GuiElement> iterator = context.getElements().get(targetChar).iterator();
        return iterator.hasNext() ? iterator.next() : null;
    }

    private ItemStack getItem(InventoryPopulationContext context, char targetChar, GuiElement element) {
        ItemStack item = context.getCachedItems().get(targetChar);
        if (item == null) {
            item = element.create(guiDetails.getPlaceholders());
            context.getCachedItems().put(targetChar, item);
        }
        return item;
    }

    private void populateSlots(InventoryPopulationContext context, char targetChar, List<Integer> slots, ItemStack item) {
        int slotCounter = 0;
        for (String row : guiDetails.getInventoryLayout()) {
            slotCounter = populateRow(
                    new PopulateRowContext(
                            context,
                            targetChar,
                            slots,
                            item,
                            slotCounter,
                            row
                    )
            );

            if (slotCounter >= slots.size()) {
                break;
            }
        }
    }

    private int populateRow(PopulateRowContext context) {
        for (int i = 0; i < context.getRow().length(); i++) {
            if (context.getRow().charAt(i) == context.getTargetChar()) {
                context.getContext().getInventory().setItem(context.getSlots().get(context.getSlotCounter()), context.getItem());
                context.setSlotCounter(context.getSlotCounter() + 1);
            }
        }
        return context.getSlotCounter();
    }

    private void mapCharToSlot(String[] layout) {
        for (int i = 0; i < layout.length; i++) {
            mapRowToSlots(layout[i], i);
        }
    }

    private void mapRowToSlots(String row, int rowIndex) {
        for (int j = 0; j < row.length(); j++) {
            char c = row.charAt(j);
            if (c != ' ') {
                addCharToMap(c, rowIndex * 9 + j);
            }
        }
    }

    private void addCharToMap(char c, int slot) {
        if (!characterListMap.containsKey(c)) {
            characterListMap.put(c, new ArrayList<>());
        }
        characterListMap.get(c).add(slot);
    }


    public void populatePage(PopulatePageContext context) {

        boolean isTargetValid = this.getCharacterListMap().containsKey(context.getTargetChar());
        if(!isTargetValid) return;

        List<Integer> slots = this.getCharacterListMap().get(context.getTargetChar());
        Map<Integer, GuiElement> tempSlots = new HashMap<>();

        for(int i = 0; i < slots.size(); i++) {

            int slot = slots.get(i);
            if(context.getSubList().size() <= i) {
                this.getInventory().setItem(slot, null);
                continue;
            }

            GuiElement element = context.getSubList().get(i);

            ItemStack item = element.create(
                    guiDetails.getPlaceholders()
            );

            this.getInventory().setItem(slot, item);
            tempSlots.put(slot, element);

        }

        // Register by this way to enable click events for each page element
        guiDetails
                .getTempPageElements()
                .put(context.getTargetChar(), tempSlots);

    }


}

