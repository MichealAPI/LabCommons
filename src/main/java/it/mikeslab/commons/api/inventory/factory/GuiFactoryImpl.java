package it.mikeslab.commons.api.inventory.factory;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.CustomInventory;
import it.mikeslab.commons.api.inventory.config.ConditionParser;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.helper.InventoryMap;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.util.action.ActionHandler;
import it.mikeslab.commons.api.inventory.util.animation.AnimationUtil;
import it.mikeslab.commons.api.logger.LogUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class GuiFactoryImpl implements GuiFactory {

    @Getter private final Map<Integer, CustomGui> cachedGuis;

    private final JavaPlugin instance;

    private int idCounter = 0;

    @Setter @Getter
    private ActionHandler actionHandler;

    @Getter @Setter
    private ConditionParser conditionParser;

    @Getter @Setter
    private InventoryMap inventoryMap;

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
            LogUtils.warn(
                    LogUtils.LogSource.API,
                    "(OPEN) Gui with id " + id + " not found"
            );
            return;
        }

        CustomGui customGui = cachedGuis.get(id);

        UUID ownerUUID = player.getUniqueId();

        customGui.setOwnerUUID(ownerUUID);

        if(customGui.getInventory() == null)
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
                            () -> pageSystem.updateInventory(player)

                    );

        });

        // If it has an animation saved, start it
        if(customGui.isAnimated()) {
            int taskId = AnimationUtil.getAnimationRunnable(customGui).runTaskTimer(
                    instance,
                    0,
                    customGui.getGuiDetails().getAnimationInterval()
            ).getTaskId();

            customGui.setAnimationTaskId(taskId);
        }

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
    public void destroyAll() {
        this.cachedGuis.clear();
    }

    @Override
    public void update(int id, @NotNull GuiDetails newGuiDetails) {

        if(!cachedGuis.containsKey(id)) {
            LogUtils.warn(
                    LogUtils.LogSource.API,
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



    @Override
    public CustomInventory getCustomInventory(UUID referencePlayerUUID, Inventory inventory) {

        if(inventoryMap == null) return null;

        if(!inventoryMap.containsKey(referencePlayerUUID)) return null;

        Map<String, CustomInventory> customInventoryMap = inventoryMap.get(referencePlayerUUID);

        for(CustomInventory customInventory : customInventoryMap.values()) {

            if(customInventory == null || customInventory.getInventory() == null) continue;

            if(customInventory.getInventory().equals(inventory)) {
                return customInventory;
            }
        }

        return null;
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
                            event -> pageSystem.nextPage(event.getWhoClicked())
                    );

                    result.put(
                            previousActionIdentifier,
                            event -> pageSystem.previousPage(event.getWhoClicked())
                    );
                }
        );

        customGui.getGuiDetails()
                .getClickActions()
                .putAll(result);

        return customGui.getGuiDetails();
    }


}
