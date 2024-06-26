package it.mikeslab.commons.api.inventory.util;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.GuiFactory;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.inventory.pojo.population.PopulatePageContext;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PageSystem {

    // Not using guiFactory to get a customGui instance may result in a
    // old instance being used, which may cause issues with the inventory
    private final GuiFactory guiFactory;
    private final JavaPlugin instance;
    private final int id;
    private final List<GuiElement> elements;

    private final Character character;

    @Setter
    private int page = 1; // Start at page 1

    private int elementsPerPage = -1;

    public PageSystem(GuiFactory guiFactory, JavaPlugin instance, int id, Character character, List<GuiElement> elements) {
        this.guiFactory = guiFactory;
        this.instance = instance;
        this.id = id;
        this.character = character;
        this.elements = elements;
    }


    public boolean hasNext() {
        return page < getMaxPages();
    }

    public boolean hasPrevious() {
        return page > 1;
    }

    public void nextPage(Player player) {
        if (hasNext()) {
            page++;  // We cannot extract this to a method because of the condition
                     // we surely want to avoid refreshing the inventory unnecessarily

            this.updateInventory(player);
        }
    }

    public void previousPage(Player player) {
        if (hasPrevious()) {
            page--;

            this.updateInventory(player); // We cannot extract this to a method because of the condition
                                    // we surely want to avoid refreshing the inventory unnecessarily
        }
    }

    public int getMaxPages() {
        return (int) Math.ceil((double) elements.size() / getElementsPerPage());
    }

    public int getElementsPerPage() {

        // Cache the elements per page value
        if(this.elementsPerPage != -1) {
            return this.elementsPerPage;
        }

        CustomGui customGui = guiFactory.getCustomGui(id);
        if(customGui == null) {
            return 0;
        }

        this.elementsPerPage = customGui
                .getCharacterListMap()
                .get(character)
                .size();

        return this.elementsPerPage;
    }


    /**
     * Calculate the sublist of elements that should be displayed on the current page.
     */
    public List<GuiElement> calculateSubList() {
        if (elements == null || elements.isEmpty()) {
            return Collections.emptyList();
        }

        int totalElements = elements.size();
        int start = (page - 1) * getElementsPerPage();
        int end = Math.min(page * getElementsPerPage(), totalElements);

        // Ensure the start index is within the bounds of the list
        if (start >= totalElements || start < 0) {
            return Collections.emptyList();
        }

        // Create the sublist using skip and limit
        return elements.stream()
                .skip(start)
                .limit(end - start)
                .collect(Collectors.toList());
    }

    /**
     * Update the inventory for the specified gui factory with the specified id and internal value.
     */
    public void updateInventory(Player player) {

        // Get the custom gui with the specified id
        CustomGui customGui = guiFactory.getCustomGui(id);
        if(customGui == null) {
            return;
        }

        // Populate the internals of the custom gui with the specified internal value
        customGui.populatePage(
                new PopulatePageContext(
                        player.getOpenInventory().getTopInventory(),
                        character,
                        calculateSubList(),
                        player
                )
        );

        player.updateInventory();

    }



}
