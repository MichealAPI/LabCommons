package it.mikeslab.commons.api.inventory.util.frame;

import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.intellij.lang.annotations.Language;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class FrameColorUtil {

    // Regular expression pattern for the animate tag
    @Language("RegExp")
    public static final String TAG_REGEX = "<animate:(.*?):(.*?)>";

    public static final int MAX_FRAMES = 40;

    public boolean isAnimated(Component displayName, List<Component> lore) {

        Pattern pattern = Pattern.compile(TAG_REGEX);

        String displayNameAsString = ComponentsUtil.serialize(displayName) + " ";
        List<String> loreAsStringList = ComponentsUtil.serialize(lore);

        return pattern.matcher(displayNameAsString).find() || loreAsStringList.stream().anyMatch(pattern.asPredicate());
    }

    /**
     * Generates an array of ItemStacks with different frame colors (phases) based on the given GuiElement.
     *
     * @param guiElement the GuiElement to base the frame colors on
     * @return an array of ItemStacks with different gradient color phases
     */
    public ItemStack[] getFrameColors(GuiElement guiElement) {

        GuiElement[] guiElements = new GuiElement[MAX_FRAMES];

        // Iterate over the phase range
        for (int i = 0; i < MAX_FRAMES; i++) {
            double phase = (i * 0.05) - 1; // range from -1 to 1

            // Get and serialize the displayName and lore from the GuiElement
            String serializedDisplayName = ComponentsUtil.serialize(guiElement.getDisplayName());
            List<String> serializedLore = ComponentsUtil.serialize(guiElement.getLore());

            // Replace the animate tag in the displayName and lore
            serializedDisplayName = replaceTag(serializedDisplayName, phase);
            serializedLore = serializedLore.stream()
                    .map(line -> replaceTag(line, phase))
                    .collect(Collectors.toList());

            // Deserialize the displayName and lore
            Component displayName = ComponentsUtil.getComponent(serializedDisplayName);
            List<Component> lore = ComponentsUtil.getComponentList(serializedLore);

            // Clone the GuiElement and set the new displayName and lore
            GuiElement defaultClone = guiElement.clone();
            defaultClone.setDisplayName(displayName);
            defaultClone.setLore(lore);

            // Add the modified GuiElement to the array
            guiElements[i] = defaultClone;

        }

        // Convert the array of GuiElements to an array of ItemStacks
        return Arrays.stream(guiElements)
                .map(GuiElement::create)
                .toArray(ItemStack[]::new);
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
