package it.mikeslab.commons.api.inventory.pojo.action;

import lombok.Builder;
import lombok.Getter;

import java.util.function.BiConsumer;

/**
 * Represents an action that can be performed by the user
 * or by the plugin itself
 */
@Getter
@Builder
public class GuiAction {

    /**
     * The action that will be performed
     * <T> - The object that will be used for the action
     *           (e.g. a player, a string, a number, etc.)
     * String - The arguments that will be used for the action
     */
    BiConsumer<Object, String> action;

    Class<?> requiredClass;

}