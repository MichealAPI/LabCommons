package it.mikeslab.commons.api.inventory.event;

import it.mikeslab.commons.api.inventory.ConsumerFilter;
import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.GuiType;
import it.mikeslab.commons.api.inventory.factory.GuiFactoryImpl;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
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
import java.util.Optional;

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

            // todo transform in spigot
            // if(event.getReason() == InventoryCloseEvent.Reason.PLUGIN || event.getReason() == InventoryCloseEvent.Reason.OPEN_NEW) return;
            //if(customGui.getGuiDetails().isCloseable()) return;

            Player player = (Player) event.getPlayer();

            // Inventory needs to be reopened after 1 tick, otherwise it will fire up the OPEN_NEW reason when closed again
            // by the player
            Bukkit.getServer().getScheduler().runTaskLater(instance, () -> {
                player.openInventory(customGui.getInventory());
            }, 1L);


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

            GuiElement clickedElement;

            boolean isPageElement = gui
                    .getGuiDetails()
                    .getTempPageElements()
                    .containsKey(clickedChar);

            if(isPageElement) {
                clickedElement = gui.getGuiDetails()
                        .getTempPageElements()
                        .get(clickedChar)
                        .get(clickedSlot);

            } else {

                // We can directly take the first since it's not a page element so, it's not a list // todo is it real?
                clickedElement = gui.getGuiDetails()
                        .getElements()
                        .get(clickedChar).stream()
                        .findFirst()
                        .orElse(null);

            }

            if(clickedElement == null) return;

            // Fire the click event
            GuiInteractEvent guiInteractEvent = new GuiInteractEvent(
                    (Player) event.getWhoClicked(),
                    clickedElement
            );

            Bukkit.getPluginManager().callEvent(guiInteractEvent);

            if(guiInteractEvent.isCancelled()) return;
            // End of fire the click event

            String internalValue = clickedElement.getInternalValue();
            if(internalValue == null) return;

            internalValue = internalValue.toUpperCase();

            boolean hasAction = gui
                    .getGuiDetails()
                    .getClickActions()
                    .containsKey(internalValue);

            if(hasAction) {
                gui
                        .getGuiDetails()
                        .getClickActions()
                        .get(internalValue)
                        .accept(guiInteractEvent);
            }

            // Global action
            if(gui.getGuiDetails().getClickActions().containsKey(ConsumerFilter.ANY.getFilter())) {
                gui.getGuiDetails()
                        .getClickActions()
                        .get(ConsumerFilter.ANY.getFilter())
                        .accept(guiInteractEvent);
            }

        }

    }

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
