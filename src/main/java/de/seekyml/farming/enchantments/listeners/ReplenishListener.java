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

public class ReplenishListener implements Listener {

    @EventHandler
    public void onCropBroken(BlockBreakEvent event, final AdvancedFarming plugin) {
        final Block block = event.getBlock();
        final Material blocktype = block.getType();
        final Player player = event.getPlayer();
        boolean hasEnch = player.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.getByKey(AdvancedFarming.replantEnch.getKey()));
        for (String enabled : plugin.getConfiguration().getProperty(ReplenishSettings.ENCHANT_WHITELIST))
            // Sugar Cane Break Event
            if (blocktype.equals(Material.SUGAR_CANE) && blocktype == Material.matchMaterial(enabled)) {
                if (!block.getRelative(BlockFace.DOWN).getType().equals(Material.SUGAR_CANE) && hasEnch && plugin.getConfiguration().getProperty(ReplenishSettings.ENABLED)) {
                    if (!plugin.getConfiguration().getProperty(ReplenishSettings.UNBREAKABLE_HOE)) {
                        damageTool(player);
                    }

                    final Block upper = block.getRelative(BlockFace.UP);

                    player.setExhaustion(player.getExhaustion() - 0.005F);

                    if (upper.getType().equals(Material.SUGAR_CANE)) {
                        upper.breakNaturally();
                    }
                    event.setCancelled(true);
                }
                // Regular Crop Break Event
            } else if (blocktype == Material.matchMaterial(enabled)) {

                final Location location = block.getLocation();
                final Ageable ageable = (Ageable) block.getBlockData();

                if ((block.getBlockData() instanceof Ageable) && hasEnch && plugin.getConfiguration().getProperty(ReplenishSettings.ENABLED)) {
                    if (ageable.getAge() == ageable.getMaximumAge()) {
                        if (!plugin.getConfiguration().getProperty(ReplenishSettings.BREAK_PARTIALLY_GROWN)) {
                            event.setCancelled(true);
                        }

                        player.setExhaustion(player.getExhaustion() - 0.005F);
                        ageable.setAge(0);
                        if (plugin.getConfiguration().getProperty(ReplenishSettings.REQUIRE_RESOURCE)) {
                            consumeResource(blocktype, player, plugin);
                        }
                        if (plugin.getConfiguration().getProperty(ReplenishSettings.UNBREAKABLE_HOE)) {
                            if (!plugin.getConfiguration().getProperty(ReplenishSettings.DROPS_TO_INVENTORY)) {
                                block.getDrops().forEach(drop -> player.getWorld().dropItemNaturally(location, drop));
                            } else {
                                block.getDrops().forEach(drop -> player.getInventory().addItem(drop));
                            }
                            block.setType(Material.AIR);
                        } else {
                           replaceNaturally(block,blocktype);
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

    public void damageTool(Player player){
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

    public void consumeResource(Material blocktype, Player player, final AdvancedFarming plugin){
        Material resource = null;
        // Get Replant Resource from Block
        switch (blocktype) {
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