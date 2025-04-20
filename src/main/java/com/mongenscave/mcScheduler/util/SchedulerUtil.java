package com.mongenscave.mcScheduler.util;

import com.mongenscave.mcScheduler.api.event.RunSchedulerEvent;
import com.mongenscave.mcScheduler.manager.SchedulerManager;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.sauronsoftware.cron4j.Scheduler;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.TimeZone;

public class SchedulerUtil {

    private Scheduler scheduler = new Scheduler();
    private final com.mongenscave.mcScheduler.Scheduler plugin;

    public SchedulerUtil(com.mongenscave.mcScheduler.Scheduler plugin) {
        this.plugin = plugin;
    }

    public void start() {
        scheduler.start();

        YamlDocument config = plugin.getConfigUtil().getConfig();
        scheduler.setTimeZone(TimeZone.getTimeZone(config.getString("Settings.timezone", "Europe/London")));
        Section eventsSection = config.getSection("Events");
        if (eventsSection == null) return;

        for (Section eventSection : CronUtil.GetEvents()) {

            List<String> timeList = eventSection.getStringList("times");

            for (String cronExpr : timeList) {
                scheduler.schedule(cronExpr, () -> {
                    SchedulerManager.run(() -> {
                        int onlinePlayers = Bukkit.getOnlinePlayers().size();

                        int requiredPlayers = eventSection.getInt("required_players", -1);
                        if (requiredPlayers == -1) {
                            final RunSchedulerEvent runSchedulerEvent = new RunSchedulerEvent(eventSection.getNameAsString());
                            Bukkit.getPluginManager().callEvent(runSchedulerEvent);
                            if (runSchedulerEvent.isCancelled()) return;

                            ActionsUtil.executeActions(eventSection.getStringList("commands"));
                        } else {
                            if (onlinePlayers < requiredPlayers) {
                                List<String> fallbackCommands = eventSection.getStringList("no_enough_player_commands");
                                ActionsUtil.executeActions(fallbackCommands);
                            } else {
                                final RunSchedulerEvent runSchedulerEvent = new RunSchedulerEvent(eventSection.getNameAsString());
                                Bukkit.getPluginManager().callEvent(runSchedulerEvent);
                                if (runSchedulerEvent.isCancelled()) return;

                                ActionsUtil.executeActions(eventSection.getStringList("commands"));
                            }
                        }
                    });
                });
            }
        }
    }

    public void stop() {
        if (scheduler != null && scheduler.isStarted()) {
            scheduler.stop();
        }
    }

    public void reload() {
        stop();
        scheduler = new Scheduler();
        start();
    }
}