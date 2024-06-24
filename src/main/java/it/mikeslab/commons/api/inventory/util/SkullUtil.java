package it.mikeslab.commons.api.inventory.util;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

/**
 * Utility class for working with Skulls
 * Credits to <a href="https://github.com/mfnalex/JeffLib/blob/286eeb7493cf5a17680ac45ba5ed1eda3252cb30/core/src/main/java/com/jeff_media/jefflib/SkullUtils.java#L70">JeffLib</a>
 */
@UtilityClass
public class SkullUtil {

    /**
     * Gets a head with the skin of the given OfflinePlayer
     */
    public ItemStack getHead(@NotNull final OfflinePlayer player) {
        final ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
        final SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(player);
        head.setItemMeta(meta);
        return head;
    }

    /**
     * Gets a head with the given base64 skin
     */
    public ItemStack getHead(@NotNull final String base64) {
        final ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
        @SuppressWarnings("TypeMayBeWeakened") final SkullMeta meta = (SkullMeta) head.getItemMeta();
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", new Property("textures", base64));
        final Field profileField;
        assert meta != null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, gameProfile);
            head.setItemMeta(meta);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return head;
    }

    /**
     * Checks if a string is a valid base64 string
     * @param base64 the string to check
     * @return true if the string is a valid base64 string
     */
    public boolean isBase64(String base64) {
        Base64.Decoder decoder = Base64.getDecoder();

        try {
            decoder.decode(base64);
        } catch (IllegalArgumentException iae) {
            return false;
        }

        return true;
    }
}
