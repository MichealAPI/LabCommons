package it.mikeslab.commons.api.inventory;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.pojo.*;
import it.mikeslab.commons.api.inventory.util.GuiChecker;
import it.mikeslab.commons.api.inventory.util.PageSystem;
import it.mikeslab.commons.api.inventory.util.frame.FrameColorUtil;
import it.mikeslab.commons.api.logger.LoggerUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

    private final Map<Character, Animation> animatedElements = new HashMap<>();

    private boolean animated;

    private int animationTaskId = -1;

    private InventoryPopulationContext populationContext;


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
        this.populationContext = new InventoryPopulationContext(
                elements,
                cachedItems,
                inventory
        );

        this.populate();

    }

    /**
     * Populate the inventory
     */
    private void populate() {
        // Iterate over each character and its corresponding slots
        for (Map.Entry<Character, List<Integer>> mappedSlots : characterListMap.entrySet()) {
            char targetChar = mappedSlots.getKey();
            List<Integer> slots = mappedSlots.getValue();

            this.handleGroupElement(targetChar, slots);
            this.handleSingleElement(targetChar, slots);
        }
    }

    /**
     * Handle groups of elements, if present
     * @param targetChar The target character
     * @param slots The slots
     */
    private void handleGroupElement(char targetChar, List<Integer> slots) {
        // Check if the current character is a group element

        Iterator<GuiElement> elementIterator = populationContext
                .getElements()
                .get(targetChar)
                .iterator();

        boolean isGroupElement = elementIterator.hasNext() && elementIterator.next().isGroupElement();

        // If it is a group element and there are more elements than slots, create a new PageSystem
        if (isGroupElement && populationContext.getElements().get(targetChar).size() > slots.size()) {

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
                            new ArrayList<>(populationContext.getElements().get(targetChar))
                    )
            );
        }
    }

    /**
     * Handle a single/static element
     * @param targetChar The target character
     * @param slots The slots
     */
    private void handleSingleElement(char targetChar, List<Integer> slots) {
        List<GuiElement> element = getGuiElement(targetChar);

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
                this.animatedElements.put(targetChar, new Animation(guiElement, slots));
                this.animated = true;
                continue;
            }

            ItemStack item = getItem(targetChar, guiElement);
            populateSlots(targetChar, slots, item, false);
        }
    }

    /**
     * Get the GuiElement
     * @param targetChar The target character to get the element for
     * @return The GuiElement
     */
    private List<GuiElement> getGuiElement(char targetChar) {

        List<GuiElement> elements = new ArrayList<>(populationContext.getElements().get(targetChar));
        Iterator<GuiElement> iterator = elements.iterator();

        return iterator.hasNext() && iterator.next().isGroupElement() ? null : elements;
    }

    /**
     * Get the item
     * @param targetChar The target character
     * @param element The element
     * @return The built item
     */
    private ItemStack getItem(char targetChar, GuiElement element) {
        ItemStack item = populationContext.getCachedItems().get(targetChar);

        if(element.isGroupElement() && pageSystemMap.containsKey(targetChar)) {
            return new ItemStack(Material.AIR);
        }

        if (item == null) {
            item = element.create(guiDetails.getPlaceholders());

            // Getting the size of the individual element.
            // If there is more than one, and
            // it isn't a grouped element, then it shall not be cached
            Multimap<Character, GuiElement> elements = populationContext.getElements();

            Collection<GuiElement> elementList = elements.get(targetChar);

            if(!containsCondition(elementList)) {
                populationContext.getCachedItems().put(targetChar, item);
            }

        }

        return item;
    }

    /**
     * Populate the slots
     * @param targetChar The target character
     * @param slots The slots
     * @param item The item to populate
     */
    private void populateSlots(char targetChar, List<Integer> slots, ItemStack item, boolean metaReplaceOnly) {
        int slotCounter = 0;
        for (String row : guiDetails.getInventoryLayout()) {
            slotCounter = populateRow(
                    new PopulateRowContext(
                            targetChar,
                            slots,
                            item,
                            slotCounter,
                            row,
                            metaReplaceOnly
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

                ItemStack itemAt = populationContext
                        .getInventory()
                        .getItem(context.getSlots().get(context.getSlotCounter()));

                if(!context.isMetaReplaceOnly() || itemAt == null) {
                    populationContext
                            .getInventory()
                            .setItem(
                                    context.getSlots().get(
                                            context.getSlotCounter()
                                    ),
                                    context.getItem()
                            );
                } else {

                    ItemMeta referenceMeta = context.getItem().getItemMeta();

                    itemAt.setItemMeta(referenceMeta);

                }


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
                context.getTargetInventory().setItem(slot, null);
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

            context.getTargetInventory().setItem(slot, item);
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


    public BukkitRunnable getAnimationRunnable() {

        if(!animated) {
            throw new IllegalStateException("The gui is not animated");
        }

        return new BukkitRunnable() {
            int frame = 0;
            final Player player = Bukkit.getPlayer(ownerUUID);

            @Override
            public void run() {

                for(Map.Entry<Character, Animation> animatedElement : animatedElements.entrySet()) {

                    ItemStack item = animatedElement
                            .getValue()
                            .getGuiElement()
                            .getFrames()
                            .get()[frame];

                    populateSlots(
                            animatedElement.getKey(),
                            animatedElement.getValue().getSlots(),
                            item,
                            true
                    );

                    player.updateInventory();
                }

                frame++;

                if(frame >= FrameColorUtil.MAX_FRAMES) {
                    frame = 0;
                }
            }
        };
    }

}

