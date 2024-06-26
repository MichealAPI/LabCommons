package it.mikeslab.commons.api.inventory.util.action;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ActionHandlerImpl implements ActionHandler {

    private final Multimap<String, GuiAction> globalActionsMap;
    private final Map<Integer, Multimap<String, GuiAction>> injectedActions;

    public ActionHandlerImpl(Multimap<String, GuiAction> globalActionsMap) {
        this.globalActionsMap = globalActionsMap;
        this.injectedActions = new HashMap<>();
    }

    @Override
    public void handleAction(int inventoryId, String actionWithArgs, GuiInteractEvent event) {

        String[] action = actionWithArgs.split(":");
        String prefix = action[0];
        String args = action.length > 1 ? action[1] : "";

        // Remove leading space from args if present
        if(args.startsWith(" ")) {
            args = args.substring(1);
        }

        // Handle global actions
        if (this.globalActionsMap.containsKey(prefix)) {
            for (GuiAction guiAction : this.globalActionsMap.get(prefix)) {
                guiAction.getAction().accept(event, args);
            }
        }

        // Handle custom-injected actions specific to the inventory
        Multimap<String, GuiAction> injectedActions = this.injectedActions.get(inventoryId);
        if(injectedActions != null && injectedActions.containsKey(prefix)) {
            for (GuiAction guiAction : injectedActions.get(prefix)) {
                guiAction.getAction().accept(event, args);
            }
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

        Multimap<String, GuiAction> actions = this.injectedActions.getOrDefault(
                inventoryId,
                ArrayListMultimap.create()
        );

        actions.put(prefix, action);

        this.injectedActions.put(inventoryId, actions);
    }


}
