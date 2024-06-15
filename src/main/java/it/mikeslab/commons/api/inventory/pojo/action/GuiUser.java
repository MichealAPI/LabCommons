package it.mikeslab.commons.api.inventory.pojo.action;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@Builder
public class GuiUser {

    private Player targetPlayer;

    private CommandSender targetSender;

}
