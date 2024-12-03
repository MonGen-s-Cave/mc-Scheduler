package hu.leonidas.mcScheduler.manager;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import hu.leonidas.mcScheduler.commands.ReloadCommand;
import hu.leonidas.mcScheduler.utils.ChatUtil;
import hu.leonidas.mcScheduler.utils.ConfigUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public class CommandManager {

    private final BukkitCommandManager<CommandSender> commandManager;

    public CommandManager(JavaPlugin plugin) {
        this.commandManager = BukkitCommandManager.create(plugin);

        this.commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> sender.sendMessage(ChatUtil.colorizeHex(ConfigUtil.argsMessage)));
        this.commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> sender.sendMessage(ChatUtil.colorizeHex(ConfigUtil.argsMessage)));
        this.commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> sender.sendMessage(ChatUtil.colorizeHex(ConfigUtil.argsMessage)));
        this.commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> sender.sendMessage(ChatUtil.colorizeHex(ConfigUtil.argsMessage)));

        this.commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendMessage(ChatUtil.colorizeHex(ConfigUtil.permissionMessage)));
    }

    public void registerCommands() {
        commandManager.registerCommand(new ReloadCommand());
    }
}