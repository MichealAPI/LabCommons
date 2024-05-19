package it.mikeslab.widencommons.api.inventory.util;

import it.mikeslab.widencommons.api.inventory.CustomGui;
import it.mikeslab.widencommons.api.inventory.GuiFactory;
import it.mikeslab.widencommons.api.inventory.pojo.GuiElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;

@Getter
@AllArgsConstructor
public class PageSystem {

    private GuiFactory guiFactory;
    private int id;
    private int page; // Start at page 1
    private String internalValue;
    private List<GuiElement> elements;

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

        this.elementsPerPage = customGui
                .getInternalValuesSlots()
                .getOrDefault(internalValue, new HashSet<>())
                .size();

        return this.elementsPerPage;
    }


    /**
     * Calculate the sublist of elements that should be displayed on the current page.
     */
    private List<GuiElement> calculateSubList() {
        // Calculate the starting index for the sublist. This is done by subtracting 1 from the current page number
        // (since pages are 1-indexed but list indices are 0-indexed) and multiplying by the number of elements per page.
        // This gives the index of the first element on the current page.
        int start = (page - 1) * elementsPerPage;

        // Calculate the ending index for the sublist. This is done by multiplying the current page number by the number
        // of elements per page. However, this might exceed the size of the elements list, so we take the minimum of this
        // value and the size of the elements list. This gives the index of the first element on the next page, or the end
        // of the list if there is no next page.
        int end = Math.min(page * elementsPerPage, elements.size());

        // Create a sublist of the elements list that includes only the elements on the current page. This is done by
        // creating a stream from the elements list, skipping the elements before the start index, and limiting the stream
        // to the number of elements from the start index to the end index. The resulting stream is then collected into a
        // new list, which replaces the old elements list.
        return elements.stream().skip(start).limit(end - start).toList();
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
                viewer -> viewer.openInventory(customGui.getInventory())
        );




    }

}
