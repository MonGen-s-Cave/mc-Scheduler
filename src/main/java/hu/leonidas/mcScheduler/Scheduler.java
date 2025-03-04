package hu.leonidas.mcScheduler;

import hu.leonidas.mcScheduler.manager.CommandManager;
import hu.leonidas.mcScheduler.manager.PlaceholderManager;
import hu.leonidas.mcScheduler.manager.SchedulerManager;
import hu.leonidas.mcScheduler.util.ConfigUtil;
import hu.leonidas.mcScheduler.util.Console;
import hu.leonidas.mcScheduler.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Scheduler extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigUtil.init(this);
        Console.init(this);

        int pluginId = 24068;
        new Metrics(this, pluginId);

        CommandManager commandManager = new CommandManager(this);
        commandManager.registerCommands();

        String main = "\u001B[38;5;87m";
        String yellow = "\u001B[33m";
        String reset = "\u001B[0m";
        String software = getServer().getName();
        String version = getServer().getVersion();

        System.out.println(" ");
        System.out.println(main + "  ____   ____ _   _ _____ ____  _   _ _     _____ ____ " + reset);
        System.out.println(main + " / ___| / ___| | | | ____|  _ \\| | | | |   | ____|  _ \\ " + reset);
        System.out.println(main + " \\___ \\| |   | |_| |  _| | | | | | | | |   |  _| | |_) |" + reset);
        System.out.println(main + "  ___) | |___|  _  | |___| |_| | |_| | |___| |___|  _ < " + reset);
        System.out.println(main + " |____/ \\____|_| |_|_____|____/ \\___/|_____|_____|_| \\_\\" + reset);
        System.out.println(" ");
        System.out.println(main + "   The plugin successfully started." + reset);
        System.out.println(main + "   mc-Scheduler " + software + " " + version + reset);
        System.out.println(yellow + "   Discord @ dc.mongenscave.com" + reset);
        System.out.println(" ");

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
