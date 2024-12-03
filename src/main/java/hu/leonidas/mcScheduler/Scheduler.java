package hu.leonidas.mcScheduler;

import hu.leonidas.mcScheduler.commands.ReloadCommand;
import hu.leonidas.mcScheduler.manager.CommandManager;
import hu.leonidas.mcScheduler.manager.PlaceholderManager;
import hu.leonidas.mcScheduler.manager.SchedulerManager;
import hu.leonidas.mcScheduler.utils.Console;
import hu.leonidas.mcScheduler.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Scheduler extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigUtil.init(this);
        Console.init(this);
        ReloadCommand.init(this);

        CommandManager commandManager = new CommandManager(this);
        commandManager.registerCommands();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Console.info("The scheduler plugin hooked into PlaceholderAPI.");
        } else {
            Console.warning("Could not find PlaceholderAPI!");
        }

        PlaceholderManager placeholder = new PlaceholderManager();
        placeholder.register();

        SchedulerManager.scheduleCommand();
    }

}
