package it.mikeslab.commons.api.inventory.util.action;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ActionHandlerImpl implements ActionHandler {

    private final Multimap<String, GuiAction> globalActionsMap;
    private final Map<Integer, Map<String, GuiAction>> injectedActions;

    public ActionHandlerImpl(Multimap<String, GuiAction> globalActionsMap) {
        this.globalActionsMap = globalActionsMap;
        this.injectedActions = new HashMap<>();
    }

    @Override
    public void handleAction(int inventoryId, String actionWithArgs, GuiInteractEvent event) {

        // If it doesn't contain a colon, it's not a valid action
        // if (!actionWithArgs.contains(":")) {
        //    return;
        //}

        String[] action = actionWithArgs.split(":");
        String prefix = action[0];
        String args = action.length > 1 ? action[1] : "";

        // If the prefix is not registered, return
        if (this.globalActionsMap.containsKey(prefix)) {

            // Get the action from the map
            Collection<GuiAction> globalActions = this.globalActionsMap.get(prefix);

            // Iterate over the globalActions
            for (GuiAction guiAction : globalActions) {
                guiAction.getAction().accept(event, args);
            }

        }

        // Custom inventory injected actions, are more specific than globals
        Map<String, GuiAction> injectedActions = this.injectedActions.getOrDefault(inventoryId, null);

        if(injectedActions != null && injectedActions.containsKey(prefix)) {

            GuiAction guiAction = injectedActions.get(prefix);

            guiAction.getAction().accept(event, args);

        }

    }

    @Override
    public void registerAction(String prefix, GuiAction action) {
        this.globalActionsMap.put(prefix, action);
    }

    @Override
    public void registerActions(Multimap<String, GuiAction> actionsMap) {
        this.globalActionsMap.putAll(actionsMap);
    }

    @Override
    public void injectAction(int inventoryId, String prefix, GuiAction action) {

        Map<String, GuiAction> actions = this.injectedActions.getOrDefault(inventoryId, new HashMap<>());

        actions.put(prefix, action);

        this.injectedActions.put(inventoryId, actions);
    }


}
