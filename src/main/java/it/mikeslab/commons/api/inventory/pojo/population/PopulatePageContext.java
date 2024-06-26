package it.mikeslab.commons.api.inventory.pojo.population;

import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

@Data
@AllArgsConstructor
public class PopulatePageContext {

    private Inventory targetInventory;
    private char targetChar;
    private List<GuiElement> subList;
    private Player player;

}