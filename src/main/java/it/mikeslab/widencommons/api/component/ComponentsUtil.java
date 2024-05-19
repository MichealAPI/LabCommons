package it.mikeslab.widencommons.api.component;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ComponentsUtil {

    @Nullable
    public Component getComponent(ConfigurationSection section, String key) {

        String value = section.getString(key, null);
        if(value == null) {
            return null;
        }

        return getComponent(
                section.getString(key)
        );
    }

    @Nullable
    public List<Component> getComponentList(ConfigurationSection section, String key) {
        return getComponentList(
                section.getStringList(key)
        );
    }

    @NotNull
    public Component getComponent(String key, TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(key, placeholders);
    }

    @Nullable
    public List<Component> getComponentList(List<String> keys) {
        return keys.stream()
                .filter(s -> s != null && !s.isEmpty())
                .map(MiniMessage.miniMessage()::deserialize)
                .collect(Collectors.toList());
    }

}
