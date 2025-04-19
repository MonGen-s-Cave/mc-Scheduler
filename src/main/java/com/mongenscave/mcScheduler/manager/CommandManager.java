package com.mongenscave.mcScheduler.manager;

import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.command.ReloadCommand;
import com.mongenscave.mcScheduler.util.ChatUtil;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public class CommandManager {

    private final BukkitCommandManager<CommandSender> commandManager;

    public CommandManager(JavaPlugin plugin) {
        this.commandManager = BukkitCommandManager.create(plugin);
    }

    public void registerCommands() {
        commandManager.registerCommand(new ReloadCommand());
    }

    public void registerMessages() {
        String argsMessage = ChatUtil.colorizeHex(Scheduler.getInstance().getConfigUtil().getConfig().getString("Settings.usage_message"));
        String permissionMessage = ChatUtil.colorizeHex(Scheduler.getInstance().getConfigUtil().getConfig().getString("Settings.no_permission_message"));

        this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage(argsMessage));
        this.commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.sendMessage(argsMessage));
        this.commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.sendMessage(argsMessage));
        this.commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage(argsMessage));

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage(permissionMessage));
    }

    public void shutdown() {
        commandManager.unregisterCommands();
    }
}