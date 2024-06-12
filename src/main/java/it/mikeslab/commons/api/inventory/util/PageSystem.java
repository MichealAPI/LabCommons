package it.mikeslab.commons.api.inventory.util;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.GuiFactory;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.commons.api.various.StringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PageSystem {

    private final GuiFactory guiFactory;
    private final JavaPlugin instance;
    private final int id;
    private final String internalValue;
    private final List<GuiElement> elements;

    private int page = 1; // Start at page 1

    private int elementsPerPage = -1;

    public boolean hasNext() {
        return page < getMaxPages();
    }

    public boolean hasPrevious() {
        return page > 1;
    }

    public void nextPage() {
        if (hasNext()) {
            page++;
        }
    }

    public void previousPage() {
        if (hasPrevious()) {
            page--;
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

        this.elementsPerPage = StringUtil.getOrDefaultContains(
                customGui.getInternalValuesSlots(),
                internalValue.toUpperCase()
        ).size();

        return this.elementsPerPage;
    }


    /**
     * Calculate the sublist of elements that should be displayed on the current page.
     */
    public List<GuiElement> calculateSubList() {
        if (elements == null || elements.isEmpty()) {
            return List.of();
        }

        int totalElements = elements.size();
        int start = (page - 1) * getElementsPerPage();
        int end = Math.min(page * getElementsPerPage(), totalElements);

        // Ensure the start index is within the bounds of the list
        if (start >= totalElements || start < 0) {
            return List.of();
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
    public void updateInventory() {

        // Get the custom gui with the specified id
        CustomGui customGui = guiFactory.getCustomGui(id);
        if(customGui == null) {
            return;
        }

        // Populate the internals of the custom gui with the specified internal value
        customGui.populateInternals(
                internalValue,
                calculateSubList()
        );

        // Update the inventory for all viewers
        customGui.getInventory().getViewers().forEach(
                viewer -> {
                    Bukkit.getScheduler().runTaskLater(instance, () -> {
                        viewer.openInventory(customGui.getInventory());
                    }, 1L);
                }
        );

    }



}
