package it.mikeslab.widencommons;

import it.mikeslab.widencommons.api.inventory.GuiFactory;
import it.mikeslab.widencommons.api.inventory.config.GuiConfig;
import it.mikeslab.widencommons.api.inventory.config.GuiConfigImpl;
import it.mikeslab.widencommons.api.inventory.event.GuiListener;
import it.mikeslab.widencommons.api.inventory.factory.GuiFactoryImpl;
import it.mikeslab.widencommons.api.inventory.pojo.GuiDetails;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
public final class WidenCommons extends JavaPlugin implements Listener {

    private GuiFactory guiFactory;

    int id;

    @Override
    public void onEnable() {
        // Plugin startup logic

        // todo testing purposes, remove.
        saveDefaultConfig();

        GuiFactoryImpl guiFactoryImpl = new GuiFactoryImpl();
        GuiListener guiListener = new GuiListener(guiFactoryImpl);

        guiFactory = guiFactoryImpl;

        this.getServer().getPluginManager().registerEvents(
                guiListener,
                this
        );


        GuiConfig guiConfig = new GuiConfigImpl(this);

        guiConfig.loadConfig("config.yml");

        Map<String, Consumer<InventoryClickEvent>> actionMap = Map.of(
                "TEST", event -> {
                    event.getWhoClicked().sendMessage("Test");
                }
        );

        GuiDetails guiDetails = guiConfig.getGuiDetails(Optional.empty(), Optional.of(actionMap));

        id = guiFactory.create(guiDetails);

        this.getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        this.getServer().getScheduler().runTaskLater(
                this,
                () -> guiFactory.open(event.getPlayer(), id),
                20L
        );

    }


}
