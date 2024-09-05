package it.mikeslab.commons.api.various.item;

import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * This class is used to create items with a fluent API.
 */
public class ItemCreator {

    public ItemStack create(GuiElement element) {

        String displayName = element.getDisplayName();
        List<String> lore = element.getLore();
        Boolean glow = element.getGlow();

        int amount = element.getAmount();
        Material material = element.getMaterial();

        ItemStack item;

        if(element.getHeadValue() != null) {
            item = handleSkullCreation(element);

        } else {
            item = new ItemStack(material, amount);

        }

        ItemMeta meta = item.getItemMeta();

        // If the item has no meta, return the item as is
        if(meta == null) {
            return item;
        }

        if(displayName != null) {
            meta.setDisplayName(ComponentsUtil.getSerializedComponent(displayName));
        }

        if(lore != null) {
            meta.setLore(ComponentsUtil.getSerializedComponents(lore));
        }

        if(glow != null && glow) {
            applyGlow(meta);
        }

        if(element.getCustomModelData() != -1) {
            meta.setCustomModelData(element.getCustomModelData());
        }

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack handleSkullCreation(GuiElement element) {

        String headValue = element.getHeadValue();

        if(SkullUtil.isBase64(headValue)) {
            return SkullUtil.getHead(headValue);
        } else {

            if(headValue.equalsIgnoreCase("%player%") && element.getPlayerName() != null) {
                return SkullUtil.getHead(
                        Bukkit.getOfflinePlayer(element.getPlayerName())
                );
            }

            return SkullUtil.getHead(
                    Bukkit.getOfflinePlayer(headValue)
            );
        }

    }


    public ItemStack create(Material material, Component displayName, List<Component> lore, boolean glow) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ComponentsUtil.serialize(displayName));

        meta.setLore(ComponentsUtil.serialize(lore));

        if(glow) applyGlow(meta);

        item.setItemMeta(meta);

        return item;
    }





    public ItemStack create(Material material, Component displayName, List<Component> lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ComponentsUtil.serialize(displayName));
        meta.setLore(ComponentsUtil.serialize(lore));

        item.setItemMeta(meta);

        return item;
    }




    public ItemStack create(Material material, Component displayName) {

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ComponentsUtil.serialize(displayName));

        item.setItemMeta(meta);

        return item;
    }





    private void applyGlow(ItemMeta meta) {

        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

    }






}
