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

    @SubCommand("reload")
    @Permission("scheduler.admin.reload")
    public void executor(CommandSender sender) {
        ConfigUtil.load();
        sender.sendMessage(ChatUtil.colorizeHex(ConfigUtil.reloadMessage));
    }


}
