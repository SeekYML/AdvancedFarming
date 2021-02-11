package de.seekyml.farming.enchantments;

import de.seekyml.farming.AdvancedFarming;
import de.seekyml.farming.config.AdvancedFarmingConfiguration;
import de.seekyml.farming.config.sections.enchants.ReplenishSettings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Replenish extends Enchantment implements Listener {

    AdvancedFarmingConfiguration configuration;

    public Replenish(final AdvancedFarming plugin) {
        super(new NamespacedKey(plugin, "enchReplant"));
    }

    @EventHandler
    public void onEnchantEvent(EnchantItemEvent e) {
        Random random = new Random();
        int setChance = (100 - configuration.getProperty(ReplenishSettings.ENCHANT_CHANCE));
        boolean chance = (random.nextInt(99) + 1 >= setChance);
        if(configuration.getProperty(ReplenishSettings.ENABLED)){
            if (chance) {
                int xpLevelCost;
                // TODO: add this to config
                boolean isHoe = (e.getItem().getType().equals(Material.WOODEN_HOE)
                        || e.getItem().getType().equals(Material.STONE_HOE)
                        || e.getItem().getType().equals(Material.IRON_HOE)
                        || e.getItem().getType().equals(Material.GOLDEN_HOE)
                        || e.getItem().getType().equals(Material.DIAMOND_HOE)
                        || e.getItem().getType().equals(Material.NETHERITE_HOE));
                boolean enchantedAlready = e.getItem().getItemMeta()
                        != null && e.getItem().getItemMeta().getLore()
                        != null && e.getItem().getItemMeta().getLore().contains(configuration.getProperty(ReplenishSettings.ENCHANT_LORE_NAME));
                xpLevelCost = 3;
                if (isHoe && !enchantedAlready && e.whichButton() + 1 == xpLevelCost) {
                    e.getItem().addUnsafeEnchantment(Enchantment.getByKey(AdvancedFarming.replantEnch.getKey()), 1);
                    List<String> lore = (e.getItem().getItemMeta().getLore() == null) ? new ArrayList() : e.getItem().getItemMeta().getLore();
                    lore.add(ChatColor.GRAY + configuration.getProperty(ReplenishSettings.ENCHANT_LORE_NAME).replaceAll("&", "§"));
                    ItemMeta temp = e.getItem().getItemMeta();
                    temp.setLore(lore);
                    e.getItem().setItemMeta(temp);
                }
            }
        }
    }

    @EventHandler
    public void onCropBroken(BlockBreakEvent event) {

        boolean hasEnch = event.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.getByKey(AdvancedFarming.replantEnch.getKey()));
        for (String enabled : configuration.getProperty(ReplenishSettings.ENCHANT_WHITELIST)) {
            if (event.getBlock().getType().equals(Material.SUGAR_CANE) && event.getBlock().getType() == Material.matchMaterial(enabled)) {
                final Player player = event.getPlayer();

                if (!event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SUGAR_CANE) && hasEnch && configuration.getProperty(ReplenishSettings.ENABLED)) {

                    if (configuration.getProperty(ReplenishSettings.UNBREAKABLE_HOE)) {
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

                    Block upper = event.getBlock().getRelative(BlockFace.UP);

                    if (upper.getType().equals(Material.SUGAR_CANE)) {
                        upper.breakNaturally();
                    }
                    event.setCancelled(true);
                }
            } else if (event.getBlock().getType() == Material.matchMaterial(enabled)) {
                Block block = event.getBlock();
                final Location location = block.getLocation();
                final Player player = event.getPlayer();
                final Ageable ageable = (Ageable) block.getBlockData();
                if ((block.getBlockData() instanceof Ageable) && hasEnch && configuration.getProperty(ReplenishSettings.ENABLED)) {
                    if (ageable.getAge() == ageable.getMaximumAge()) {

                        if (!configuration.getProperty(ReplenishSettings.BREAK_PARTIALLY_GROWN)) {
                            event.setCancelled(true);
                        }

                        player.setExhaustion(player.getExhaustion() - 0.005F);
                        ageable.setAge(0);
                        if (configuration.getProperty(ReplenishSettings.REQUIRE_RESOURCE)) {
                            ItemStack resource = null;
                            switch (event.getBlock().getType()) {
                                case POTATOES:
                                    resource = new ItemStack(Material.POTATO);
                                    break;
                                case WHEAT:
                                    resource = new ItemStack(Material.WHEAT_SEEDS);
                                    break;
                                case CARROTS:
                                    resource = new ItemStack(Material.CARROT);
                                    break;
                                case BEETROOTS:
                                    resource = new ItemStack(Material.BEETROOT_SEEDS);
                                    break;
                                case NETHER_WART:
                                    resource = new ItemStack(Material.NETHER_WART);
                                    break;
                            }
                            // TODO: Finish removing a resource to replant
                            for(ItemStack itemStack : player.getInventory().getContents()) {
                                if(itemStack.equals(resource)) {
                                    if (resource.getAmount() > 1) {
                                        resource.setAmount(resource.getAmount() - configuration.getProperty(ReplenishSettings.REQUIRE_RESOURCE_AMOUNT));
                                        event.getPlayer().getInventory().removeItem(resource);
                                    } else {
                                        resource.setType(Material.AIR);
                                    }
                                }
                            }
                        }
                        if (configuration.getProperty(ReplenishSettings.UNBREAKABLE_HOE)) {
                            if (!configuration.getProperty(ReplenishSettings.DROPS_TO_INVENTORY)) {
                                block.getDrops().forEach(drop -> player.getWorld().dropItemNaturally(location, drop));
                            } else {
                                block.getDrops().forEach(drop -> player.getInventory().addItem(drop));
                            }
                            block.setType(Material.AIR);
                        } else {
                            block.breakNaturally();
                        }
                    } else if (!configuration.getProperty(ReplenishSettings.BREAK_PARTIALLY_GROWN)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public String getName() {
        String lore = configuration.getProperty(ReplenishSettings.ENCHANT_LORE_NAME).replaceAll("&", "§");
        return lore;
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