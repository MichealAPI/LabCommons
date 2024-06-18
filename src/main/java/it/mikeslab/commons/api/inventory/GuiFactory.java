package it.mikeslab.commons.api.inventory;

import it.mikeslab.commons.api.inventory.config.ConditionParser;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.util.action.ActionHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface is used to create and manage custom inventories.
 * Performs basic CRUDs and caching operations.
 */
public interface GuiFactory {

    /**
     * Creates a new custom inventory and caches it.
     * @param details The details of the inventory
     * @return The id of the cached inventory
     */
    int create(GuiDetails details);

    /**
     * Opens the inventory to the player
     * @param player The player
     * @param id The id of the inventory
     */
    void open(Player player, int id);

    /**
     * Closes all open custom inventories with the same id
     * @param id The id of the inventory
     */
    void closeAll(int id);

    /**
     * Destroys the inventory with the given id
     * @param id The id of the inventory
     */
    void destroy(int id);

    /**
     * Updates the inventory with the given id
     * @param id The id of the inventory
     * @param newGuiDetails The new details of the inventory
     */
    void update(int id, @NotNull GuiDetails newGuiDetails);

    /**
     * Gets the custom inventory with the given id
     * @param id The id of the inventory
     * @return The custom inventory
     */
    @Nullable
    CustomGui getCustomGui(int id);

    /**
     * Sets the action handler
     * @param handler The handler
     */
    void setActionHandler(ActionHandler handler);

    /**
     * Sets the condition parser
     * @param parser The parser
     */
    void setConditionParser(ConditionParser parser);

    /**
     * Gets the action handler
     * @return The action handler
     */
    ConditionParser getConditionParser();

}
