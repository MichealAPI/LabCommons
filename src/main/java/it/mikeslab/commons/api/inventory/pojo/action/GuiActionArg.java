package it.mikeslab.commons.api.inventory.pojo.action;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@Getter
@Builder
public class GuiActionArg {

    private Player targetPlayer;

    private ConsoleCommandSender console;

}
