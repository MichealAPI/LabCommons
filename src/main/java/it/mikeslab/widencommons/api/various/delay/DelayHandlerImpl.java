package it.mikeslab.widencommons.api.various.delay;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DelayHandlerImpl<T extends DelayedAction> implements DelayHandler<T> {

    private final Map<Player, Map<T, Long>> delays;
    private final String timeFormat;

    public DelayHandlerImpl(String timeFormat) {
        this.delays = new HashMap<>();
        this.timeFormat = timeFormat;
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
    public long getRemainingTime(Player player, T delayType) {
        Map<T, Long> playerDelays = delays.getOrDefault(player, null);

        if(playerDelays != null) {
            Long delay = playerDelays.get(delayType);

            if(delay != null && delay > System.currentTimeMillis()) {

                // check if the player has the bypass permission,
                // otherwise, apply the existing delay
                if(player.hasPermission(delayType.getBypassPermission())) {
                    return 0;
                }

                return delay - System.currentTimeMillis();
            }
        }

        return 0;
    }

    @Override
    public String format(long time) {
        return new SimpleDateFormat(this.timeFormat).format(time);
    }

    @Override
    public void performDelayed(Player player, T delayType, Component delayActive, Consumer<Void> action) {
        long remainingTime = this.getRemainingTime(player, delayType);

        if(remainingTime > 0) {

            TextReplacementConfig config = TextReplacementConfig.builder()
                    .matchLiteral("<remaining>")
                    .replacement(this.format(remainingTime))
                    .build();

            player.sendMessage(
                    delayActive.replaceText(config)
            );
            return;
        }

        action.accept(null);

        this.registerDelay(player, delayType);
    }
}
