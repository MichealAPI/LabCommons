package it.mikeslab.widencommons.api.inventory;

import it.mikeslab.widencommons.api.inventory.pojo.GuiDetails;
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

}
