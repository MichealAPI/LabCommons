package it.mikeslab.commons.api.inventory.pojo;

import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.util.ItemCreator;
import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Data
@Builder
public class GuiElement {

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

    // Animation
    private Optional<ItemStack[]> frames; // if present, the element will be animated


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
                .isGroupElement(isGroupElement)
                .frames(frames)
                .build();
    }


}
