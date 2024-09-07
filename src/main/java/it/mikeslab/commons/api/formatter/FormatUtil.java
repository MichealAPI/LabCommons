package it.mikeslab.commons.api.formatter;

import com.google.common.base.Strings;
import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.component.ComponentsUtil;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class FormatUtil {

    public void printStartupInfos(JavaPlugin instance, String colorCode) {

        BukkitAudiences audiences = LabCommons
                .getInstance()
                .getAudiences();

        String customColorTag = "<#" + colorCode + ">";

        PluginDescriptionFile pluginMeta = instance.getDescription();

        Component header = ComponentsUtil.getComponent("<gray>" + Strings.repeat("-", 50));

        audiences.console().sendMessage(header);

        audiences.console().sendMessage(
                ComponentsUtil.getComponent(customColorTag + "Plugin<dark_gray>: <white>" + pluginMeta.getName())
        );

        audiences.console().sendMessage(
                ComponentsUtil.getComponent(customColorTag + "Version<dark_gray>: <white>" + pluginMeta.getVersion())
        );

        audiences.console().sendMessage(
                ComponentsUtil.getComponent(customColorTag + "Authors<dark_gray>: <white>" + pluginMeta.getAuthors())
        );

        audiences.console().sendMessage(
                ComponentsUtil.getComponent(customColorTag + "Description<dark_gray>: <white>" + pluginMeta.getDescription())
        );

        audiences.console().sendMessage(
                ComponentsUtil.getComponent(customColorTag + "Website<dark_gray>: <white>" + pluginMeta.getWebsite())
        );

        audiences.console().sendMessage(header);

    }


    /**
     * Send the running infos to the player
     * @param instance The plugin instance
     * @param colorCode The color code
     */
    public void sendRunningInfos(CommandSender sender, JavaPlugin instance, String colorCode) {

        BukkitAudiences audiences = LabCommons
                .getInstance()
                .getAudiences();

        PluginDescriptionFile pluginMeta = instance.getDescription();

        String authors = pluginMeta.getAuthors()
                .toString()
                .replace("[", "")
                .replace("]", "");

        audiences.sender(sender).sendMessage(
                ComponentsUtil.getComponent(
                        "<#{colorCode}>This server is currently running <gold><plugin> v<version> <#{colorCode}>made by <gold><author>".replace("{colorCode}", colorCode),
                        Placeholder.unparsed("plugin", pluginMeta.getName()),
                        Placeholder.unparsed("version", pluginMeta.getVersion()),
                        Placeholder.unparsed("author", authors))
        );

    }




}
