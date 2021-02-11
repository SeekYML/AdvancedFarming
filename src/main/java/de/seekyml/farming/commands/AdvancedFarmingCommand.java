package de.seekyml.farming.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import de.seekyml.farming.AdvancedFarming;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("advancedfarming|af")
public final class AdvancedFarmingCommand extends BaseCommand {

    private final AdvancedFarming plugin;

    public AdvancedFarmingCommand(final AdvancedFarming plugin) {
        this.plugin = plugin;
    }


    @Default
    @HelpCommand
    public void help(final CommandHelp help)
    {
        help.showHelp();
    }


    @Subcommand("reload")
    @CommandPermission("af.admin")
    public void reload(final CommandSender sender)
    {
        this.plugin.reload();

        sender.sendMessage(/* todo: add prefix from PluginSettings */ChatColor.GREEN + "plugin reloaded!");
    }

}