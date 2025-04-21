package com.mongenscave.mcScheduler;

import com.mongenscave.mcScheduler.api.SchedulerAPI;
import com.mongenscave.mcScheduler.hook.HookManager;
import com.mongenscave.mcScheduler.listener.*;
import com.mongenscave.mcScheduler.manager.CommandManager;
import com.mongenscave.mcScheduler.util.ConfigUtil;
import com.mongenscave.mcScheduler.util.SchedulerUtil;
import com.mongenscave.mcScheduler.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class Scheduler extends JavaPlugin {

    private static Scheduler instance;
    private HookManager hookManager;
    private SchedulerUtil schedulerUtil;
    private CommandManager commandManager;
    private ConfigUtil configUtil;

    @Override
    public void onEnable() {
        instance = this;
        configUtil = new ConfigUtil(this);

        int pluginId = 24068;
        new Metrics(this, pluginId);

        commandManager = new CommandManager(this);
        commandManager.registerCommands();
        commandManager.registerMessages();

        schedulerUtil = new SchedulerUtil(this);
        schedulerUtil.start();

        SchedulerAPI.initialize(new SchedulerAPI());

        getServer().getPluginManager().registerEvents(new EventsCreateListener(), this);
        getServer().getPluginManager().registerEvents(new TimeCreateListener(), this);
        getServer().getPluginManager().registerEvents(new TimeEditListener(), this);
        getServer().getPluginManager().registerEvents(new CommandCreateListener(), this);
        getServer().getPluginManager().registerEvents(new CommandEditListener(), this);

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

        hookManager = new HookManager(this);
        hookManager.registerHooks();

        if (configUtil.getConfig().getBoolean("enable_update_checker")) {
            new UpdateChecker(this, 6915);
        }
    }

    @Override
    public void onDisable() {

        if (hookManager != null && hookManager.getPlaceholderAPIHook() != null) {
            hookManager.getPlaceholderAPIHook().unregister();
        }

        if(commandManager != null) {
            commandManager.shutdown();
        }
    }

    public static Scheduler getInstance() {
        return instance;
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }

    public SchedulerUtil getSchedulerUtil() {
        return schedulerUtil;
    }
}
