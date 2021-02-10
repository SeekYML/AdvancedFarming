package de.seekyml.farming.enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.seekyml.farming.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Replenish extends Enchantment implements Listener {

    public Replenish() {
        super(new NamespacedKey(Main.getPlugin(), "enchReplant"));
    }

    @EventHandler
    public void onEnchantEvent(EnchantItemEvent e) {
        Random random = new Random();
        int setChance = (100 - Main.getPlugin().getConfig().getInt("enchantments.replenish.enchantmentchance"));
        boolean chance = (random.nextInt(99) + 1 >= setChance);
        if(Main.getPlugin().getConfig().getBoolean("enchantments.replenish.enable")){
            if (chance) {
                int xpLevelCost;
                boolean isHoe = (e.getItem().getType().equals(Material.WOODEN_HOE)
                        || e.getItem().getType().equals(Material.STONE_HOE)
                        || e.getItem().getType().equals(Material.IRON_HOE)
                        || e.getItem().getType().equals(Material.GOLDEN_HOE)
                        || e.getItem().getType().equals(Material.DIAMOND_HOE)
                        || e.getItem().getType().equals(Material.NETHERITE_HOE));
                boolean enchantedAlready = e.getItem().getItemMeta()
                        != null && e.getItem().getItemMeta().getLore()
                        != null && e.getItem().getItemMeta().getLore().contains(Main.getPlugin().getConfig().getString("enchantments.replenish.lore"));
                xpLevelCost = 3;
                if (isHoe && !enchantedAlready && e.whichButton() + 1 == xpLevelCost) {
                    e.getItem().addUnsafeEnchantment(Enchantment.getByKey(Main.replantEnch.getKey()), 1);
                    List<String> lore = (e.getItem().getItemMeta().getLore() == null) ? new ArrayList() : e.getItem().getItemMeta().getLore();
                    lore.add(ChatColor.GRAY + Main.getPlugin().getConfig().getString("enchantments.replenish.lore").replaceAll("&","§"));
                    ItemMeta temp = e.getItem().getItemMeta();
                    temp.setLore(lore);
                    e.getItem().setItemMeta(temp);
                }
            }
        }
    }

    @EventHandler
    public void onCropBroken(BlockBreakEvent event) {
        boolean hasEnch = Bukkit.getPlayer(event.getPlayer().getUniqueId()).getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.getByKey(Main.replantEnch.getKey()));
        Block block = event.getBlock();
        if ((block.getBlockData() instanceof Ageable) && hasEnch && Main.getPlugin().getConfig().getBoolean("enchantments.replenish.enable")) {
            final Ageable ageable = (Ageable) block.getBlockData();
            if (ageable.getAge() == ageable.getMaximumAge()) {
                final Location location = block.getLocation();
                final Player player = event.getPlayer();

                if (!Main.getPlugin().getConfig().getBoolean("enchantments.replenish.dropstoinventory")){
                    block.getDrops().forEach(drop -> player.getWorld().dropItemNaturally(location,drop));
                }else {
                    block.getDrops().forEach(drop -> player.getInventory().addItem(drop));
                }
                if (!Main.getPlugin().getConfig().getBoolean("enchantments.replenish.breaknotfullygrown")){
                    event.setCancelled(true);
                }
                if (Main.getPlugin().getConfig().getBoolean("enchantments.replenish.unbreakable")) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    ItemMeta im = item.getItemMeta();
                    if (im instanceof Damageable) {
                        Damageable dmg = (Damageable) im;
                        if (dmg.hasDamage()) {
                            dmg.setDamage(dmg.getDamage() + 1);
                        }
                        item.setItemMeta(im);
                    }
                }
                player.setExhaustion(player.getExhaustion() - 0.005F);
                ageable.setAge(0);
                block.setBlockData(ageable);
            }else if (!Main.getPlugin().getConfig().getBoolean("enchantments.replenish.breaknotfullygrown")){
                event.setCancelled(true);
            }
        }
    }

    public String getName() {
        return Objects.requireNonNull(Main.getPlugin().getConfig().getString("enchantments.replenish.lore").replaceAll("&","§"));
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getStartLevel() {
        return 1;
    }

    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    public boolean isTreasure() {
        return true;
    }

    public boolean isCursed() {
        return false;
    }

    public boolean conflictsWith(Enchantment e) {
        return false;
    }

    public boolean canEnchantItem(ItemStack is) {
        boolean isHoe = (is.getType().equals(Material.WOODEN_HOE) || is.getType().equals(Material.STONE_HOE) || is.getType().equals(Material.IRON_HOE) || is.getType().equals(Material.GOLDEN_HOE) || is.getType().equals(Material.DIAMOND_HOE) || is.getType().equals(Material.NETHERITE_HOE));
        return isHoe;
    }

}
