package it.mikeslab.commons.api.inventory.pojo;

import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.util.ItemCreator;
import it.mikeslab.commons.api.inventory.util.frame.FrameColorUtil;
import lombok.Builder;
import lombok.Data;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@Builder
public class GuiElement {

    private final static Pattern PLACEHOLDER_REGEX_PATTERN = Pattern.compile("%[^%]+%");

    private Material material;

    // Optionals
    private String displayName;
    private List<String> lore;
    private Integer amount;
    private Boolean glow;
    private String internalValue; // If the plugin should build that element in a specific way, internally

    private Map<String, String> replacements;

    private List<String> actions;

    private int customModelData;

    private boolean isGroupElement;
    // private int order;

    private Optional<String> condition;

    // Consumers
    private Consumer<GuiInteractEvent> onClick;

    private String headValue; // If the element is a player head, this is the value to be used

    // Animation
    private Optional<ItemStack[]> frames; // if present, the element will be animated

    private Boolean hasPlaceholders;

    private Map<String, String> placeholders; // This is a cache for PlaceholderAPI.
                                              // No need to create new items if the placeholder values are the same

    private Boolean isAnimated;

    /**
     * Checks if the element is animated
     * @return true if the element is animated
     */
    public boolean isAnimated() {

        if(isAnimated != null) {
            return isAnimated;
        }

        isAnimated = FrameColorUtil.isAnimated(
                displayName,
                lore
        );

        return isAnimated;
    }

    /**
     * Checks if the displayName or lore contains placeholders
     * @return true if the displayName or lore contains placeholders
     */
    public boolean containsPlaceholders() {
        if (hasPlaceholders != null) {
            return hasPlaceholders;
        }

        hasPlaceholders = PLACEHOLDER_REGEX_PATTERN.matcher(displayName).find() ||
                lore.stream().anyMatch(PLACEHOLDER_REGEX_PATTERN.asPredicate());

        return hasPlaceholders;
    }

    public boolean havePlaceholdersChanged(Player papiReference) {

        if(!LabCommons.PLACEHOLDER_API_ENABLED) return false;

        if(placeholders == null) return false;

        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String newValue = PlaceholderAPI.setPlaceholders(papiReference, key);

            if(!value.equals(newValue)) {
                placeholders.put(key, newValue);
                return true;
            }
        }

        return false;
    }

    private void loadPlaceholders(Player papiReference) {

        if(!LabCommons.PLACEHOLDER_API_ENABLED) return;

        if(this.displayName != null) {
            this.loadPlaceholder(papiReference, this.displayName);
        }

        if(this.lore != null) {
            for(String line : this.lore) {
                this.loadPlaceholder(papiReference, line);
            }
        }

    }

    private void loadPlaceholder(Player papiReference, String line) {
        int groupSize = PLACEHOLDER_REGEX_PATTERN.matcher(line).groupCount();

        if(groupSize == 0) return; // No placeholders found in the line

        String key, value;

        for(int i = 0; i < groupSize; i++) {
            key = PLACEHOLDER_REGEX_PATTERN.matcher(line).group(i);
            value = PlaceholderAPI.setPlaceholders(papiReference, key);

            this.placeholders.put(key, value);
        }
    }

    public ItemStack create(Map<String, String> placeholders, Player papiReference) {

        // Early exit if replacements are provided, avoiding unnecessary processing
        if (replacements != null) {
            return this.create(replacements);
        }

        this.parsePlaceholders(placeholders, papiReference);

        // Use ItemCreator to generate and return the final ItemStack
        return new ItemCreator().create(this);
    }

    /**
     * Quick method to create the itemStack
     * @return The itemStack
     */
    public ItemStack create() {

        if(amount == null) amount = 1;

        // If there are replacements, we need to create the itemStack with placeholder support
        if(replacements != null) {
            return this.create(replacements);
        }

        return new ItemCreator().create(this);
    }


    /**
     * Quick method to create the itemStack
     * with Placeholders support
     * @return The itemStack
     */
    public ItemStack create(Map<String, String> placeholders) {

        if (amount == null) amount = 1;

        if(this.replacements != null) {
            placeholders.putAll(this.replacements);
        }

        if (displayName != null) {
            displayName = this.replace(displayName, placeholders);
        }

        if(lore != null) {
            lore = this.replaceMany(lore, placeholders);
        }

        return new ItemCreator().create(this);
    }

    private String replace(String text, Map<String, String> placeholders) {

        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        return text;
    }

    private List<String> replaceMany(List<String> lore, Map<String, String> placeholders) {

        List<String> replacedLore = new ArrayList<>();

        for(String line : lore) {
            replacedLore.add(replace(line, placeholders));
        }

        return replacedLore;
    }


    public void parsePlaceholders(Map<String, String> internalPlaceholders, Player player) {

        if (displayName != null) {
            displayName = replace(displayName, internalPlaceholders);
        }

        if (lore != null) {
            lore = replaceMany(lore, internalPlaceholders);
        }

        if (LabCommons.PLACEHOLDER_API_ENABLED) {
            if (displayName != null) {
                displayName = PlaceholderAPI.setPlaceholders(player, displayName);
            }
            if (lore != null) {
                lore = lore.stream().map(line -> PlaceholderAPI.setPlaceholders(player, line)).collect(Collectors.toList());
            }
        }
    }


    public GuiElement clone() {
        return GuiElement.builder()
                .material(material)
                .displayName(displayName)
                .lore(lore != null ? new ArrayList<>(lore) : null)
                .amount(amount)
                .glow(glow)
                .actions(actions != null ? new ArrayList<>(actions) : null)
                .customModelData(customModelData)
                .internalValue(internalValue)
                .onClick(onClick)
                .condition(condition)
                .headValue(headValue)
                .isGroupElement(isGroupElement)
                .frames(frames)
                .build();
    }


}
