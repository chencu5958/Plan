package com.djrapitops.plan.command.commands;

import com.djrapitops.plan.Phrase;
import com.djrapitops.plan.Plan;
import com.djrapitops.plan.command.CommandType;
import com.djrapitops.plan.command.SubCommand;
import com.djrapitops.plan.command.hooks.PlaceholderAPIHook;
import com.djrapitops.plan.command.utils.DataUtils;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {

    private Plan plugin;

    public ReloadCommand(Plan plugin) {
        super("reload", "plan.reload", "Reload plugin config & Hooks", CommandType.CONSOLE);

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        plugin.reloadConfig();
        plugin.hookPlanLite();
        ChatColor operatorColor = Phrase.COLOR_MAIN.color();
        ChatColor textColor = Phrase.COLOR_SEC.color();
        sender.sendMessage(textColor + "[" + operatorColor + "PLAN" + textColor + "] Reload complete.");
        
        return true;
    }

}
