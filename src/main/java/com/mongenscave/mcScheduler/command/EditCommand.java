package com.mongenscave.mcScheduler.command;

import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.guis.EditorGUI;
import com.mongenscave.mcScheduler.guis.EventsGUI;
import com.mongenscave.mcScheduler.util.ChatUtil;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(value = "mcscheduler", alias = {"scheduler", "mc-scheduler"})
public class EditCommand extends BaseCommand {

    @SubCommand("editor")
    @Permission("scheduler.admin.editor")
    public void executor(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.colorizeHex(Scheduler.getInstance().getConfigUtil().getConfig().getString("Settings.not_player_message")));
            return;
        }

        EventsGUI eventsGUI = new EventsGUI();
        eventsGUI.openMenu((Player) sender);
    }
}
