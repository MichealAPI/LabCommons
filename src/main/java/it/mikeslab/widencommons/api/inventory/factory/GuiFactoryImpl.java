package it.mikeslab.widencommons.api.inventory.factory;

import it.mikeslab.widencommons.api.inventory.CustomGui;
import it.mikeslab.widencommons.api.inventory.GuiFactory;
import it.mikeslab.widencommons.api.inventory.pojo.GuiDetails;
import it.mikeslab.widencommons.api.logger.LoggerUtil;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class GuiFactoryImpl implements GuiFactory {

    @Getter private final Map<Integer, CustomGui> cachedGuis;
    private int idCounter = 0;

    public GuiFactoryImpl() {
        this.cachedGuis = new HashMap<>();
    }




    @Override
    public int create(GuiDetails guiDetails) {

        CustomGui customGui = new CustomGui(guiDetails);
        int id = getAndIncrementId();

        this.cachedGuis.put(id, customGui);

        return id;
    }



    @Override
    public void open(Player player, int id) {

        if(!cachedGuis.containsKey(id)) {
            LoggerUtil.log(
                    Level.WARNING,
                    LoggerUtil.LogSource.API,
                    "(OPEN) Gui with id " + id + " not found"
            );
            return;
        }

        CustomGui customGui = cachedGuis.get(id);
        customGui.generateInventory();

        player.openInventory(customGui.getInventory());

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
    public void update(int id, GuiDetails newGuiDetails) {

        if(!cachedGuis.containsKey(id)) {
            LoggerUtil.log(
                    Level.WARNING,
                    LoggerUtil.LogSource.API,
                    "(UPDATE) Gui with id " + id + " not found"
            );
            return;
        }

        CustomGui customGui = new CustomGui(newGuiDetails);
        cachedGuis.put(id, customGui);

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
