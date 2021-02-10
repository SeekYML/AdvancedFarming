package de.seekyml.farming;

import de.seekyml.farming.commands.Reload;
import de.seekyml.farming.enchantments.Replenish;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;

public final class AdvancedFarming extends JavaPlugin {

    private static AdvancedFarming plugin;

    public static Replenish replantEnch;

    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();
        registerListeners();
        registerEnchants();
        registerCommands();
    }

    public static AdvancedFarming getPlugin() {
        return plugin;
    }

    @Override
    public void onDisable() {
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");

            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);

            if (byKey.containsKey(replantEnch.getKey())) {
                byKey.remove(replantEnch.getKey());
            }
            Field nameField = Enchantment.class.getDeclaredField("byName");

            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);

            if (byName.containsKey(replantEnch.getName())) {
                byName.remove(replantEnch.getName());
            }
        } catch (Exception ignored) {
        }
    }

    public void registerEnchants(){
        replantEnch = new Replenish();
        registerEnchantment(replantEnch);
    }
    public void registerListeners(){
        getServer().getPluginManager().registerEvents(new Replenish(),this);
    }
    public void registerCommands(){
        getCommand("advancedfarming").setExecutor(new Reload());
    }

    public static void registerEnchantment(Enchantment enchantment) {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, Boolean.TRUE);
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}