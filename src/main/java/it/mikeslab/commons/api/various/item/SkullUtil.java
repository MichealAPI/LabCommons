package it.mikeslab.commons.api.various.item;

import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import lombok.experimental.UtilityClass;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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
        return XSkull.createItem().profile(Profileable.of(player)).apply();
    }

    /**
     * Gets a head with the given base64 skin
     */
    public ItemStack getHead(@NotNull final String base64) {
        return XSkull.createItem().profile(Profileable.of(ProfileInputType.BASE64, base64)).apply();
    }


    public ItemMeta getHeadMeta(@NotNull final String base64) {

        if(SKULL_META_CACHE.containsKey(base64)) {
            return SKULL_META_CACHE.get(base64);
        }

        SkullMeta skullMeta = (SkullMeta) getHead(base64).getItemMeta();

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
