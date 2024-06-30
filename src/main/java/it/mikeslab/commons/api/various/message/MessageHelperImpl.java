package it.mikeslab.commons.api.various.message;

import it.mikeslab.commons.api.config.ConfigurableEnum;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.swing.*;

public class MessageHelperImpl extends MessageHelper {

    public MessageHelperImpl(BukkitAudiences audiences) {
        super(audiences);
    }

    @Override
    public void sendMessage(CommandSender target, ConfigurableEnum key, TagResolver.Single... tagResolvers) {
        this.execute(key, (impl) -> {
            audiences.sender(target).sendMessage(impl.getComponent(key, tagResolvers));
        });
    }

    @Override
    public void sendMessage(CommandSender target, ConfigurableEnum key) {
        this.sendMessage(target, key, new TagResolver.Single[0]);
    }

    @Override
    public void sendTitle(Player target, ConfigurableEnum titleKey, ConfigurableEnum subtitleKey) {
        this.execute(titleKey, (impl) -> {

            Title title = Title.title(
                    impl.getComponent(titleKey),
                    impl.getComponent(subtitleKey)
            );

            audiences.player(target).showTitle(title);

        });
    }

}
