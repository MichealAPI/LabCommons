package it.mikeslab.commons.api.inventory.config;

import java.util.Map;

public interface ConditionParser {

    /**
     * Parse the condition
     * @param condition The condition to parse
     * @param injectedValues The values to inject
     * @return If the condition is valid
     */
    boolean parse(String condition, Map<String, String> injectedValues);

    /**
     * Parse the condition
     * @param condition The condition to parse
     * @return If the condition is valid
     */
    boolean parse(String condition);

}
