package it.mikeslab.commons.api.various;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class StringUtil {

    /**
     * Get the list of integers from the map that contains the specified key.
     * @param map The map
     * @param key The key
     * @return The list of integers
     */
    public List<Integer> getOrDefaultContains(Map<String, List<Integer>> map, String key) {
        return map.entrySet().stream()
                .filter(e -> e.getKey().contains(key))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(new ArrayList<>());
    }

}
