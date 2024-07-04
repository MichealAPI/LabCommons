package it.mikeslab.commons.api.inventory.helper;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.factory.GuiFactory;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.util.ConditionUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ActionHelper {

    private final GuiFactory guiFactory;
    private final Player player;
    private final CustomGui gui;
    private final GuiInteractEvent event;

    /**
     * Execute the action handler for a specific element
     *
     * @param element The element
     */
    public void executeActionHandler(GuiElement element) {

        if (element.getCondition().isPresent() && guiFactory.getConditionParser() != null) {

            String replacedCondition = ConditionUtil.replace(
                    player,
                    element.getCondition().get(),
                    gui.getGuiDetails().getInjectedConditionPlaceholders()
            );

            // Parse the condition
            boolean can = guiFactory.getConditionParser().parse(
                    event.getWhoClicked(),
                    replacedCondition
            );

            if (!can) {
                return;
            }
        }

        this.acceptActionConsumers(
                gui.getId(),
                element,
                event
        );
    }



    private void acceptActionConsumers(int guiId, GuiElement element, GuiInteractEvent event) {
        element.getActions().forEach(action -> {
            guiFactory.getActionHandler().handleAction(guiId, action, event);
        });
    }


}
