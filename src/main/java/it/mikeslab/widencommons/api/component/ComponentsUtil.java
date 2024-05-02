package it.mikeslab.widencommons.api.component;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ComponentsUtil {

    public Component getComponent(ConfigurationSection section, String key) {
        return getComponent(
                section.getString(key)
        );
    }

    public List<Component> getComponentList(ConfigurationSection section, String key) {
        return getComponentList(
                section.getStringList(key)
        );
    }

    public Component getComponent(String key, TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(key, placeholders);
    }

    public List<Component> getComponentList(List<String> keys) {
        return keys.stream()
                .map(MiniMessage.miniMessage()::deserialize)
                .collect(Collectors.toList());
    }

}
