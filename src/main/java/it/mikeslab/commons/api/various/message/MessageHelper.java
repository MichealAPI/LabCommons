package it.mikeslab.commons.api.various.message;

import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.config.ConfigurableEnum;
import it.mikeslab.commons.api.config.impl.ConfigurableImpl;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class MessageHelper {

    final BukkitAudiences audiences;

    public MessageHelper(final BukkitAudiences audiences) {
        this.audiences = audiences;
    }

    /**
     * Send a message to the target
     * @param target the target to send the message to
     * @param key the key of the configurable
     */
    abstract void sendMessage(CommandSender target, ConfigurableEnum key);

    /**
     * Send a message to the target
     * @param target the target to send the message to
     * @param key the key of the configurable
     * @param tagResolvers the tag resolvers to apply
     */
    abstract void sendMessage(CommandSender target, ConfigurableEnum key, TagResolver.Single... tagResolvers);

    /**
     * Send a message to the target
     * @param target the target to send the message to
     * @param titleKey the key of the title
     * @param subtitleKey the key of the subtitle
     */
    abstract void sendTitle(Player target, ConfigurableEnum titleKey, ConfigurableEnum subtitleKey);


    /**
     * Execute the consumer if the configurable is present
     * @param key the configurable key does not need to be validated, the implementation will do it
     * @param consumer the consumer to execute
     */
    void execute(ConfigurableEnum key, Consumer<ConfigurableImpl> consumer) {

        Optional<ConfigurableImpl> optional = getConfigurableImpl(key);

        optional.ifPresent(consumer);

    }

    private Optional<ConfigurableImpl> getConfigurableImpl(ConfigurableEnum key) {

        String classSimpleName = key
                .getClass()
                .getSimpleName();

        ConfigurableImpl impl = LabCommons.fromClassName(classSimpleName);

        if(impl == null) {
            throw new IllegalArgumentException("No configuration found for class " + classSimpleName);
        }

        return Optional.of(impl);
    }



}
