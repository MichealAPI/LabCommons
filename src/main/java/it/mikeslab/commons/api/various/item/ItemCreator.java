package it.mikeslab.commons.api.various.item;

import com.cryptomorin.xseries.XMaterial;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.config.ConfigField;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is used to create items with a fluent API.
 */
@UtilityClass
public class ItemCreator {

    /**
     * Creates an ItemStack from a GuiElement.
     * @param element The GuiElement source.
     * @return The created ItemStack.
     */
    public ItemStack create(GuiElement element) {
        // Handle skull creation first if headValue is present
        if (element.getHeadValue() != null) {
            return handleSkullCreation(element.getHeadValue(), element.getPlayerName(), Collections.emptyMap());
        }

        return createItemFromData(
                element.getMaterial(),
                element.getAmount(),
                element.getDisplayName(),
                element.getLore(),
                element.getGlow() != null && element.getGlow(),
                element.getCustomModelData(),
                Collections.emptyMap() // No placeholders needed here as GuiElement doesn't support them directly in this structure
        );
    }

    /**
     * Creates an ItemStack from a ConfigurationSection.
     * @param section The ConfigurationSection to create the item from.
     * @return The created ItemStack.
     */
    public ItemStack create(ConfigurationSection section) {
        return create(section, Collections.emptyMap());
    }

    /**
     * Creates an ItemStack from a ConfigurationSection with placeholder support.
     * @param section The ConfigurationSection to create the item from.
     * @param placeholders The placeholders to replace.
     * @return The created ItemStack with placeholders replaced.
     */
    public ItemStack create(ConfigurationSection section, Map<String, String> placeholders) {
        String headValue = section.getString(ConfigField.HEAD_VALUE.getField());
        String materialName = section.getString(ConfigField.MATERIAL.getField());

        // Resolve and handle skull creation first if headValue is present
        if (headValue != null && materialName != null && (materialName.equalsIgnoreCase("PLAYER_HEAD") || materialName.equalsIgnoreCase("SKULL_ITEM"))) {
            // Player name isn't directly available in ConfigurationSection, handle standard head values
            return handleSkullCreation(headValue, null, placeholders);
        }

        // Proceed with regular item creation if not a skull
        Material material = XMaterial.matchXMaterial(materialName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid material '" + materialName + "' for section '" + section.getName() + "'"))
                .parseMaterial();

        if (material == null) {
            throw new IllegalArgumentException("Could not parse material '" + materialName + "' for section '" + section.getName() + "'");
        }

        return createItemFromData(
                material,
                section.getInt(ConfigField.AMOUNT.getField(), 1),
                section.getString(ConfigField.DISPLAYNAME.getField()),
                section.getStringList(ConfigField.LORE.getField()),
                section.getBoolean(ConfigField.GLOWING.getField(), false),
                section.getInt(ConfigField.CUSTOM_MODEL_DATA.getField(), -1),
                placeholders
        );
    }

    /**
     * Core logic to create an ItemStack from provided data and apply common metadata.
     */
    private ItemStack createItemFromData(Material material, int amount, String displayName, List<String> lore, boolean glow, int customModelData, Map<String, String> placeholders) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        // If the item has no meta (e.g., AIR), return the item as is
        if (meta == null) {
            return item;
        }

        // Apply placeholders and set display name
        String finalDisplayName = applyPlaceholders(displayName, placeholders);
        if (finalDisplayName != null) {
            meta.setDisplayName(ComponentsUtil.getSerializedComponent(finalDisplayName));
        }

        // Apply placeholders and set lore
        List<String> finalLore = applyPlaceholders(lore, placeholders);
        if (finalLore != null && !finalLore.isEmpty()) {
            meta.setLore(ComponentsUtil.getSerializedComponents(finalLore));
        }

        // Apply glow effect
        if (glow) {
            applyGlow(meta);
        }

        // Set custom model data
        if (customModelData != -1) {
            meta.setCustomModelData(customModelData);
        }

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Handles the creation of player heads/skulls.
     * @param headValue The raw head value (base64, UUID, player name, or %player%).
     * @param playerName The player name to use if headValue is "%player%" (can be null).
     * @param placeholders Placeholders to apply to the headValue if it's not base64.
     * @return The skull ItemStack.
     */
    private ItemStack handleSkullCreation(String headValue, String playerName, Map<String, String> placeholders) {
        String resolvedValue = resolveHeadValue(headValue, playerName, placeholders);

        if (SkullUtil.isBase64(resolvedValue)) {
            return SkullUtil.getHead(resolvedValue);
        } else {
            // resolvedValue is now a player name or UUID string
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(resolvedValue);
            return SkullUtil.getHead(offlinePlayer);
        }
    }

    /**
     * Resolves the head value string, applying placeholders and handling special cases like "%player%".
     */
    private String resolveHeadValue(String headValue, String playerName, Map<String, String> placeholders) {
        if (headValue == null) {
            return null; // Or throw an exception if headValue is required
        }

        // Handle "%player%" placeholder specifically
        if ("%player%".equalsIgnoreCase(headValue)) {
            return (playerName != null) ? playerName : headValue; // Return original if no player name provided
        }

        // Apply general placeholders only if it's not base64
        if (!SkullUtil.isBase64(headValue)) {
            return applyPlaceholders(headValue, placeholders);
        }

        // Return original base64 value or resolved player name/UUID
        return headValue;
    }


    // --- Overloads for creating items directly with Components ---

    public ItemStack create(Material material, Component displayName) {
        return create(material, displayName, null, false);
    }

    public ItemStack create(Material material, Component displayName, List<Component> lore) {
        return create(material, displayName, lore, false);
    }

    public ItemStack create(Material material, Component displayName, List<Component> lore, boolean glow) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        // If the item has no meta, return the item as is
        if (meta == null) {
            return item;
        }

        if (displayName != null) {
            meta.setDisplayName(ComponentsUtil.serialize(displayName)); // Assuming serialize handles Component -> String
        }

        if (lore != null && !lore.isEmpty()) {
            meta.setLore(ComponentsUtil.serialize(lore)); // Assuming serialize handles List<Component> -> List<String>
        }

        if (glow) {
            applyGlow(meta);
        }

        item.setItemMeta(meta);
        return item;
    }

    // --- Utility Methods ---

    private void applyGlow(ItemMeta meta) {
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    private String applyPlaceholders(String text, Map<String, String> placeholders) {
        if (text == null || placeholders == null || placeholders.isEmpty()) {
            return text;
        }
        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private List<String> applyPlaceholders(List<String> list, Map<String, String> placeholders) {
        if (list == null || list.isEmpty() || placeholders == null || placeholders.isEmpty()) {
            return list;
        }
        return list.stream()
                .map(line -> applyPlaceholders(line, placeholders))
                .collect(Collectors.toList());
    }
}