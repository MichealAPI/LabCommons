package it.mikeslab.commons.api.inventory.event;

import it.mikeslab.commons.api.inventory.ConsumerFilter;
import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.GuiType;
import it.mikeslab.commons.api.inventory.factory.GuiFactoryImpl;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.util.ConditionUtil;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@RequiredArgsConstructor
public class GuiListener implements Listener {

    private final GuiFactoryImpl guiFactoryImpl;
    private final JavaPlugin instance;

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

            runDefaultActionHandler(gui, clickedElement, guiInteractEvent);
            handleClickActions(gui, clickedElement, guiInteractEvent);
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
     * @param clickedElement The clicked element
     * @param guiInteractEvent The custom click event
     */
    private void runDefaultActionHandler(CustomGui gui, Collection<GuiElement> clickedElement, GuiInteractEvent guiInteractEvent) {
        if(guiFactoryImpl.getActionHandler() != null) {

            Player player = Bukkit.getPlayer(gui.getOwnerUUID());

            for(GuiElement element : clickedElement) {

                if(element.getCondition().isPresent() && guiFactoryImpl.getConditionParser() != null) {

                    System.out.println("Condition: " + element.getCondition().get());

                    String replacedCondition = ConditionUtil.replace(
                            player,
                            element.getCondition().get(),
                            gui.getGuiDetails().getInjectedConditionPlaceholders()
                    );

                    System.out.println("Replaced condition: " + replacedCondition);

                    boolean can = guiFactoryImpl.getConditionParser().parse(
                            guiInteractEvent.getWhoClicked(),
                            replacedCondition
                    );

                    System.out.println("Can: " + can);

                    if(!can) {
                        continue;
                    }
                }

                element.getActions().forEach(action -> {
                    guiFactoryImpl.getActionHandler().handleAction(gui.getId(), action, guiInteractEvent);
                });
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
