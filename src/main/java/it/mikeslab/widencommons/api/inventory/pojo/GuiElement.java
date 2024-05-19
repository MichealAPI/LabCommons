package it.mikeslab.widencommons.api.inventory.pojo;

import it.mikeslab.widencommons.api.inventory.util.ItemCreator;
import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Data
@Builder
public class GuiElement {

    private Material material;

    // Optionals
    private Component displayName;
    private List<Component> lore;
    private Integer amount;
    private Boolean glow;
    private String internalValue; // If that element should be built in a specific way by the plugin, internally

    private int customModelData = -1;

    // Consumers
    private Consumer<InventoryClickEvent> onClick;

    /**
     * Quick method to create the itemStack
     * @return The itemStack
     */
    public ItemStack create() {

        if(amount == null) amount = 1;

        return new ItemCreator().create(this);
    }


    /**
     * Quick method to create the itemStack
     * with Placeholders support
     * @return The itemStack
     */
    public ItemStack create(Map<String, String> placeholders) {

        if (amount == null) amount = 1;

        if (displayName != null) {
            replaceDisplayName(placeholders);
        }

        if(lore != null) {
            this.replaceLore(placeholders);
        }

        return new ItemCreator().create(this);
    }

    private void replaceDisplayName(Map<String, String> placeholders) {
        for(Map.Entry<String, String> entry : placeholders.entrySet()) {
            displayName = replace(displayName, entry.getKey(), entry.getValue());
        }
    }

    private void replaceLore(Map<String, String> placeholders) {
        for(int i = 0; i < lore.size(); i++) {
            Component component = lore.get(i);

            for(Map.Entry<String, String> entry : placeholders.entrySet()) {
                component = replace(component, entry.getKey(), entry.getValue());
            }

            lore.set(i, component);

        }
    }

    private Component replace(Component component, String firstValue, String secondValue) {
        return component.replaceText(
                this.generateReplacement(
                        firstValue,
                        secondValue
                )
        );
    }

    private TextReplacementConfig generateReplacement(String firstValue, String secondValue) {
        return TextReplacementConfig.builder()
                .matchLiteral(firstValue)
                .replacement(secondValue)
                .build();
    }

    public GuiElement clone() {
        return GuiElement.builder()
                .material(material)
                .displayName(displayName)
                .lore(lore != null ? new ArrayList<>(lore) : null)
                .amount(amount)
                .glow(glow)
                .customModelData(customModelData)
                .onClick(onClick)
                .build();
    }


}
