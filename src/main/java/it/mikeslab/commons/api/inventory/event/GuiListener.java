package it.mikeslab.commons.api.inventory.event;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.CustomInventory;
import it.mikeslab.commons.api.inventory.GuiType;
import it.mikeslab.commons.api.inventory.factory.GuiFactory;
import it.mikeslab.commons.api.inventory.helper.ActionHelper;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.util.action.ActionHandler;
import it.mikeslab.commons.api.inventory.util.action.internal.ConsumerFilter;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class GuiListener implements Listener {

    private final GuiFactory guiFactory;

    @Getter
    private final HashMap<UUID, String> openInventories = new HashMap<>();

    public GuiListener(GuiFactory guiFactory, JavaPlugin plugin) {
        this.guiFactory = guiFactory;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

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
    public void onInventoryOpen(InventoryOpenEvent event) {

            Player player = (Player) event.getPlayer();

            CustomGui customGui = this.getCustomGui(
                    player,
                    event
            );

            if(customGui == null) return;

            this.callEvent(
                    player,
                    customGui,
                    ActionHandler.ActionEvent.OPEN
            );

            this.openInventories.put(
                    player.getUniqueId(),
                    customGui.getGuiDetails().getIdentifier()
            );
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();

        CustomGui customGui = this.getCustomGui(
                player,
                event
        );

        if(customGui == null) return;

        // Cancel animation if present and scheduled
        if(customGui.isAnimated() && customGui.getAnimationTaskId() != -1) {
            Bukkit.getScheduler().cancelTask(customGui.getAnimationTaskId());
        }

        this.callEvent(
                player,
                customGui,
                ActionHandler.ActionEvent.CLOSE
        );

        this.openInventories.computeIfPresent(player.getUniqueId(), (uuid, identifier) -> {
            if(identifier.equals(customGui.getGuiDetails().getIdentifier())) {
                return null; // Remove the entry if it matches
            }

            LogUtils.debug(LogUtils.LogSource.API, String.format("Tried to close a GUI that was not the current one. Expected: %s, Actual: %s",
                    identifier,
                    customGui.getGuiDetails().getIdentifier()
            ));

            return identifier; // Keep the entry if it doesn't match
        });

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

    /**
     * Get the custom gui from the player and the event
     * @param player The player
     * @param event The event
     * @return The custom gui
     */
    private CustomGui getCustomGui(Player player, InventoryEvent event) {
        CustomInventory customInventory = guiFactory.getCustomInventory(
                player.getUniqueId(),
                event.getInventory()
        );

        if(customInventory == null) return null;

        return customInventory.getCustomGui();
    }

    private void callEvent(Player player, CustomGui gui, ActionHandler.ActionEvent when) {
        GuiEvent guiEvent = new GuiEvent(
                player,
                gui,
                when
        );

        Bukkit.getPluginManager().callEvent(guiEvent);
    }

}
