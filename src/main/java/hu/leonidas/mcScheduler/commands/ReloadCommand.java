package hu.leonidas.mcScheduler.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import hu.leonidas.mcScheduler.util.ChatUtil;
import hu.leonidas.mcScheduler.util.ConfigUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@Command(value = "mcscheduler", alias = {"scheduler", "mc-scheduler"})
public class ReloadCommand extends BaseCommand {

    private static Plugin plugin;

    @SubCommand("reload")
    @Permission("scheduler.admin.reload")
    public void executor(CommandSender sender) {
        ConfigUtil.load(plugin);
        sender.sendMessage(ChatUtil.colorizeHex(ConfigUtil.reloadMessage));
    }

    public static void init(Plugin main) {
        plugin = main;
    }

}
