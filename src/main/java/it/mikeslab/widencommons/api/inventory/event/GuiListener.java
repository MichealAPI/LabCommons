package it.mikeslab.widencommons.api.inventory.event;

import it.mikeslab.widencommons.api.inventory.CustomGui;
import it.mikeslab.widencommons.api.inventory.GuiType;
import it.mikeslab.widencommons.api.inventory.factory.GuiFactoryImpl;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class GuiListener implements Listener {

    private final GuiFactoryImpl guiFactoryImpl;

    // Since this is a separate event, we may need
    // to get custom gui details after 1ms delay
    // to ensure both events are fired
    //@EventHandler
    @ApiStatus.Experimental
    public void onAnvilMenuPrepare(PrepareAnvilEvent event) {

        AnvilInventory anvilInventory = event.getInventory();

        CustomGui customGui = findCustomGui(anvilInventory);

        if(customGui != null) {

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

        CustomGui customGui = findCustomGui(clickedInventory);

        if (customGui != null) {

            event.setCancelled(true);

            this.performClickAction(
                    event.getSlot(),
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

            CustomGui customGui = findCustomGui(inventory);

            if(customGui == null) return;
            if(event.getReason() == InventoryCloseEvent.Reason.PLUGIN) return;
            if(customGui.getGuiDetails().isCloseable()) return;

            Player player = (Player) event.getPlayer();
            player.openInventory(customGui.getInventory());

        }

    }


    /**
     * Perform the click action
     * @param clickedSlot The slot clicked
     * @param gui The gui
     * @param event The event
     */
    private void performClickAction(int clickedSlot, CustomGui gui, InventoryClickEvent event) {

        String[] layout = gui.getGuiDetails().getInventoryLayout();
        GuiType guiType = gui.getGuiDetails().getGuiType();
        int rowLength = guiType.getRowLength();

        int clickedRow = clickedSlot / rowLength;

        char clickedChar = layout[clickedRow].charAt(clickedSlot % rowLength);

        if(gui.getGuiDetails().getElements().containsKey(clickedChar)) {

            Optional<Consumer<InventoryClickEvent>> consumer = Optional.ofNullable(
                    gui.getGuiDetails().getElements()
                            .get(clickedChar)
                            .getOnClick()
            );

            consumer.ifPresent(lambdaConsumer -> lambdaConsumer.accept(event));

        }

    }

    private CustomGui findCustomGui(Inventory inventory) {

        if(!isCustomGui(inventory)) {
            return null;
        }

        for(int id : guiFactoryImpl.getCachedGuis().keySet()) {

            CustomGui customGui = guiFactoryImpl.getCachedGuis().get(id);

            CustomGui inventoryHolder = (CustomGui) inventory.getHolder();

            if(customGui.equals(inventoryHolder)) {
                return customGui;
            }

        }
        return null;
    }

    private boolean isCustomGui(Inventory inventory) {
        return inventory.getHolder() instanceof CustomGui;
    }




}
