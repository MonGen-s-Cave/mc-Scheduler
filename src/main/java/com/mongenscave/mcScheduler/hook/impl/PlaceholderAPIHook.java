package com.mongenscave.mcScheduler.hook.impl;

import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.util.CronUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "Scheduler";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return Scheduler.getInstance().getDescription().getAuthors().toString();
    }

    @NotNull
    @Override
    public String getVersion() {
        return Scheduler.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.startsWith("nextrun_")) {
            String eventName = params.substring("nextrun_".length());
                return CronUtil.getNextRun(eventName);
        }

        return null;
    }
}