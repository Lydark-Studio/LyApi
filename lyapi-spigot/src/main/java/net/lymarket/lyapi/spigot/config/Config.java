/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2022, LyMarket
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * https://github.com/Lydark-Studio/LyApi/blob/master/LICENSE
 *
 * Contact: contact@lymarket.net
 */

package net.lymarket.lyapi.spigot.config;

import com.cryptomorin.xseries.XMaterial;
import net.lymarket.common.config.ConfigGenerator;
import net.lymarket.lyapi.spigot.utils.ItemBuilder;
import net.lymarket.lyapi.spigot.utils.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config extends ConfigGenerator {
    
    
    public Config(JavaPlugin plugin, String name){
        super(plugin, name);
    }
    
    public Config(JavaPlugin plugin, String name, String resourcePath, String filePath){
        super(plugin, name, resourcePath, filePath);
    }
    
    public ItemStack getItem(String key, ItemStack orgItem){
        String section = "items." + key + ".";
        final String name = getString(section + "name");
        final Material material = Material.valueOf(getString(section + "material"));
        final ItemStack midItem = orgItem.getType() == Material.AIR ? new ItemStack(material) : orgItem;
        final String skin = getString(section + "skin");
        ItemStack item = new ItemBuilder(midItem).setDisplayName(name).build();
        if (material == XMaterial.PLAYER_HEAD.parseMaterial()){
            if (skin != null && !skin.isEmpty()){
                item = new ItemBuilder(item).setHeadSkin(skin).setDisplayName(name).build();
            }
        }
        try {
            item = new ItemBuilder(item).setLore(getStringList(section + "description")).build();
        } catch (NullPointerException ignored) {
        }
        try {
            HashMap < Enchantment, Integer > enchantments = new HashMap <>();
            for ( String ench : getStringList(section + "enchantments") ){
                String[] enchantment = ench.split(":");
                enchantments.put(Enchantment.getByName(enchantment[0]), Integer.parseInt(enchantment[1]));
            }
            
            item = new ItemBuilder(item).addEnchantments(enchantments).build();
        } catch (NullPointerException ignored) {
        }
    
        try {
            final List < String > nbts = getStringList(section + "nbt");
            for ( String nbt : nbts ){
                String[] nbtData = nbt.split(":");
                item = new ItemBuilder(item).addTag(nbtData[0], nbtData[1]).build();
            }
        
        } catch (NullPointerException ignored) {
        }
        try {
            final int customModelData = getInt(section + "customModelData");
            item = new ItemBuilder(item).setCustomModelData(customModelData).build();
        } catch (NullPointerException ignored) {
        }
        return item;
    }
    
    public ItemStack getItem(String key){
        return getItem(key, new ItemStack(Material.AIR));
    }
    
    public ItemStack getItem(String key, HashMap < String, String > replacements){
        final ItemStack item = getItem(key);
        List < String > lore = new ArrayList <>();
        try {
            for ( String s : item.getItemMeta().getLore() ){
                for ( String key2 : replacements.keySet() ){
                    s = s.replace("%" + key2 + "%", Utils.format(replacements.get(key2)));
                }
                lore.add(s);
            }
            
        } catch (NullPointerException ignored) {
        }
    
    
        String name = item.getItemMeta().getDisplayName();
        for ( String key2 : replacements.keySet() ){
            name = name.replace("%" + key2 + "%", Utils.format(replacements.get(key2)));
        }
    
    
        return new ItemBuilder(item).setDisplayName(name).setLore(lore).build();
    }
    
    public String getMenuTitle(String menu){
        return getString("menus." + menu + ".title");
    }
    
    
    public String getMenuTitle(String menu, String word, String toReplace){
        return getString("menus." + menu + ".title").replace("%" + word + "%", toReplace);
    }
    
}
