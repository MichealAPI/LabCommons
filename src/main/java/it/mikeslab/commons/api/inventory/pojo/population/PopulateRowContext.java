package it.mikeslab.commons.api.inventory.pojo.population;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
@AllArgsConstructor
public class PopulateRowContext {
    private char targetChar;
    private List<Integer> slots;
    private ItemStack item;
    private int slotCounter;
    private String row;

    private boolean metaReplaceOnly;
}
