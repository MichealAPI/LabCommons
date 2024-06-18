package it.mikeslab.commons.api.inventory.factory;

import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.GuiFactory;
import it.mikeslab.commons.api.inventory.config.ConditionParser;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.util.PageSystem;
import it.mikeslab.commons.api.inventory.util.action.ActionHandler;
import it.mikeslab.commons.api.logger.LoggerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class GuiFactoryImpl implements GuiFactory {

    @Getter private final Map<Integer, CustomGui> cachedGuis;

    private final JavaPlugin instance;

    private int idCounter = 0;

    @Setter @Getter
    private ActionHandler actionHandler;

    @Getter @Setter
    private ConditionParser conditionParser;

    public GuiFactoryImpl(final JavaPlugin instance) {
        this.cachedGuis = new HashMap<>();
        this.instance = instance;
    }

    @Override
    public int create(GuiDetails guiDetails) {

        int id = getAndIncrementId();
        CustomGui customGui = new CustomGui(
                this,
                instance,
                id
        );

        customGui.setGuiDetails(guiDetails);

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

        // Inject page system consumers
        GuiDetails guiDetails = injectPageSystemConsumers(customGui);
        //guiDetails.setTempPageElements(new HashMap<>());

        customGui.setGuiDetails(guiDetails);

        player.openInventory(customGui.getInventory());

        // Update default page systems
        // to allow them to appear in the inventory
        customGui.getPageSystemMap().values().forEach(pageSystem -> {

            instance
                    .getServer()
                    .getScheduler()
                    .runTask(
                            instance,
                            pageSystem::updateInventory // ignore with -1

                    );

        });


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

        CustomGui customGui = cachedGuis.get(id);

        customGui.setGuiDetails(newGuiDetails);

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

    /**
     * Inject the page system consumers
     * to go to the next and previous page
     * @param customGui The custom gui
     */
    private GuiDetails injectPageSystemConsumers(CustomGui customGui) {

        boolean hasPageSystems = !customGui.getPageSystemMap().isEmpty();

        if(!hasPageSystems) return customGui.getGuiDetails(); // default if no pages

        Map<String, Consumer<GuiInteractEvent>> result = new HashMap<>();

        customGui.getPageSystemMap().forEach(
                (character, pageSystem) -> {

                    String nextActionIdentifier = "NEXT_PAGE_" + Character.toUpperCase(character);
                    String previousActionIdentifier = "PREVIOUS_PAGE_" + Character.toUpperCase(character);

                    result.put(
                            nextActionIdentifier,
                            event -> {
                                pageSystem.nextPage();
                            }
                    );

                    result.put(
                            previousActionIdentifier,
                            event -> {
                                pageSystem.previousPage();
                            }
                    );
                }
        );

        customGui.getGuiDetails()
                .getClickActions()
                .putAll(result);

        return customGui.getGuiDetails();
    }

}
