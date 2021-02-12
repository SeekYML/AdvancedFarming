package de.seekyml.farming.enchantments.listeners;

import de.seekyml.farming.AdvancedFarming;
import de.seekyml.farming.config.sections.enchants.ReplenishSettings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import sun.util.locale.provider.AvailableLanguageTags;

public class CropBreakListener implements Listener {

    @EventHandler
    public void onCropBroken(BlockBreakEvent event, final AdvancedFarming plugin) {
        final Block block = event.getBlock();
        final Player player = event.getPlayer();
        boolean hasEnch = event.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.getByKey(AdvancedFarming.replantEnch.getKey()));
        for (String enabled : plugin.getConfiguration().getProperty(ReplenishSettings.ENCHANT_WHITELIST))
            if (event.getBlock().getType().equals(Material.SUGAR_CANE) && event.getBlock().getType() == Material.matchMaterial(enabled)) {

                if (!event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SUGAR_CANE) && hasEnch && plugin.getConfiguration().getProperty(ReplenishSettings.ENABLED)) {

                    if (!plugin.getConfiguration().getProperty(ReplenishSettings.UNBREAKABLE_HOE)) {
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
                final Location location = block.getLocation();
                final Ageable ageable = (Ageable) block.getBlockData();
                final Material oldblock = event.getBlock().getType();
                if ((block.getBlockData() instanceof Ageable) && hasEnch && plugin.getConfiguration().getProperty(ReplenishSettings.ENABLED)) {
                    if (ageable.getAge() == ageable.getMaximumAge()) {

                        if (!plugin.getConfiguration().getProperty(ReplenishSettings.BREAK_PARTIALLY_GROWN)) {
                            event.setCancelled(true);
                        }

                        player.setExhaustion(player.getExhaustion() - 0.005F);
                        ageable.setAge(0);
                        if (plugin.getConfiguration().getProperty(ReplenishSettings.REQUIRE_RESOURCE)) {
                            consumeResource(block, player, plugin);
                        }
                        if (plugin.getConfiguration().getProperty(ReplenishSettings.UNBREAKABLE_HOE)) {
                            if (!plugin.getConfiguration().getProperty(ReplenishSettings.DROPS_TO_INVENTORY)) {
                                block.getDrops().forEach(drop -> player.getWorld().dropItemNaturally(location, drop));
                            } else {
                                block.getDrops().forEach(drop -> player.getInventory().addItem(drop));
                            }
                            block.setType(Material.AIR);
                        } else {
                           replaceNaturally(block,oldblock);
                        }
                    } else if (!plugin.getConfiguration().getProperty(ReplenishSettings.BREAK_PARTIALLY_GROWN)) {
                        event.setCancelled(true);
                    }
                }
            }
    }

    public void replaceNaturally(Block newblock,Material oldmaterial){
        newblock.breakNaturally();
        newblock.setType(oldmaterial);
        ((Ageable) newblock.getBlockData()).setAge(0);
    }

    public void damageTool(){

    }

    public void consumeResource(Block block, Player player, final AdvancedFarming plugin){
        Material resource = null;
        // Get Replant Resource from Block
        switch (block.getType()) {
            case POTATOES:
                resource = Material.POTATO;
                break;
            case WHEAT:
                resource = Material.WHEAT_SEEDS;
                break;
            case CARROTS:
                resource = Material.CARROT;
                break;
            case BEETROOTS:
                resource = Material.BEETROOT_SEEDS;
                break;
            case NETHER_WART:
                resource = Material.NETHER_WART;
                break;
        }
        // Remove Item from Player's Inventory
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack.getType().equals(resource)) {
                if (itemStack.getAmount() > 1) {
                    itemStack.setAmount(itemStack.getAmount() - plugin.getConfiguration().getProperty(ReplenishSettings.REQUIRE_RESOURCE_AMOUNT));
                    player.getInventory().removeItem(itemStack);
                } else {
                    itemStack.setType(Material.AIR);
                }
            }
        }
    }
}
