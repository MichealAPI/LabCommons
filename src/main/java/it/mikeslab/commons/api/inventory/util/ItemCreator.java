package it.mikeslab.commons.api.inventory.util;

import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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

        Component displayName = element.getDisplayName();
        List<Component> lore = element.getLore();
        Boolean glow = element.getGlow();

        int amount = element.getAmount();
        Material material = element.getMaterial();

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        // If the item has no meta, return the item as is
        if(meta == null) {
            return item;
        }

        if(displayName != null) {
            meta.setDisplayName(ComponentsUtil.serialize(displayName));
        }

        if(lore != null) {
            meta.setLore(ComponentsUtil.serialize(lore));
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
