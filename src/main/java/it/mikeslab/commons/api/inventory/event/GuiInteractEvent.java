package it.mikeslab.commons.api.inventory.event;

import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Event that is called when a player interacts with a GUI
 */
@Getter
@Setter
@RequiredArgsConstructor
public class GuiInteractEvent extends Event implements Cancellable {

    private final static HandlerList HANDLERS = new HandlerList();

    private final Player whoClicked;
    private final Collection<GuiElement> clickedElements;
    private boolean cancelled;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    // Quick fix for the missing method in latest versions of Spigot
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public GuiInteractEvent(GuiEvent parentEvent) {
        this.whoClicked = parentEvent.getPlayer();
        this.clickedElements = null;
    }


}
