package it.mikeslab.widencommons.api.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public class ChatMessagingContext {

    private final Predicate<String> condition;

    private final Consumer<Player> start,
            failure,
            timeOutConsumer;

    private final BiConsumer<Player, String> success;

    @Setter private long timeOut;

    @Setter private boolean abortOnFailure;

    @Setter private BukkitTask timeoutTask;

}
