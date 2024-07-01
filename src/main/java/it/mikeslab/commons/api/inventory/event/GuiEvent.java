package it.mikeslab.commons.api.inventory.event;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.util.action.ActionHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

@Setter
@Getter
@RequiredArgsConstructor
public class GuiEvent extends Event implements Cancellable {

    boolean cancelled;

    final Player player;
    final CustomGui gui;

    final ActionHandler.ActionEvent when;

    private static final HandlerList HANDLERS = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    // Quick fix for the missing method in latest versions of Spigot
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }



}
