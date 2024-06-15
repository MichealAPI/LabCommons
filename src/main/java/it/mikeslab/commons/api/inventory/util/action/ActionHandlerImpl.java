package it.mikeslab.commons.api.inventory.util.action;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.commons.api.inventory.pojo.action.GuiUser;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

public class ActionHandlerImpl implements ActionHandler {

    private final Multimap<String, GuiAction<?>> actionsMap;

    public ActionHandlerImpl(Multimap<String, GuiAction<?>> actionsMap) {
        this.actionsMap = actionsMap;
    }

    @Override
    public void handleAction(String actionWithArgs, GuiUser user) {

        // If it doesn't contain a colon, it's not a valid action
        if (!actionWithArgs.contains(":")) {
            return;
        }

        String[] action = actionWithArgs.split(":");
        String prefix = action[0];
        String args = action[1];

        // If the prefix is not registered, return
        if (!this.actionsMap.containsKey(prefix)) {
            return;
        }

        // Get the action from the map
        Collection<GuiAction<?>> actions = this.actionsMap.get(prefix);

        // Iterate over the actions
        for (GuiAction<?> guiAction : actions) {

            Optional<Object> optionalPassedValue = getPassedValue(guiAction, user);

            // Perform the action if a valid passedValue is present
            optionalPassedValue.ifPresent(passedValue ->
                    guiAction.getAction().accept(passedValue, args)
            );
        }
    }

    @Override
    public void registerAction(String prefix, GuiAction<?> action) {
        this.actionsMap.put(prefix, action);
    }

    @Override
    public void registerActions(Multimap<String, GuiAction<?>> actionsMap) {
        this.actionsMap.putAll(actionsMap);
    }

    private Optional<Object> getPassedValue(GuiAction<?> guiAction, GuiUser user) {
        if(isTargetPlayer(guiAction)) {
            return Optional.ofNullable(user.getTargetPlayer());
        } else if(isTargetCommandSender(guiAction)) {
            return Optional.ofNullable(user.getTargetSender());
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
    boolean isTargetPlayer(GuiAction<?> action) {
        return action.getRequiredClass().equals(Player.class);
    }

    /**
     * Check if the action requires a CommandSender
     * @param action The action to check
     * @return True if the action requires a CommandSender, false otherwise
     */
    boolean isTargetCommandSender(GuiAction<?> action) {
        return action.getRequiredClass().equals(Player.class);
    }

}
