package it.mikeslab.commons.api.inventory;

import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.GuiContext;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface CustomInventory {

    /**
     * Get the id of the gui
     * @return The id of the gui
     */
    default int getId() {
        return this.getGuiContext().getId();
    }

    /**
     * Show the gui to the player
     * @param player The player
     */
    default void show(Player player) {

        if(!isValid()) return;

        this.getGuiContext().getGuiFactory().open(
                player,
                this.getId()
        );

    }

    /**
     * Checks if the factory actually contains the given inventory id
     * @return True if the inventory is valid, false otherwise
     */
    default boolean isValid() {

        CustomGui customGui = this.getGuiContext().getGuiFactory().getCustomGui(
                this.getId()
        );

        return customGui != null;

    }

    /**
     * Get the consumers of the gui
     * @return The consumers of the gui
     */
    default Optional<Map<String, Consumer<GuiInteractEvent>>> getConsumers() {
        return Optional.empty(); // These consumers are related to the internal value of a GuiElement, not the actions
    }

    /**
     * Set the placeholders of the gui, applied to each element
     * Default implementation actually updates the gui by calling the update method of the GuiFactory
     * @param guiDetails The details of the gui
     */
    default void setPlaceholders(GuiDetails guiDetails) {
        this.getGuiContext().getGuiFactory().update(
                getId(),
                guiDetails
        );
    }

    /**
     * Get the inventory type of the gui
     * @return The inventory type of the gui
     */
    default InventoryType getInventoryType() {
        return this.getGuiContext().getInventoryType();
    }

    /**
     * Get the file name of the gui
     * @return The file name of the gui
     */
    default Path getRelativePath() {
        return this.getGuiContext().getRelativePath();
    }

    /**
     * Set the id of the gui
     * @param id The id of the gui
     */
    default void setId(int id) {
        this.getGuiContext().setId(id);
    }

    default void setGuiFactory(GuiFactory guiFactory) {
        this.getGuiContext().setGuiFactory(guiFactory);
    }

    /**
     * Get the context of the custom inventory
     */
    GuiContext getGuiContext();

    /**
     * Set the context of the custom inventory
     * @param context The context of the custom inventory
     */
    void setCustomContext(GuiContext context);

    @Nullable
    default Inventory getInventory() {

        CustomGui customGui = this.getCustomGui().getGuiFactory().getCustomGui(
                this.getId()
        );

        if(customGui == null) return null;

        return customGui.getInventory();
    }

    /**
     * Get the custom gui of the inventory if it exists in the factory
     * @return The custom gui instance of the inventory
     */
    @Nullable
    default CustomGui getCustomGui() {
        return this.getGuiContext().getGuiFactory().getCustomGui(
                this.getId()
        );
    }


}
