package it.mikeslab.commons.api.inventory.pojo.population;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;


/**
 * This class is used to store the context of a row population
 */
@Getter
@RequiredArgsConstructor
public class InventoryPopulationContext {

    private final Multimap<Character, GuiElement> elements;
    private final Inventory inventory;

}
