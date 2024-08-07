package it.mikeslab.commons.api.inventory.util.animation;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.pojo.Animation;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Optional;

@UtilityClass
public class AnimationUtil {

    /**
     * Generates the animation runnable for the given
     * animated customGui.
     * Note that a pre-check for gui animations is needed
     * to avoid performance issues
     * @param targetGui The targetGui
     * @return The animation runnable
     */
    public BukkitRunnable getAnimationRunnable(CustomGui targetGui) {

        return new BukkitRunnable() {
            int frame = 0;
            final Player player = Bukkit.getPlayer( // Having the player cached here is not a problem,
                    targetGui.getOwnerUUID()        // since the task is canceled when the player closes the inventory
            );

            @Override
            public void run() {

                Map<Character, Animation> animatedElements = targetGui.getAnimatedElements();

                for (Map.Entry<Character, Animation> animatedElement : animatedElements.entrySet()) {

                    ItemStack item = animatedElement
                            .getValue()
                            .getGuiElement()
                            .getFrames()
                            .get()[frame];

                    targetGui.populateSlots(
                            animatedElement.getKey(),
                            animatedElement.getValue().getSlots(),
                            item,
                            true
                    );

                    // If the player disconnects or whatever, stops the task
                    if (player == null) {
                        this.cancel();
                        return;
                    }

                    player.updateInventory();
                }

                frame++;

                if (frame >= FrameColorUtil.MAX_FRAMES) {
                    frame = 0;
                }
            }
        };
    }

    /**
     * Post process a guiElement which needs an animation by
     * requesting the creation of its missing frames
     * @param guiDetails The customGui details
     * @param guiElement The target element
     * @param player The referencePlayer for Placeholders by PlaceholderAPI
     */
    public void postProcessElement(GuiDetails guiDetails, GuiElement guiElement, Player player) {
        Optional<ItemStack[]> frames = Optional.empty();

        boolean hasChangedPlaceholders = guiElement.containsPlaceholders() && guiElement.havePlaceholdersChanged(player);
        boolean containsConditionPlaceholders = guiElement.containsConditionPlaceholders(guiDetails);

        if(guiElement.getFrames().isPresent() && !hasChangedPlaceholders) return;
        if(containsConditionPlaceholders) return;

        if (guiElement.isAnimated()) {
            frames = Optional.of(FrameColorUtil.getFrameColors(
                    guiElement,
                    guiDetails.getPlaceholders(),
                    player)
            );
        }

        guiElement.setFrames(frames);
    }


}
