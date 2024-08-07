package it.mikeslab.commons.api.various.delay;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public interface DelayHandler<T extends DelayedAction> {

    /**
     * Register a default delay for the player
     * @param player the player to register the delay for
     * @param delayType the delay type
     */
    void registerDelay(Player player, T delayType);

    /**
     * Register a delay for the player
     * @param player the player to register the delay for
     * @param delayType the delay type
     * @param delay the delay in milliseconds
     */
    void registerDelay(Player player, T delayType, long delay);

    /**
     * Remove the delay from the player
     * @param player the player to remove the delay from
     * @param delayType the delay type
     */
    void removeDelay(Player player, T delayType);

    /**
     * Check if the player has a delay
     *
     * @param player    the player to check
     * @param delayType the delay type
     * @return 0 if the player has no delay, otherwise the remaining time
     */
    long getRemainingTime(Player player, T delayType);


    /**
     * Check if the player has a delay
     * @param time the time to check
     * @return the formatted time
     */
    String format(long time);

    /**
     * Perform a delayed action
     * @param player the player to perform the action for
     * @param delayType the delay type
     * @param delayActive the delay active message
     * @param action the action to perform
     */
    void performDelayed(Player player, T delayType, Component delayActive, Runnable action);


}
