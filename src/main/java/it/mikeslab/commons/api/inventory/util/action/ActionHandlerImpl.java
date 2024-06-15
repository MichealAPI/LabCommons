package it.mikeslab.commons.api.inventory.util.action;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.commons.api.inventory.pojo.action.GuiActionArg;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class ActionHandlerImpl implements ActionHandler {

    private final Multimap<String, GuiAction> globalActionsMap;
    private final Map<Integer, Map<String, GuiAction>> injectedActions;

    public ActionHandlerImpl(Multimap<String, GuiAction> globalActionsMap) {
        this.globalActionsMap = globalActionsMap;
        this.injectedActions = new HashMap<>();
    }

    @Override
    public void handleAction(int inventoryId, String actionWithArgs, GuiActionArg user) {

        // If it doesn't contain a colon, it's not a valid action
        if (!actionWithArgs.contains(":")) {
            return;
        }

        String[] action = actionWithArgs.split(":");
        String prefix = action[0];
        String args = action[1];

        // If the prefix is not registered, return
        if (!this.globalActionsMap.containsKey(prefix)) {
            return;
        }

        // Get the action from the map
        Collection<GuiAction> globalActions = this.globalActionsMap.get(prefix);

        // Iterate over the globalActions
        for (GuiAction guiAction : globalActions) {

            Optional<Object> optionalPassedValue = getPassedValue(guiAction, user);

            // Perform the action if a valid passedValue is present
            optionalPassedValue.ifPresent(passedValue ->
                    guiAction.getAction().accept(passedValue, args)
            );
        }

        // Custom inventory injected actions, are more specific than globals
        Map<String, GuiAction> injectedActions = this.injectedActions.getOrDefault(inventoryId, null);

        if(injectedActions != null && injectedActions.containsKey(prefix)) {

            GuiAction guiAction = injectedActions.get(prefix);

            Optional<Object> optionalPassedValue = getPassedValue(guiAction, user);

            optionalPassedValue.ifPresent(passedValue ->
                    guiAction.getAction().accept(passedValue, args)
            );
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

    private Optional<Object> getPassedValue(GuiAction guiAction, GuiActionArg user) {
        if(isTargetPlayer(guiAction)) {
            return Optional.ofNullable(user.getTargetPlayer());
        } else if(isTargetConsole(guiAction)) {
            return Optional.ofNullable(user.getConsole());
        } else {
            // Log or handle unexpected situation
            return Optional.empty();
        }
    }

    /**
     * Check if the action requires a player
     * @param action The action to check
     * @return True if the action requires a player, false otherwise
     */
    boolean isTargetPlayer(GuiAction action) {
        return action.getRequiredClass().equals(Player.class);
    }

    /**
     * Check if the action requires a CommandSender
     * @param action The action to check
     * @return True if the action requires a CommandSender, false otherwise
     */
    boolean isTargetConsole(GuiAction action) {
        return action.getRequiredClass().equals(ConsoleCommandSender.class);
    }

}
