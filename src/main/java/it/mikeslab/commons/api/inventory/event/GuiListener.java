package it.mikeslab.commons.api.inventory.event;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.CustomInventory;
import it.mikeslab.commons.api.inventory.GuiFactory;
import it.mikeslab.commons.api.inventory.GuiType;
import it.mikeslab.commons.api.inventory.helper.ActionHelper;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.util.action.internal.ConsumerFilter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@RequiredArgsConstructor
public class GuiListener implements Listener {

    private final GuiFactory guiFactory;
    private final JavaPlugin instance;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null) {
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();

        CustomInventory customInventory = guiFactory.getCustomInventory(
                event.getWhoClicked().getUniqueId(),
                clickedInventory
        );

        if (customInventory == null) return;

        CustomGui customGui = customInventory.getCustomGui();

        if(customGui == null) return;

        event.setCancelled(true);

        this.performClickAction(
                customGui,
                event
        );
    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        Inventory draggedInventory = event.getView().getTopInventory();
        UUID playerUUID = event.getWhoClicked().getUniqueId();

        if(isCustomInventory(playerUUID, draggedInventory)) {
            event.setCancelled(true);
        }

    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        Inventory inventory = event.getInventory();

        CustomInventory customInventory = guiFactory.getCustomInventory(
                event.getPlayer().getUniqueId(),
                inventory
        );

        if(customInventory == null) return;

        CustomGui customGui = customInventory.getCustomGui();

        if(customGui == null) return;

        // Cancel animation if present and scheduled
        if(customGui.isAnimated() && customGui.getAnimationTaskId() != -1) {
            Bukkit.getScheduler().cancelTask(customGui.getAnimationTaskId());
        }

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
            Collection<GuiElement> clickedElement = getClickedElement(gui, clickedChar, clickedSlot);
            if(clickedElement == null) return;

            GuiInteractEvent guiInteractEvent = fireClickEvent(event, clickedElement);
            if(guiInteractEvent.isCancelled()) return;

            this.executeDefaultActionHandler(gui, clickedElement, guiInteractEvent);
            this.handleClickActions(gui, clickedElement, guiInteractEvent);
        }
    }

    /**
     * Get the clicked element, either from the page elements or the main elements
     * @param gui The gui
     * @param clickedChar The clicked char
     * @param clickedSlot The clicked slot
     * @return The clicked elements
     */
    private Collection<GuiElement> getClickedElement(CustomGui gui, char clickedChar, int clickedSlot) {
        Collection<GuiElement> clickedElement;
        boolean isPageElement = gui.getGuiDetails().getTempPageElements().containsKey(clickedChar);

        if(isPageElement) {
            clickedElement = Collections.singletonList(
                    gui.getGuiDetails().getTempPageElements().get(clickedChar).get(clickedSlot)
            );
        } else {
            clickedElement = gui.getGuiDetails().getElements().get(clickedChar);
        }
        return clickedElement;
    }

    /**
     * Fire the Bukkit custom click event
     * @param event The original click event
     * @param clickedElement The clicked element
     * @return The custom click event, already fired
     */
    private GuiInteractEvent fireClickEvent(InventoryClickEvent event, Collection<GuiElement> clickedElement) {
        GuiInteractEvent guiInteractEvent = new GuiInteractEvent((Player) event.getWhoClicked(), clickedElement);
        Bukkit.getPluginManager().callEvent(guiInteractEvent);
        return guiInteractEvent;
    }

    /**
     * Run the default action handler
     * @param gui The gui
     * @param clickedElements The clicked element
     * @param event The custom click event
     */
    private void executeDefaultActionHandler(CustomGui gui, Collection<GuiElement> clickedElements, GuiInteractEvent event) {
        if(guiFactory.getActionHandler() != null) {

            ActionHelper actionHelper = new ActionHelper(
                    guiFactory,
                    event.getWhoClicked(),
                    gui,
                    event
            );

            for(GuiElement element : clickedElements) {
                actionHelper.executeActionHandler(element);
            }
        }
    }

    /**
     * Handle the click actions
     * @param gui The gui
     * @param clickedElement The clicked element
     * @param guiInteractEvent The custom click event
     */
    private void handleClickActions(CustomGui gui, Collection<GuiElement> clickedElement, GuiInteractEvent guiInteractEvent) {

        GuiElement firstElement = clickedElement.iterator().next();

        String internalValue = firstElement.getInternalValue();
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

    boolean isCustomInventory(UUID referencePlayerUUID, Inventory inventory) {
        return guiFactory.getCustomInventory(referencePlayerUUID, inventory) != null;
    }




}
