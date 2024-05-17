package it.mikeslab.widencommons.api.formatter;

import com.google.common.base.Strings;
import io.papermc.paper.plugin.configuration.PluginMeta;
import it.mikeslab.widencommons.api.component.ComponentsUtil;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class FormatUtil {

    public void printStartupInfos(JavaPlugin instance, String colorCode) {

        String customColorTag = "<#" + colorCode + ">";

        PluginMeta pluginMeta = instance.getPluginMeta();

        ConsoleCommandSender console = instance.getServer().getConsoleSender();

        Component header = ComponentsUtil.getComponent("<gray>" + Strings.repeat("-", 50));


        console.sendMessage(header);

        console.sendMessage(
                ComponentsUtil.getComponent(customColorTag + "Plugin<dark_gray>: <white>" + pluginMeta.getName())
        );

        console.sendMessage(
                ComponentsUtil.getComponent(customColorTag + "Version<dark_gray>: <white>" + pluginMeta.getVersion())
        );

        console.sendMessage(
                ComponentsUtil.getComponent(customColorTag + "Authors<dark_gray>: <white>" + pluginMeta.getAuthors())
        );

        console.sendMessage(
                ComponentsUtil.getComponent(customColorTag + "Description<dark_gray>: <white>" + pluginMeta.getDescription())
        );

        console.sendMessage(
                ComponentsUtil.getComponent(customColorTag + "Website<dark_gray>: <white>" + pluginMeta.getWebsite())
        );

        console.sendMessage(header);

    }


    /**
     * Send the running infos to the player
     * @param audience The audience
     * @param instance The plugin instance
     * @param colorCode The color code
     */
    public void sendRunningInfos(Audience audience, JavaPlugin instance, String colorCode) {

        PluginMeta pluginMeta = instance.getPluginMeta();

        String authors = pluginMeta.getAuthors()
                .toString()
                .replace("[", "")
                .replace("]", "");

        audience.sendMessage(
                ComponentsUtil.getComponent(
                        "<#{colorCode}>This server is currently running <gold><plugin> v<version> <#{colorCode}>made by <gold><author>".replace("{colorCode}", colorCode),
                        Placeholder.unparsed("plugin", pluginMeta.getName()),
                        Placeholder.unparsed("version", pluginMeta.getVersion()),
                        Placeholder.unparsed("author", authors))
        );

    }




}
