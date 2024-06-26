package it.mikeslab.commons.api.inventory;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.pojo.Animation;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.pojo.population.InventoryPopulationContext;
import it.mikeslab.commons.api.inventory.pojo.population.PopulatePageContext;
import it.mikeslab.commons.api.inventory.pojo.population.PopulateRowContext;
import it.mikeslab.commons.api.inventory.util.MappingUtil;
import it.mikeslab.commons.api.inventory.util.PageSystem;
import it.mikeslab.commons.api.inventory.util.animation.AnimationUtil;
import it.mikeslab.commons.api.inventory.util.config.GuiChecker;
import it.mikeslab.commons.api.inventory.util.animation.FrameColorUtil;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Supplier;

@Getter
@Setter
public class CustomGui {

    private final GuiFactory guiFactory;
    private final int id;

    private JavaPlugin instance;

    private UUID ownerUUID;

    private GuiDetails guiDetails,
                       tempGuiDetails; // for placeholders / internal injections

    private Inventory inventory;

    private final Map<Character, List<Integer>> characterListMap = new HashMap<>(); // A mapping of slots to characters

    private final Map<Character, PageSystem> pageSystemMap = new HashMap<>();

    private final Map<Character, Animation> animatedElements = new HashMap<>();

    private final Map<Character, ItemStack> cachedItems = new HashMap<>();

    private boolean animated; // True if the AnimationRunnable should be requested

    private int animationTaskId = -1; // Refers to the BukkitTask id

    private InventoryPopulationContext populationContext;

    private Player tempPlayer; // This is not going to remain in memory


    public CustomGui(GuiFactory guiFactory, JavaPlugin instance, int id) {
        this.guiFactory = guiFactory;
        this.id = id;
        this.instance = instance;
    }


    // it is used just to initialize the inventory

    public void generateInventory() {

        if (!GuiChecker.isValid(guiDetails)) {
            LogUtils.warn(
                    LogUtils.LogSource.CONFIG,
                    "Invalid gui details"
            );
            return;
        }

        // Parsing details

        // Required details
        int size = this.getGuiDetails().getInventorySize();
        InventoryType type = this.getGuiDetails().getGuiType()
                .toInventoryType();

        // Optional details
        String title = this.getGuiDetails().getInventoryName();


        // Generating inventory
        if (inventory == null) {
            if (type != InventoryType.CHEST) {
                inventory = Bukkit.createInventory(null, type, ComponentsUtil.getSerializedComponent(title));
            } else {

                // We cannot extract to specify
                // the size of the inventory

                inventory = Bukkit.createInventory(null, size, ComponentsUtil.getSerializedComponent(title));
            }
        }


        // Populating inventory
        this.populateInventory();

    }


