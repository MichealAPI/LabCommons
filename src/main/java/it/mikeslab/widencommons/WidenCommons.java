package it.mikeslab.widencommons;

import it.mikeslab.widencommons.api.formatter.FormatUtil;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class WidenCommons extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        FormatUtil.printStartupInfos(this, "00FF72");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }



}
