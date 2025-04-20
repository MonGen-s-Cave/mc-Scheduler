package com.mongenscave.mcScheduler.hook;

import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.hook.impl.PlaceholderAPIHook;
import org.bukkit.Bukkit;

public class HookManager {

    private final Scheduler plugin;

    private PlaceholderAPIHook placeholderAPIHook;

    public HookManager(Scheduler plugin) {
        this.plugin = plugin;
    }

    public void registerHooks() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null &&
                Bukkit.getPluginManager().getPlugin("PlaceholderAPI").isEnabled()) {

            plugin.getLogger().info("\u001B[32m   [Hook] PlaceholderAPI successfully enabled.\u001B[0m");

            placeholderAPIHook = new PlaceholderAPIHook();
            placeholderAPIHook.register();
        }
    }

    public PlaceholderAPIHook getPlaceholderAPIHook() {
        return placeholderAPIHook;
    }
}
