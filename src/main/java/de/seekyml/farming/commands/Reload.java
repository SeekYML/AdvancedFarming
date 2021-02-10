package de.seekyml.farming.commands;

import de.seekyml.farming.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Main.getPlugin().getConfig().getString("general.prefix").replaceAll("&", "§") + " " + ChatColor.GRAY + "v1.0, developed by @seekyml\n"
                    + "§7------------------------------.\n"
                    + "§6/af reload §8- §7reload config");

        }else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission("af.admin") || player.isOp()) {
                    Main.getPlugin().reloadConfig();
                    player.sendMessage(Main.getPlugin().getConfig().getString("general.prefix").replaceAll("&", "§") + " " + ChatColor.GREEN + "Config reloaded.");
                }else{
                    player.sendMessage(Main.getPlugin().getConfig().getString("general.prefix").replaceAll("&", "§") + " " + Main.getPlugin().getConfig().getString("general.nopermissions").replaceAll("&", "§"));
                }
            }
        }
        return false;
    }
}
