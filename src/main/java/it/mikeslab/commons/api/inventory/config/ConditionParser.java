package it.mikeslab.commons.api.inventory.config;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Supplier;

public interface ConditionParser {

    /**
     * Parse the condition
     * @param condition The condition to parse
     * @param injectedValues The values to inject
     * @return If the condition is valid
     */
    boolean parse(Player player, String condition, Map<String, Supplier<String>> injectedValues);

    /**
     * Parse the condition
     * @param condition The condition to parse
     * @return If the condition is valid
     */
    boolean parse(Player player, String condition);

}
