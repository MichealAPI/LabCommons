package it.mikeslab.commons.api.component;

import it.mikeslab.commons.api.various.HexUtils;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

    @Nullable
    public Component getComponent(String key, TagResolver.Single... placeholders) {

        if(key == null) return null;

        return MiniMessage.miniMessage().deserialize(key, placeholders);
    }

    @Nullable
    public List<Component> getComponentList(List<String> keys, TagResolver.Single... placeholders) {

        return keys.stream()
                .filter(s -> s != null && !s.isEmpty())
                .map(s -> getComponent(s, placeholders))
                .collect(Collectors.toList());
    }

    @NotNull
    public List<String> serialize(List<Component> components) {
        return components.stream()
                .map(ComponentsUtil::serialize)
                .collect(Collectors.toList());
    }

    @Nullable
    public String serialize(Component component) {

        if(component == null) return null;

        String serialized = LegacyComponentSerializer
                .builder()
                .hexColors()
                .hexCharacter('#')
                .build()
                .serialize(component);

        if(HexUtils.HEX_PATTERN.matcher(serialized).find()) {
            serialized = HexUtils.translateHexCodes(serialized);
        }

        return serialized;
    }


    @Nullable
    public String getSerializedComponent(String val, TagResolver.Single... placeholders) {
        return serialize(getComponent(val, placeholders));
    }

    public @NotNull List<String> getSerializedComponents(List<String> val, TagResolver.Single... placeholders) {
        return serialize(getComponentList(val, placeholders));
    }


}
