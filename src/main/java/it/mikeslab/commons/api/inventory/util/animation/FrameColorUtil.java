package it.mikeslab.commons.api.inventory.util.animation;

import com.cryptomorin.xseries.XMaterial;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.various.item.SkullUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.intellij.lang.annotations.Language;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class FrameColorUtil {

    // Regular expression pattern for the animate tag
    @Language("RegExp")
    public static final String TAG_REGEX = "<animate:(.*?):(.*?)>";

    public static final int MAX_FRAMES = 40;

    public boolean isAnimated(String displayName, List<String> lore) {

        Pattern pattern = Pattern.compile(TAG_REGEX);

        return pattern.matcher(displayName).find() || lore.stream().anyMatch(pattern.asPredicate());
    }

    /**
     * Generates an array of ItemStacks with different frame colors (phases) based on the given GuiElement.
     *
     * @param guiElement the GuiElement to base the frame colors on
     * @return an array of ItemStacks with different gradient color phases
     */
    public ItemStack[] getFrameColors(GuiElement guiElement, Map<String, Supplier<String>> internalPlaceholders, Player referencePlayer) {

        ItemStack[] frameStacks = new ItemStack[MAX_FRAMES];

        GuiElement defaultElementClone = guiElement.clone();
        ItemStack defaultItem;

        Optional<SkullMeta> headValueSkullMeta = Optional.empty();

        // Due to the slowness of response by Mojang API, we need to check if the headValue is a base64 string
        // and if it is, avoid animating to prevent too many requests to the Mojang servers
        if(guiElement.getHeadValue() != null) {

            headValueSkullMeta = Optional.ofNullable(
                    (SkullMeta) SkullUtil.getHeadMeta(guiElement.getHeadValue())
            );

            defaultItem = XMaterial.PLAYER_HEAD.parseItem();

        } else {
            defaultItem = defaultElementClone.create();
        }

        GuiElement guiElementClone = guiElement.parsePlaceholders(
                internalPlaceholders,
                referencePlayer
        );

        ItemStack defaultItemClone;
        ItemMeta defaultItemCloneMeta;

        // Iterate over the phase range
        for (int i = 0; i < MAX_FRAMES; i++) {
            double phase = -i * 0.05 + 1; // range from -1 to 1

            defaultItemClone = defaultItem.clone(); // Default item is a plain PLAYER_HEAD

            if(headValueSkullMeta.isPresent()) {
                defaultItemClone.setItemMeta(headValueSkullMeta.get());
            }

            defaultItemCloneMeta = defaultItemClone.getItemMeta();

            String displayName = guiElementClone.getDisplayName();
            List<String> lore = guiElementClone.getLore();

            // Replace the animate tag in the displayName and lore
            displayName = replaceTag(displayName, phase);
            lore = lore.stream()
                    .map(line -> replaceTag(line, phase))
                    .collect(Collectors.toList());

            // Clone the GuiElement and set the new displayName and lore

            // TODO TEMPORARY DISABLED TO TEST 1.8
            // defaultItemCloneMeta.setCustomModelData(guiElementClone.getCustomModelData());
            defaultItemCloneMeta.setDisplayName(ComponentsUtil.getSerializedComponent(displayName));
            defaultItemCloneMeta.setLore(ComponentsUtil.getSerializedComponents(lore));

            defaultItemClone.setItemMeta(defaultItemCloneMeta);

            // Add the modified GuiElement to the array
            frameStacks[i] = defaultItemClone;

        }

        return frameStacks;
    }

    /**
     * Replaces the animate tag in the given line with a gradient tag.
     *
     * @param line  the line to replace the animate tag in
     * @param phase the phase to use in the gradient tag
     * @return the line with the animate tag replaced by a gradient tag
     */
    private String replaceTag(String line, double phase) {
        if (line == null) {
            return null;
        }

        // The pattern to replace the animate tag with
        String replacementPattern = "<gradient:$1:$2:" + phase + ">";

        // Replace the animate tag with the gradient tag and return the result
        return line.replaceAll(TAG_REGEX, replacementPattern)
                .replace("</animate>", "</gradient>");
    }

}
