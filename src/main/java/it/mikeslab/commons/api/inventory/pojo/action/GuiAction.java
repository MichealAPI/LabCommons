package it.mikeslab.commons.api.inventory.pojo.action;

import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;

/**
 * Represents an action that can be performed by the user
 * or by the plugin itself
 */
@Getter
@RequiredArgsConstructor
public class GuiAction {

    /**
     * The action that will be performed
     * String - The arguments that will be used for the action
     */
    private final BiConsumer<GuiInteractEvent, String> action;

}
