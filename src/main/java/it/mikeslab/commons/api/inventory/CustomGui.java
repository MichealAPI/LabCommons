package it.mikeslab.commons.api.inventory;

import com.google.common.collect.Multimap;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Level;

@Data
@RequiredArgsConstructor
public class CustomGui implements InventoryHolder {

    private final GuiFactory guiFactory;
    private final JavaPlugin instance;
    private final int id;

    private UUID ownerUUID;

    private GuiDetails guiDetails;

    // Note: the holder will also use this
    private Inventory inventory;

    private final Map<Character, List<Integer>> characterListMap = new HashMap<>(); // A mapping of slots to characters

    private final Map<Character, PageSystem> pageSystemMap = new HashMap<>();

    private final List<BukkitRunnable> animationRunnable = new ArrayList<>();


    public void generateInventory() {

        if (!GuiChecker.isValid(guiDetails)) {
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

        // guiDetails.setTempPageElements(new HashMap<>()); // reset temp

        // Generating inventory

        if(inventory == null) {
            if (type != InventoryType.CHEST) {
                inventory = Bukkit.createInventory(this, type, ComponentsUtil.serialize(title));
            } else {

                // We cannot extract to specify
                // the size of the inventory

                inventory = Bukkit.createInventory(this, size, ComponentsUtil.serialize(title));
            }
        }


        // Populating inventory
        this.populateInventory(
                inventory
        );

    }

    /**
     * Populate the inventory with the elements
     * @param inventory The inventory to populate
     */
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

    /**
     * Populate the inventory
     * @param context The context of the population
     */
    private void populate(InventoryPopulationContext context) {
        // Iterate over each character and its corresponding slots
        for (Map.Entry<Character, List<Integer>> mappedSlots : characterListMap.entrySet()) {
            char targetChar = mappedSlots.getKey();
            List<Integer> slots = mappedSlots.getValue();

            this.handleGroupElement(context, targetChar, slots);
            this.handleSingleElement(context, targetChar, slots);
        }
    }

    /**
     * Handle groups of elements, if present
     * @param context The context of the population
     * @param targetChar The target character
     * @param slots The slots
     */
    private void handleGroupElement(InventoryPopulationContext context, char targetChar, List<Integer> slots) {
        // Check if the current character is a group element

        Iterator<GuiElement> elementIterator = context
                .getElements()
                .get(targetChar)
                .iterator();

        boolean isGroupElement = elementIterator.hasNext() && elementIterator.next().isGroupElement();

        // If it is a group element and there are more elements than slots, create a new PageSystem
        if (isGroupElement && context.getElements().get(targetChar).size() > slots.size()) {

            if(this.pageSystemMap.containsKey(targetChar)) {
                return;
            }

            this.pageSystemMap.put(
                    targetChar,
                    new PageSystem(
                            guiFactory,
                            instance,
                            id,
                            targetChar,
                            new ArrayList<>(context.getElements().get(targetChar))
                    )
            );
        }
    }

    /**
     * Handle a single/static element
     * @param context The context of the population
     * @param targetChar The target character
     * @param slots The slots
     */
    private void handleSingleElement(InventoryPopulationContext context, char targetChar, List<Integer> slots) {
        List<GuiElement> element = getGuiElement(context, targetChar);

        if (element == null) return;

        for(GuiElement guiElement : element) {


            if (guiFactory.getConditionParser() != null && guiElement.getCondition().isPresent()) {

                boolean isValid = guiFactory.getConditionParser()
                        .parse(
                                Bukkit.getPlayer(ownerUUID),
                                guiElement.getCondition().get(),
                                guiDetails.getInjectedConditionPlaceholders()
                        );

                if (!isValid) {
                    continue;
                }

            }

            if(guiElement.getFrames().isPresent()) {
                System.out.println("animation is present!!");
                this.runAnimation(guiElement, context, targetChar, slots);
                continue;
            }

            ItemStack item = getItem(context, targetChar, guiElement);
            populateSlots(context, targetChar, slots, item);
        }
    }

    /**
     * Get the GuiElement
     * @param context The context of the population
     * @param targetChar The target character to get the element for
     * @return The GuiElement
     */
    private List<GuiElement> getGuiElement(InventoryPopulationContext context, char targetChar) {

        List<GuiElement> elements = new ArrayList<>(context.getElements().get(targetChar));
        Iterator<GuiElement> iterator = elements.iterator();

        return iterator.hasNext() && iterator.next().isGroupElement() ? null : elements;
    }

    /**
     * Get the item
     * @param context The context of the population
     * @param targetChar The target character
     * @param element The element
     * @return The built item
     */
    private ItemStack getItem(InventoryPopulationContext context, char targetChar, GuiElement element) {
        ItemStack item = context.getCachedItems().get(targetChar);

        if(element.isGroupElement() && pageSystemMap.containsKey(targetChar)) {
            return new ItemStack(Material.AIR);
        }

        if (item == null) {
            item = element.create(guiDetails.getPlaceholders());

            // Getting the size of the individual element.
            // If there is more than one, and
            // it isn't a grouped element, then it shall not be cached
            Multimap<Character, GuiElement> elements = context.getElements();

            Collection<GuiElement> elementList = elements.get(targetChar);

            if(!containsCondition(elementList)) {
                context.getCachedItems().put(targetChar, item);
            }

        }

        return item;
    }

    /**
     * Populate the slots
     * @param context The context of the population
     * @param targetChar The target character
     * @param slots The slots
     * @param item The item to populate
     */
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

    /**
     * Populate a row
     * @param context The context of the population
     * @return The slot counter
     */
    private int populateRow(PopulateRowContext context) {

        for (int i = 0; i < context.getRow().length(); i++) {

            if (context.getRow().charAt(i) == context.getTargetChar()) {
                context
                        .getContext()
                        .getInventory()
                        .setItem(
                                context.getSlots().get(
                                        context.getSlotCounter()
                                ),
                                context.getItem()
                        );


                context.setSlotCounter(context.getSlotCounter() + 1); // todo there may be a problem here

            } // todo isnt cached anymore
        }
        return context.getSlotCounter();
    }

    /**
     * Map the characters to slots
     * @param layout The layout
     */
    private void mapCharToSlot(String[] layout) {

        // If the map is not empty, return early
        if(!characterListMap.isEmpty()) {
            return;
        }

        for (int i = 0; i < layout.length; i++) {
            mapRowToSlots(layout[i], i);
        }
    }

    /**
     * Map a row of chars to slots
     * @param row The row
     * @param rowIndex The row index
     */
    private void mapRowToSlots(String row, int rowIndex) {
        for (int j = 0; j < row.length(); j++) {
            char c = row.charAt(j);
            if (c != ' ') {
                addCharToMap(c, rowIndex * row.length() + j);
            }
        }
    }

    /**
     * Add a character to the map
     * @param c The character
     * @param slot The slot
     */
    private void addCharToMap(char c, int slot) {
        if (!characterListMap.containsKey(c)) {
            characterListMap.put(c, new ArrayList<>());
        }
        characterListMap.get(c).add(slot);
    }

    /**
     * Populate the page
     * @param context The context of the population
     */
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

            if(guiFactory.getConditionParser() != null && element.getCondition().isPresent()) {

                boolean isValid = guiFactory.getConditionParser()
                        .parse(
                                Bukkit.getPlayer(ownerUUID),
                                element.getCondition().get(),
                                guiDetails.getInjectedConditionPlaceholders()
                        );

                if(!isValid) {
                    continue;
                }
            }

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


    private boolean containsCondition(Collection<GuiElement> elements) {

        for(GuiElement element : elements) {
            if(element.getCondition().isPresent()) {
                return true;
            }
        }

        return false;
    }


    private void runAnimation(GuiElement guiElement, InventoryPopulationContext context, char targetChar, List<Integer> slots) {
        // item is animated!

        System.out.println("animation has been requested!!");

        BukkitRunnable runnable = new BukkitRunnable() {
            int frame = 0;

            @Override
            public void run() {

                System.out.println("Running animation");
                System.out.println("frame: " + frame);

                ItemStack item = guiElement.getFrames().get()[frame]; // runs only if present

                populateSlots(context, targetChar, slots, item);
                frame++;

                if(frame >= guiElement.getFrames().get().length) {
                    frame = 0;
                }
            }
        };

        runnable.runTaskTimer(instance, 0, 5); // todo configurable

        animationRunnable.add(runnable);
    }

}

