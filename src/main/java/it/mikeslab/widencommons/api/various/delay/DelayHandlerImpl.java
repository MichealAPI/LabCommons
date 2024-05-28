package it.mikeslab.widencommons.api.various.delay;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DelayHandlerImpl<T extends DelayedAction> implements DelayHandler<T> {

    private final Map<Player, Map<T, Long>> delays;

    public DelayHandlerImpl() {
        this.delays = new HashMap<>();
    }

    @Override
    public void registerDelay(Player player, T delayType) {
        this.registerDelay(player, delayType, delayType.getDefaultDelay());
    }

    @Override
    public void registerDelay(Player player, T delayType, long delay) {

        // check if the player has the bypass permission,
        if(player.hasPermission(delayType.getBypassPermission())) {
            return;
        }

        Map<T, Long> playerDelays = delays.getOrDefault(player, null);

        if(playerDelays == null) {
                playerDelays = new HashMap<>();
                delays.put(player, playerDelays);
        }

        playerDelays.put(delayType, System.currentTimeMillis() + delay);
    }

    @Override
    public void removeDelay(Player player, T delayType) {
        Map<T, Long> playerDelays = delays.getOrDefault(player, null);

        if(playerDelays != null) {
            playerDelays.remove(delayType);
        }
    }

    @Override
    public boolean hasDelay(Player player, T delayType) {
        Map<T, Long> playerDelays = delays.getOrDefault(player, null);

        if(playerDelays != null) {
            Long delay = playerDelays.get(delayType);

            if(delay != null && delay > System.currentTimeMillis()) {

                // check if the player has the bypass permission,
                // otherwise, apply the existing delay
                return !player.hasPermission(delayType.getBypassPermission());
            }
        }

        return false;
    }
}
