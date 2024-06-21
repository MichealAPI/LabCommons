package it.mikeslab.commons.api.inventory.util;

import it.mikeslab.commons.LabCommons;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Supplier;

@UtilityClass
public class ConditionUtil {

    public String replace(Player reference, String condition, Map<String, Supplier<String>> injectedReplacements) {

        if(condition == null) return null;

        for(Map.Entry<String, Supplier<String>> entry : injectedReplacements.entrySet()) {
            condition = condition.replace(
                    entry.getKey(),
                    entry.getValue().get()
            );
        }

        if(LabCommons.PLACEHOLDER_API_ENABLED) {
            condition = PlaceholderAPI.setPlaceholders(reference, condition)
                    .replace(" ", "");
        }

        return condition;

    }


}
