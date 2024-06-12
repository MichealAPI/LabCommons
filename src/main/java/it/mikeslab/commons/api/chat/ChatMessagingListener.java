package it.mikeslab.commons.api.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatMessagingListener implements ChatMessagingHandler, Listener {

    private final Map<UUID, ChatMessagingContext> contextMap;
    private final JavaPlugin instance;

    public ChatMessagingListener(JavaPlugin instance) {

        this.contextMap = new HashMap<>();
        this.instance = instance;

        instance.getServer()
                .getPluginManager()
                .registerEvents(
                        this,
                        instance
                );

    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        UUID playerUUID = event.getPlayer().getUniqueId();

        if(contextMap.isEmpty() || !contextMap.containsKey(playerUUID)) return;

        ChatMessagingContext context = contextMap.get(playerUUID);
        String message = event.getMessage();

        event.setCancelled(true);

        if(context.getCondition().test(message)) {

            this.cancelTimeoutTask(playerUUID);
            this.contextMap.remove(playerUUID); // Reversed to allow a context-boxing

            context.getSuccess()
                    .accept(
                            event.getPlayer(),
                            message
                    );

        } else {

            context.getFailure()
                    .accept(event.getPlayer());

            if(context.isAbortOnFailure()) {
                this.abort(playerUUID);
            }

        }

    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if(!contextMap.containsKey(playerUUID)) return;

        // This currently just removes the context from the map
        // The check is made because in the future, this function
        // May perform additional operations
        this.abort(
                event.getPlayer().getUniqueId()
        );
    }



    @Override
    public void register(UUID referenceUUID, ChatMessagingContext context) {
        contextMap.put(referenceUUID, context);

        Player player = instance.getServer().getPlayer(referenceUUID);

        if(player != null) { // Player is online
            context.getStart().accept(
                    instance.getServer().getPlayer(referenceUUID)
            );

        } else abort(referenceUUID);

        // Check if the timeout time is -1, if so, don't run the
        // timeout scheduler for this operation
        if(context.getTimeOut() == -1) return;

        // Run the timeout scheduler
        this.runTimeoutScheduler(referenceUUID, context);

    }


    @Override
    public void abort(UUID referenceUUID) {
        contextMap.remove(referenceUUID);

        this.cancelTimeoutTask(referenceUUID);

    }

    private void cancelTimeoutTask(UUID referenceUUID) {
        if(contextMap.containsKey(referenceUUID)) {
            contextMap.get(referenceUUID)
                    .getTimeoutTask()
                    .cancel();
        }
    }

    /**
     * Run the timeout scheduler
     * @param referenceUUID the reference UUID
     * @param context the chat messaging context
     */
    private void runTimeoutScheduler(UUID referenceUUID, ChatMessagingContext context) {

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if(contextMap.containsKey(referenceUUID)) {
                    context.getTimeOutConsumer().accept(
                            instance.getServer().getPlayer(referenceUUID)
                    );
                    abort(referenceUUID);
                }
            }
        }.runTaskLater(
                instance,
                context.getTimeOut()
        );

        this.contextMap
                .get(referenceUUID)
                .setTimeoutTask(task);

    }

}
