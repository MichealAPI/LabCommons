package it.mikeslab.commons.api.inventory.pojo;

import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


/**
 * This class is used to store the context of a row population
 */
@Getter
@RequiredArgsConstructor
public class InventoryPopulationContext {

    private final Multimap<Character, GuiElement> elements;
    private final Inventory inventory;

}
