package it.mikeslab.commons.api.inventory.util.action;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.event.GuiEvent;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;

public interface ActionHandler {

    /**
     * Handle the action with the given arguments
     * @param inventoryId The id of the inventory
     * @param actionWithArgs The action with the arguments
     */
    void handleAction(int inventoryId, String actionWithArgs, GuiInteractEvent event);

    // void handleAction(int inventoryId, String actionWithArgs, GuiEvent event);

    /**
     * Register an action with the given prefix
     * @param prefix The prefix of the action
     * @param action The action to register
     */
    void registerAction(String prefix, GuiAction action);

    /**
     * Register all the actions
     * @param actionsMap The map of actions to register
     */
    void registerActions(Multimap<String, GuiAction> actionsMap);

    /**
     * Inject an action into the map
     * @param inventoryId The id of the inventory to inject the action into
     * @param prefix The prefix of the action
     * @param action The action to inject
     */
    void injectAction(int inventoryId, String prefix, GuiAction action);

}
