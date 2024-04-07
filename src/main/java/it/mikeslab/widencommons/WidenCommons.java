package it.mikeslab.widencommons;

import it.mikeslab.widencommons.api.inventory.GuiFactory;
import it.mikeslab.widencommons.api.inventory.event.GuiListener;
import it.mikeslab.widencommons.api.inventory.factory.GuiFactoryImpl;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class WidenCommons extends JavaPlugin {

    private GuiFactory guiFactory;

    @Override
    public void onEnable() {
        // Plugin startup logic

        GuiFactoryImpl guiFactoryImpl = new GuiFactoryImpl();
        GuiListener guiListener = new GuiListener(guiFactoryImpl);

        guiFactory = guiFactoryImpl;

        this.getServer().getPluginManager().registerEvents(
                guiListener,
                this
        );

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
