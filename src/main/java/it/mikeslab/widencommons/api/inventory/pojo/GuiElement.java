package it.mikeslab.widencommons.api.inventory.pojo;

import it.mikeslab.widencommons.api.inventory.util.ItemCreator;
import lombok.Builder;
import lombok.Data;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
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

}
