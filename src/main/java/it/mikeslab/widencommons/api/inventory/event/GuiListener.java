package it.mikeslab.widencommons.api.inventory.event;

import it.mikeslab.widencommons.api.inventory.CustomGui;
import it.mikeslab.widencommons.api.inventory.GuiType;
import it.mikeslab.widencommons.api.inventory.factory.GuiFactoryImpl;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class GuiListener implements Listener {

    private final GuiFactoryImpl guiFactoryImpl;



    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if(event.getClickedInventory() == null) {
            return;
        }

        Inventory clickedInventory = event.getClickedInventory();

        for(int id : guiFactoryImpl.getCachedGuis().keySet()) {

            CustomGui customGui = guiFactoryImpl.getCachedGuis().get(id);

            if(isCustomGui(clickedInventory)) {

                event.setCancelled(true);

                this.performClickAction(
                        event.getSlot(),
                        customGui,
                        event
                );

                // Stops searching for the gui
                // to prevent multiple actions
                return;

            }
        }

    }


    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {

        Inventory draggedInventory = event.getView().getTopInventory();

        if(isCustomGui(draggedInventory)) {
            event.setCancelled(true);
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


    private boolean isCustomGui(Inventory inventory) {
        return inventory.getHolder() instanceof CustomGui;
    }






}
