package de.seekyml.farming.commands;

import ch.jalu.configme.SettingsManager;
import de.seekyml.farming.AdvancedFarming;

import de.seekyml.farming.config.AdvancedFarmingConfiguration;
import de.seekyml.farming.config.sections.PluginSettings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reload implements CommandExecutor {

    AdvancedFarmingConfiguration settingsManager;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(settingsManager.getProperty(PluginSettings.PREFIX).replaceAll("&", "§") + " " + ChatColor.GRAY + "v1.0, developed by @seekyml\n"
                               + "§7------------------------------.\n"
                               + "§6/af reload §8- §7reload config");

        }else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("af.admin") || player.isOp()) {
                    //RELOAD CONFIG HERE
                    player.sendMessage(settingsManager.getProperty(PluginSettings.PREFIX).replaceAll("&", "§") + " " + ChatColor.GREEN + "Config reloaded.");
                }else{
                    player.sendMessage(settingsManager.getProperty(PluginSettings.PREFIX).replaceAll("&", "§") + " " + settingsManager.getProperty(PluginSettings.NO_PERMISSION).replaceAll("&", "§"));
                }
            }
        }
        return false;
    }
}