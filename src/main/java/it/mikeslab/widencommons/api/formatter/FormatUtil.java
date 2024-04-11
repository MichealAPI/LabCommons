package it.mikeslab.widencommons.api.formatter;

import com.google.common.base.Strings;
import io.papermc.paper.plugin.configuration.PluginMeta;
import it.mikeslab.widencommons.api.component.ComponentsUtil;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.command.ConsoleCommandSender;
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




}
