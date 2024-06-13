package it.mikeslab.commons.api.inventory.factory;

import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.GuiFactory;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.util.PageSystem;
import it.mikeslab.commons.api.logger.LoggerUtil;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class GuiFactoryImpl implements GuiFactory {

    @Getter private final Map<Integer, CustomGui> cachedGuis;

    private final JavaPlugin instance;

    private int idCounter = 0;

    public GuiFactoryImpl(final JavaPlugin instance) {
        this.cachedGuis = new HashMap<>();
        this.instance = instance;
    }




    @Override
    public int create(GuiDetails guiDetails) {

        int id = getAndIncrementId();
        CustomGui customGui = new CustomGui(
                guiDetails,
                this,
                instance,
                id
        );

        this.cachedGuis.put(id, customGui);

        return id;
    }



    @Override
    public void open(Player player, int id) {

        if(!cachedGuis.containsKey(id)) {
            LoggerUtil.log(
                    LabCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.API,
                    "(OPEN) Gui with id " + id + " not found"
            );
            return;
        }

        CustomGui customGui = cachedGuis.get(id);
        customGui.generateInventory();

        player.openInventory(customGui.getInventory());

        // Update default page systems
        // to allow them to appear in the inventory
        customGui.getPageSystemMap().values().forEach(PageSystem::updateInventory);


    }



    @Override
    public void closeAll(int id) {

        for(HumanEntity player : cachedGuis.get(id).getInventory().getViewers()) {

            player.closeInventory();

        }

    }




    @Override
    public void destroy(int id) {
        cachedGuis.remove(id);
    }




    @Override
    public void update(int id, @NotNull GuiDetails newGuiDetails) {

        if(!cachedGuis.containsKey(id)) {
            LoggerUtil.log(
                    LabCommons.PLUGIN_NAME,
                    Level.WARNING,
                    LoggerUtil.LogSource.API,
                    "(UPDATE) Gui with id " + id + " not found"
            );
            return;
        }

        CustomGui customGui = new CustomGui(
                newGuiDetails,
                this,
                instance,
                id
        );

        cachedGuis.put(id, customGui);

    }

    @Override
    @Nullable
    public CustomGui getCustomGui(int id) {
        return cachedGuis.getOrDefault(id, null);
    }


    /**
     * Get and increment the ID counter
     * Used to assign unique IDs to each gui
     * @return the id
     */
    private int getAndIncrementId() {
        int counterCopy = idCounter;

        idCounter++;

        return counterCopy;
    }


}