    /**
     * Populate the inventory with the elements
     */
    public void populateInventory() {

        // A player instance is needed in many contexts,
        // although having it stored could cause memory leaks on the long run
        // a temporary player instance based on the player's UUID is considered
        // to be the best approach
        this.tempPlayer = Bukkit.getPlayer(ownerUUID);

        // Get the elements and layout from the GuiDetails
        Multimap<Character, GuiElement> elements = this.getGuiDetails().getElements();
        String[] layout = this.getGuiDetails().getInventoryLayout();

        // If the layout is not valid, return early
        if (GuiChecker.isLayoutValid(layout)) {
            return;
        }

        // Map characters from the layout to slots
        MappingUtil.mapCharToSlot(
                layout,
                characterListMap
        );

        // Populate the inventory
        this.populationContext = new InventoryPopulationContext(
                elements,
                inventory
        );

        // Post-process guiElement for animations
        for (GuiElement guiElement : elements.values()) {

            // if an element should be animated, it will generate
            // the missing animation frames
            AnimationUtil.postProcessElement(
                    getGuiDetails(),
                    guiElement,
                    tempPlayer
            );

        }

        // Populate the page
        this.populate();

        // Reset the temporary player instance to prevent
        // memory leaks
        this.tempPlayer = null;

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
     *
     * @param targetChar The target character
     * @param slots      The slots
     */
    private void handleGroupElement(char targetChar, List<Integer> slots) {
        // Check if the current character is a group element

        Iterator<GuiElement> elementIterator = populationContext
                .getElements()
                .get(targetChar)
                .iterator();

        boolean isGroupElement = elementIterator.hasNext() && elementIterator.next().isGroupElement();

        // If the number of elements is greater than the number of slots, then it's pageable
        boolean canBePaginated = populationContext.getElements().get(targetChar).size() > slots.size();

        // If it is a group element and there are more elements than slots, create a new PageSystem
        if (isGroupElement && canBePaginated) {

            if (this.pageSystemMap.containsKey(targetChar)) {
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
     *
     * @param targetChar The target character
     * @param slots      The slots
     */
    private void handleSingleElement(char targetChar, List<Integer> slots) {
        List<GuiElement> element = getGuiElement(targetChar);

        if (element == null) return;

        for (GuiElement guiElement : element) {

            if(!this.hasValidCondition(guiElement)) continue;

            if (guiElement.isAnimated()) {

                this.animatedElements.put(targetChar, new Animation(guiElement, slots));
                this.animated = true;
                continue;
            }

            ItemStack item = this.getItem(targetChar, guiElement);
            populateSlots(targetChar, slots, item, false);
        }


    }

    /**
     * Get the GuiElement
     *
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
     *
     * @param targetChar The target character
     * @param element    The element
     * @return The built item
     */
    private ItemStack getItem(char targetChar, GuiElement element) {

        // Directly return from cache if conditions are met
        boolean conditionAbsent = !element.getCondition().isPresent();

        if (cachedItems.containsKey(targetChar) && isCacheable(element)) {
            return cachedItems.get(targetChar);
        }

        // Handle group elements with a direct return of a new ItemStack
        if (element.isGroupElement() && pageSystemMap.containsKey(targetChar)) {
            return null; // air
        }

        Map<String, String> internalPlaceholders = this.getGuiDetails().getPlaceholders();

        // Adds the condition placeholders to the internal ones
        internalPlaceholders.putAll(
                this.getUpdatedConditionPlaceholders()
        );

        // Create a new ItemStack based on the current context
        ItemStack stack = element.create(
                internalPlaceholders,
                tempPlayer
        );

        // Cache the ItemStack if no conditions are present
        if (conditionAbsent) {
            cachedItems.put(targetChar, stack);
        }

        return stack;
    }

    /**
     * Populate the slots
     *
     * @param targetChar The target character
     * @param slots      The slots
     * @param item       The item to populate
     */
    public void populateSlots(char targetChar, List<Integer> slots, ItemStack item, boolean metaReplaceOnly) {

        int slotCounter = 0;

        for (String row : this.getGuiDetails().getInventoryLayout()) {
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
     *
     * @param context The context of the population
     * @return The slot counter
     */
    private int populateRow(PopulateRowContext context) {

        for (int i = 0; i < context.getRow().length(); i++) {

            if (context.getRow().charAt(i) == context.getTargetChar()) {

                ItemStack itemAt = populationContext
                        .getInventory()
                        .getItem(context.getSlots().get(context.getSlotCounter()));

                if (!context.isMetaReplaceOnly() || itemAt == null) {
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


                context.setSlotCounter(context.getSlotCounter() + 1);

            } // todo isnt cached anymore
        }
        return context.getSlotCounter();
    }

    /**
     * Populate the page
     *
     * @param context The context of the population
     */
    public void populatePage(PopulatePageContext context) {

        boolean isTargetValid = this.getCharacterListMap().containsKey(context.getTargetChar());
        if (!isTargetValid) return;

        List<Integer> slots = this.getCharacterListMap().get(context.getTargetChar());
        Map<Integer, GuiElement> tempSlots = new HashMap<>();

        for (int i = 0; i < slots.size(); i++) {

            int slot = slots.get(i);

            if (context.getSubList().size() <= i) {
                context.getTargetInventory().setItem(slot, null);
                continue;
            }

            GuiElement element = context.getSubList().get(i);

            if(!this.hasValidCondition(element)) continue;

            GuiElement cloneElement = element.clone();

            ItemStack item = cloneElement.create(
                    this.getGuiDetails().getPlaceholders(),
                    context.getPlayer()
            );

            context.getTargetInventory().setItem(slot, item);
            tempSlots.put(slot, cloneElement);

        }

        // Register by this way to enable click events for each page element
        guiDetails
                .getTempPageElements()
                .put(context.getTargetChar(), tempSlots);

    }




    private boolean hasValidCondition(GuiElement element) {
        if (guiFactory.getConditionParser() != null && element.getCondition().isPresent()) {

            return guiFactory.getConditionParser()
                    .parse(
                            tempPlayer,
                            element.getCondition().get(),
                            this.getGuiDetails().getInjectedConditionPlaceholders()
                    );
        }

        return true;
    }



    /**
     * Get the GuiDetails, preferred the temporary one
     * to avoid overwriting the original one
     * @return The GuiDetails
     */
    public GuiDetails getGuiDetails() {
        
        if(this.tempGuiDetails != null) {
            return this.tempGuiDetails;
        }
        
        return this.guiDetails;
        
    }


    /**
     * Get the latest value from each cached supplier
     * @return The updated condition placeholders
     */
    private Map<String, String> getUpdatedConditionPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();

        for(Map.Entry<String, Supplier<String>> entry : this.getGuiDetails().getInjectedConditionPlaceholders().entrySet()) {
            placeholders.put(entry.getKey(), entry.getValue().get());
        }

        return placeholders;
    }

    /**
     * Check if the element is a static one by checking if contains
     * conditions, changed placeholders or condition placeholders
     * @param element The element to check
     * @return True if the element is cacheable
     */
    private boolean isCacheable(GuiElement element) {

        boolean placeholdersUnchanged = element.containsPlaceholders() && !element.havePlaceholdersChanged(tempPlayer);
        boolean containsConditionPlaceholders = element.containsConditionPlaceholders(guiDetails);
        boolean conditionAbsent = !element.getCondition().isPresent();

        if(!conditionAbsent) return true;
        if(!placeholdersUnchanged) return true;

        return !containsConditionPlaceholders;  // todo I inverted logically the condition here, if some kind of problem occurs, take a look here
    }


}

