package com.mongenscave.mcScheduler.command;

import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.util.ChatUtil;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;

@Command(value = "mcscheduler", alias = {"scheduler", "mc-scheduler"})
public class ReloadCommand extends BaseCommand {

    @SubCommand("reload")
    @Permission("scheduler.admin.reload")
    public void executor(CommandSender sender) {
        Scheduler.getInstance().getConfigUtil().reloadConfig();
        Scheduler.getInstance().getSchedulerUtil().reload();
        sender.sendMessage(ChatUtil.colorizeHex(Scheduler.getInstance().getConfigUtil().getConfig().getString("Settings.reload_message")));
    }
}
