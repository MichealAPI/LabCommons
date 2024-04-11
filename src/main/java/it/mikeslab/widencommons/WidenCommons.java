package it.mikeslab.widencommons;

import it.mikeslab.widencommons.api.database.Database;
import it.mikeslab.widencommons.api.database.config.ConfigDatabaseUtil;
import it.mikeslab.widencommons.api.database.impl.MongoDatabaseImpl;
import it.mikeslab.widencommons.api.database.pojo.ExamplePOJO;
import it.mikeslab.widencommons.api.database.pojo.URIBuilder;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class WidenCommons extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        ConfigDatabaseUtil<ExamplePOJO> dbUtil = new ConfigDatabaseUtil<>(this.getConfig().getConfigurationSection("database"));

        Database<ExamplePOJO> database = dbUtil.getDatabaseInstance();

        database.connect(ExamplePOJO.class);


        
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



}
