package it.mikeslab.commons.api.component;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
    public List<Component> getComponentList(ConfigurationSection section, String key, TagResolver.Single... placeholders) {
        return getComponentList(section.getStringList(key), placeholders);
    }

    @NotNull
    public Component getComponent(String key, TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(key, placeholders);
    }

    @Nullable
    public List<Component> getComponentList(List<String> keys, TagResolver.Single... placeholders) {

        return keys.stream()
                .filter(s -> s != null && !s.isEmpty())
                .map(s -> MiniMessage.miniMessage().deserialize(s, placeholders))
                .collect(Collectors.toList());
    }

    @NotNull
    public List<String> serialize(List<Component> components) {
        return components.stream()
                .map(MiniMessage.miniMessage()::serialize)
                .toList();
    }

    @NotNull
    public String serialize(Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

}
