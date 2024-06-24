package it.mikeslab.commons.api.inventory.event;

import it.mikeslab.commons.api.inventory.CustomGui;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
@Deprecated
@RequiredArgsConstructor
public abstract class GuiEvent extends Event {

    final CustomGui customGui;
    final Player viewer;

}
