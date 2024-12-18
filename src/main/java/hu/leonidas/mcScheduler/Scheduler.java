package hu.leonidas.mcScheduler;

import hu.leonidas.mcScheduler.manager.CommandManager;
import hu.leonidas.mcScheduler.manager.PlaceholderManager;
import hu.leonidas.mcScheduler.manager.SchedulerManager;
import hu.leonidas.mcScheduler.util.Console;
import hu.leonidas.mcScheduler.util.ConfigUtil;
import hu.leonidas.mcScheduler.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Scheduler extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigUtil.init(this);
        Console.init(this);
        SchedulerManager.init(this);

        int pluginId = 24068;
        new Metrics(this, pluginId);

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

        if (ConfigUtil.updateChecker) {
            new UpdateChecker(this, 6915);
        }
    }

}
