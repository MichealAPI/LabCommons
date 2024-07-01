package it.mikeslab.commons.api.inventory.util.action;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.event.GuiEvent;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionHandlerImpl implements ActionHandler {

    private final Multimap<String, GuiAction> globalActionsMap;
    private final Map<Integer, Multimap<String, GuiAction>> injectedActions;

    private final Map<Integer, String> closeActions, openActions;

    public ActionHandlerImpl(Multimap<String, GuiAction> globalActionsMap) {
        this.globalActionsMap = globalActionsMap;
        this.injectedActions = new HashMap<>();
        this.closeActions = new HashMap<>();
        this.openActions = new HashMap<>();
    }

    @Override
    public void handleAction(int inventoryId, String actionWithArgs, GuiInteractEvent event) {

        String[] action = actionWithArgs.split(":");
        String prefix = action[0];
        String args = action.length > 1 ? actionWithArgs.substring(prefix.length() + 1) : "";

        // Remove leading space from args if present
        if(args.startsWith(" ")) {
            args = args.substring(1);
        }

        ActionContext context = new ActionContext(prefix, event, args);

        this.handleGlobalActions(inventoryId, context);
        this.handleInjectedActions(inventoryId, context);
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

    @Override
    public void registerActions(int inventoryId, ActionEvent when, List<String> actions) {

        Map<Integer, String> reference = this.getReferenceMap(when);

        reference.put(inventoryId, String.join(",", actions));
    }

    @Override
    public void handleActions(int inventoryId, ActionEvent when, GuiEvent event) {

        Map<Integer, String> reference = this.getReferenceMap(when);

        if(reference.isEmpty()) return;

        GuiInteractEvent openInteractionEvent = new GuiInteractEvent(event); // inherit the event characteristics

        for (String action : this.openActions.get(inventoryId).split(",")) {
            this.handleAction(inventoryId, action, openInteractionEvent);
        }
    }


    /**
     * Handle the global actions
     * @param context the action context
     */
    private void handleGlobalActions(int inventoryId, ActionContext context) {

        String prefix = context.getPrefix();
        String args = context.getArgs();
        GuiInteractEvent event = context.getEvent();

        // Check if the action exists in the global actions map
        if (this.globalActionsMap.containsKey(prefix)) {

            boolean areInjectedActionsPresent = this.injectedActions.containsKey(inventoryId);
            boolean isOverridden = areInjectedActionsPresent && this.injectedActions.get(inventoryId).containsKey(prefix);

            if(isOverridden) {
                return;
            }

            for (GuiAction guiAction : this.globalActionsMap.get(prefix)) {

                // accepts a consumer if the action is actually mapped
                guiAction.getAction().accept(event, args);
            }

        }
    }


    private void handleInjectedActions(int inventoryId, ActionContext context) {

        GuiInteractEvent event = context.getEvent();
        String prefix = context.getPrefix();
        String args = context.getArgs();

        // Actions injected by specific inventory handling classes
        Multimap<String, GuiAction> injectedActions = this.injectedActions.get(inventoryId);

        // Check if they're not null and if they're mapped
        if(injectedActions != null && injectedActions.containsKey(prefix)) {

            for (GuiAction guiAction : injectedActions.get(prefix)) {

                // if they're mapped, accept each of them
                guiAction.getAction().accept(event, args);
            }

        }
    }


    /**
     * Get the reference map
     * @param when the action event
     * @return the reference map
     */
    private Map<Integer, String> getReferenceMap(ActionEvent when) {
        return when == ActionEvent.OPEN ? this.openActions : this.closeActions;
    }


    @Getter
    @RequiredArgsConstructor
    private static class ActionContext {

        private final String prefix;
        private final GuiInteractEvent event;
        private final String args;

    }

}
