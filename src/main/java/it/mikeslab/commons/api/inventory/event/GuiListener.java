package it.mikeslab.commons.api.inventory.event;

import it.mikeslab.commons.api.inventory.ConsumerFilter;
import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.GuiType;
import it.mikeslab.commons.api.inventory.factory.GuiFactoryImpl;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.util.PageSystem;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.AbstractMap;
import java.util.Map;

@RequiredArgsConstructor
public class GuiListener implements Listener {

    private final GuiFactoryImpl guiFactoryImpl;
    private final JavaPlugin instance;

    // Since this is a separate event, we may need
    // to get custom gui details after 1ms delay
    // to ensure both events are fired
    //@EventHandler
    @ApiStatus.Experimental
    public void onAnvilMenuPrepare(PrepareAnvilEvent event) {

        AnvilInventory anvilInventory = event.getInventory();

        Map.Entry<Integer, CustomGui> guiEntry = findCustomGui(anvilInventory);

        if(guiEntry != null && guiEntry.getValue() != null) {

            CustomGui customGui = guiEntry.getValue();

            // Set the text of the anvil
            customGui.getGuiDetails()
                    .setText(anvilInventory.getRenameText());

        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null) {
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();

        Map.Entry<Integer, CustomGui> guiEntry = findCustomGui(clickedInventory);

        if (guiEntry != null && guiEntry.getValue() != null) {

            CustomGui customGui = guiEntry.getValue();

            event.setCancelled(true);

            this.performClickAction(
                    customGui,
                    event
            );

        }
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        Inventory draggedInventory = event.getView().getTopInventory();

        if(isCustomGui(draggedInventory)) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        Inventory inventory = event.getInventory();

        if(isCustomGui(inventory)) {

            Map.Entry<Integer, CustomGui> guiEntry = findCustomGui(inventory);

            if(guiEntry == null) return;

            CustomGui customGui = guiEntry.getValue();

            if(customGui == null) return;

            Player player = (Player) event.getPlayer();

            GuiCloseEvent guiCloseEvent = new GuiCloseEvent(
                    event,
                    customGui
            );

            Bukkit.getPluginManager().callEvent(guiCloseEvent);

            if(guiCloseEvent.isCancelled()) {
                Bukkit.getScheduler().runTask(instance, () -> {
                    player.openInventory(customGui.getInventory());
                });
            }

        }

    }


    /**
     * Perform the click action
     * @param gui The gui
     * @param event The event
     */
    private void performClickAction(CustomGui gui, InventoryClickEvent event) {
        int clickedSlot = event.getSlot();
        String[] layout = gui.getGuiDetails().getInventoryLayout();
        GuiType guiType = gui.getGuiDetails().getGuiType();
        int rowLength = guiType.getRowLength();
        int clickedRow = clickedSlot / rowLength;
        char clickedChar = layout[clickedRow].charAt(clickedSlot % rowLength);

        if(gui.getGuiDetails().getElements().containsKey(clickedChar)) {
            GuiElement clickedElement = getClickedElement(gui, clickedChar, clickedSlot);
            if(clickedElement == null) return;

            GuiInteractEvent guiInteractEvent = fireClickEvent(event, clickedElement);
            if(guiInteractEvent.isCancelled()) return;

            runDefaultActionHandler(gui, clickedElement, guiInteractEvent);
            handleClickActions(gui, clickedElement, guiInteractEvent);
        }
    }

    /**
     * Get the clicked element, either from the page elements or the main elements
     * @param gui The gui
     * @param clickedChar The clicked char
     * @param clickedSlot The clicked slot
     * @return The clicked element
     */
    private GuiElement getClickedElement(CustomGui gui, char clickedChar, int clickedSlot) {
        GuiElement clickedElement;
        boolean isPageElement = gui.getGuiDetails().getTempPageElements().containsKey(clickedChar);

        if(isPageElement) {
            clickedElement = gui.getGuiDetails().getTempPageElements().get(clickedChar).get(clickedSlot);
        } else {
            clickedElement = gui.getGuiDetails().getElements().get(clickedChar).stream().findFirst().orElse(null);
        }
        return clickedElement;
    }

    /**
     * Fire the Bukkit custom click event
     * @param event The original click event
     * @param clickedElement The clicked element
     * @return The custom click event, already fired
     */
    private GuiInteractEvent fireClickEvent(InventoryClickEvent event, GuiElement clickedElement) {
        GuiInteractEvent guiInteractEvent = new GuiInteractEvent((Player) event.getWhoClicked(), clickedElement);
        Bukkit.getPluginManager().callEvent(guiInteractEvent);
        return guiInteractEvent;
    }

    /**
     * Run the default action handler
     * @param gui The gui
     * @param clickedElement The clicked element
     * @param guiInteractEvent The custom click event
     */
    private void runDefaultActionHandler(CustomGui gui, GuiElement clickedElement, GuiInteractEvent guiInteractEvent) {
        if(guiFactoryImpl.getActionHandler() != null) {
            clickedElement.getActions().forEach(action -> {
                guiFactoryImpl.getActionHandler().handleAction(gui.getId(), action, guiInteractEvent);
            });
        }
    }

    /**
     * Handle the click actions
     * @param gui The gui
     * @param clickedElement The clicked element
     * @param guiInteractEvent The custom click event
     */
    private void handleClickActions(CustomGui gui, GuiElement clickedElement, GuiInteractEvent guiInteractEvent) {
        String internalValue = clickedElement.getInternalValue();
        if(internalValue == null) return;

        internalValue = internalValue.toUpperCase();
        boolean hasAction = gui.getGuiDetails().getClickActions().containsKey(internalValue);

        if(hasAction) {
            gui.getGuiDetails().getClickActions().get(internalValue).accept(guiInteractEvent);
        }

        if(gui.getGuiDetails().getClickActions().containsKey(ConsumerFilter.ANY.getFilter())) {
            gui.getGuiDetails().getClickActions().get(ConsumerFilter.ANY.getFilter()).accept(guiInteractEvent);
        }
    }

    /**
     * Find the custom gui
     * @param inventory The inventory
     * @return The custom gui
     */
    private Map.Entry<Integer, CustomGui> findCustomGui(Inventory inventory) {

        if(!isCustomGui(inventory)) {
            return null;
        }

        for(int id : guiFactoryImpl.getCachedGuis().keySet()) {

            CustomGui customGui = guiFactoryImpl.getCachedGuis().get(id);

            CustomGui inventoryHolder = (CustomGui) inventory.getHolder();

            if(customGui.equals(inventoryHolder)) {
                return new AbstractMap.SimpleEntry<>(id, customGui);
            }

        }
        return null;
    }

    private boolean isCustomGui(Inventory inventory) {
        return inventory.getHolder() instanceof CustomGui;
    }





}
