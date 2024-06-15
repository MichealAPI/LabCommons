package it.mikeslab.commons.api.inventory.util.action;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.commons.api.inventory.pojo.action.GuiUser;

public interface ActionHandler {

    /**
     * Handle the action with the given arguments
     * @param actionWithArgs The action with the arguments
     * @param user The user that is performing the action
     */
    void handleAction(String actionWithArgs, GuiUser user);

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

}
