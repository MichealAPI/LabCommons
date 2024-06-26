package it.mikeslab.commons.api.inventory.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class MappingUtil {


    /**
     * Map the characters to slots
     *
     * @param layout The layout
     */
    public void mapCharToSlot(String[] layout, Map<Character, List<Integer>> characterIntegerMap) {

        // If the map is not empty, return early
        if (!characterIntegerMap.isEmpty()) {
            return;
        }

        for (int i = 0; i < layout.length; i++) {
            mapRowToSlots(
                    layout[i],
                    i,
                    characterIntegerMap
            );
        }
    }

    /**
     * Map a row of chars to slots
     *
     * @param row      The row
     * @param rowIndex The row index
     */
    private void mapRowToSlots(String row, int rowIndex, Map<Character, List<Integer>> characterIntegerMap) {
        for (int j = 0; j < row.length(); j++) {
            char c = row.charAt(j);
            if (c != ' ') {

                int slot = rowIndex * row.length() + j;

                addCharToMap(
                        c,
                        slot,
                        characterIntegerMap
                );
            }
        }
    }


    /**
     * Add a character to the map
     *
     * @param c    The character
     * @param slot The slot
     */
    private void addCharToMap(char c, int slot, Map<Character, List<Integer>> characterIntegerMap) {
        if (!characterIntegerMap.containsKey(c)) {
            characterIntegerMap.put(c, new ArrayList<>());
        }
        characterIntegerMap.get(c).add(slot);
    }


}
