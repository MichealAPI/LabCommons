package it.mikeslab.commons.api.inventory.pojo;

import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.util.ItemCreator;
import it.mikeslab.commons.api.inventory.util.frame.FrameColorUtil;
import lombok.Builder;
import lombok.Data;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@Builder
public class GuiElement {

    // Regular expression pattern for the placeholders
    private final static Pattern PLACEHOLDER_REGEX_PATTERN = Pattern.compile("(%.*?%)");

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
    public boolean containsPlaceholders() { // if the reference is not null, placeholders will be loaded if they're not
        if (hasPlaceholders != null) {
            return hasPlaceholders;
        }

        hasPlaceholders = PLACEHOLDER_REGEX_PATTERN.matcher(displayName).find() ||
                lore.stream().anyMatch(PLACEHOLDER_REGEX_PATTERN.asPredicate());


        return hasPlaceholders;
    }

    public boolean havePlaceholdersChanged(Player papiReference) {

        if(!LabCommons.PLACEHOLDER_API_ENABLED) return false;

        if(placeholders == null) return false; // PLACEHOLDERS ARE NOT LOADED!!!

        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String newValue = PlaceholderAPI.setPlaceholders(papiReference, key);

            System.out.println("Key: " + key + " Value: " + value + " New Value: " + newValue);

//            if(!value.equals(newValue)) {
//                placeholders.put(key, newValue);
//                return true;
//            }

            return true;
        }

        return false;
    }

    public boolean containsConditionPlaceholders(GuiDetails guiDetails) {

        Set<String> placeholders = guiDetails.getInjectedConditionPlaceholders().keySet();

        for(String placeholder : placeholders) {

            if(displayName != null && displayName.contains(placeholder)) {
                return true;
            }

            if(lore != null) {
                for(String line : lore) {
                    if(line.contains(placeholder)) {
                        return true;
                    }
                }
            }

        }

        return false;
    }

    private void loadPlaceholders(Player papiReference) {

        if(!LabCommons.PLACEHOLDER_API_ENABLED) return;

        if(this.placeholders != null) return; // Placeholders are already loaded

        this.placeholders = new HashMap<>();

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

        Matcher matcher = PLACEHOLDER_REGEX_PATTERN.matcher(line);
        int lastMatchPos = 0;

        String key, value;

        while(matcher.find()) {
            key = matcher.group(1);
            value = PlaceholderAPI.setPlaceholders(papiReference, key);

            this.placeholders.put(key, value);

            // todo restore this debugger
//            lastMatchPos = matcher.end();
        }

//        if(lastMatchPos != line.length()) {
//            LoggerUtil.log(
//                    Level.WARNING,
//                    LoggerUtil.LogSource.PLUGIN,
//                    "Invalid string '" + line + "' for regex pattern '(%.*?%)'"
//            );
//        }
    }

    public ItemStack create(Map<String, String> placeholders, Player papiReference) {

        GuiElement clone = this.parsePlaceholders(placeholders, papiReference); // HERE AND


        // Early exit if replacements are provided, avoiding unnecessary processing
        //if (replacements != null) {
        //    clone = clone.executeReplacements(replacements); // TODO WARNING! DOUBLE CLONING | HERE
        // }

        System.out.println(this.getDisplayName() + " MY FEVORITE DISAPLY NAMRE");

        // Use ItemCreator to generate and return the final ItemStack
        return new ItemCreator().create(clone);
    }

    /**
     * Quick method to create the itemStack
     * @return The itemStack
     */
    public ItemStack create() {

        GuiElement clone = this;

        // If there are replacements, we need to create the itemStack with placeholder support
        if(replacements != null) {
            clone = this.executeReplacements(replacements);
        }

        return new ItemCreator().create(clone);
    }


    /**
     * Quick method to create the itemStack
     * with Placeholders support
     * @return The itemStack
     */
    public GuiElement executeReplacements(Map<String, String> placeholders) {

        GuiElement clone = this.clone();

        if (clone.amount == null) clone.amount = 1;

        if(clone.replacements != null) {
            placeholders.putAll(clone.replacements);
        }

        if (clone.displayName != null) {
            clone.displayName = this.replace(clone.displayName, placeholders);
        }

        if(clone.lore != null) {
            clone.lore = clone.replaceMany(clone.lore, placeholders);
        }

        return clone;

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


    public GuiElement parsePlaceholders(Map<String, String> internalPlaceholders, Player player) {

        GuiElement element = this.clone(); // avoid removing the placeholders from the original element

        this.loadPlaceholders(player);

        if (element.displayName != null) {
            element.displayName = replace(element.displayName, internalPlaceholders);
        }

        if (element.lore != null) {
            element.lore = replaceMany(element.lore, internalPlaceholders);
        }

        if (LabCommons.PLACEHOLDER_API_ENABLED) {
            if (element.displayName != null) {
                element.displayName = PlaceholderAPI.setPlaceholders(player, element.displayName);
            }
            if (element.lore != null) {
                element.lore = element.lore.stream().map(line -> PlaceholderAPI.setPlaceholders(player, line)).collect(Collectors.toList());
            }
        }

        return element;
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
