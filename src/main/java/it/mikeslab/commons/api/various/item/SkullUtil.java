package it.mikeslab.commons.api.various.item;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for working with Skulls
 * Credits to <a href="https://github.com/mfnalex/JeffLib/blob/286eeb7493cf5a17680ac45ba5ed1eda3252cb30/core/src/main/java/com/jeff_media/jefflib/SkullUtils.java#L70">JeffLib</a>
 */
@UtilityClass
public class SkullUtil {

    private static final Map<String, SkullMeta> SKULL_META_CACHE = new HashMap<>();

    /**
     * Gets a head with the skin of the given OfflinePlayer
     */
    public ItemStack getHead(@NotNull final OfflinePlayer player) {
        final ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
        final SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;
        meta.setOwner(player.getName());
        head.setItemMeta(meta);
        return head;
    }

    /**
     * Gets a head with the given base64 skin
     */
    public ItemStack getHead(@NotNull final String base64) {
        final ItemStack head = XMaterial.PLAYER_HEAD.parseItem();

        final SkullMeta meta = (SkullMeta) getHeadMeta(base64);

        head.setItemMeta(meta);

        return head;
    }


    public ItemMeta getHeadMeta(@NotNull final String base64) {

        if(SKULL_META_CACHE.containsKey(base64)) {
            return SKULL_META_CACHE.get(base64);
        }

        SkullMeta skullMeta = (SkullMeta) XMaterial.PLAYER_HEAD.parseItem().getItemMeta();

        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().put("textures", new Property("textures", base64));
        final Field profileField;
        assert skullMeta != null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        SKULL_META_CACHE.put(base64, skullMeta);

        return skullMeta;
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
