package it.mikeslab.commons.api.inventory.util;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.GuiFactory;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
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
        return this.getCustomContext().getId();
    }

    /**
     * Show the gui to the player
     * @param player The player
     */
    default void show(Player player) {

        if(!isValid()) return;

        this.getCustomContext().getGuiFactory().open(
                player,
                this.getId()
        );

    }

    default boolean isValid() {

        CustomGui customGui = this.getCustomContext().getGuiFactory().getCustomGui(
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
     * @param player The player
     * @param guiDetails The details of the gui
     */
    default void setPlaceholders(Player player, GuiDetails guiDetails) {
        this.getCustomContext().getGuiFactory().update(
                getId(),
                guiDetails
        );
    }

    /**
     * If the setup for the gui is completed
     * @return If the setup for the gui is completed
     */
    default boolean isCompleted() {
        return this.getCustomContext().isCompleted();
    }

    /**
     * Get the inventory type of the gui
     * @return The inventory type of the gui
     */
    default InventoryType getInventoryType() {
        return this.getCustomContext().getSettings().getInventoryType();
    }

    /**
     * Get the file name of the gui
     * @return The file name of the gui
     */
    default Path getRelativePath() {
        return this.getCustomContext().getSettings().getRelativePath();
    }

    /**
     * Set the id of the gui
     * @param id The id of the gui
     */
    default void setId(int id) {
        this.getCustomContext().setId(id);
    }

    /**
     * Set the completed flag of the gui
     * @param completed The completed flag of the gui
     */
    default void setCompleted(boolean completed) {
        this.getCustomContext().setCompleted(completed);
    }

    default void setGuiFactory(GuiFactory guiFactory) {
        this.getCustomContext().setGuiFactory(guiFactory);
    }

    /**
     * Get the context of the gui, internal
     * @return The context of the gui
     */
    default InventoryContext getInventoryContext() {
        return this.getCustomContext().getInventoryContext();
    }

    default void setInventoryContext(InventoryContext context) {
        this.getCustomContext().setInventoryContext(context);
    }

    default void setSettings(InventorySettings settings) {
        this.getCustomContext().setSettings(settings);
    }

    /**
     * Get the context of the custom inventory
     */
    CustomInventoryContext getCustomContext();

    /**
     * Set the context of the custom inventory
     * @param context The context of the custom inventory
     */
    void setCustomContext(CustomInventoryContext context);

    @Nullable
    default Inventory getInventory() {
        CustomGui customGui = this.getCustomContext().getGuiFactory().getCustomGui(
                this.getId()
        );

        if(customGui == null) return null;

        return customGui.getInventory();
    }

    @Nullable
    default CustomGui getCustomGui() {
        return this.getCustomContext().getGuiFactory().getCustomGui(
                this.getId()
        );
    }


}
