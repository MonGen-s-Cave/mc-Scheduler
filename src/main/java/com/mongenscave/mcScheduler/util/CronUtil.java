package com.mongenscave.mcScheduler.util;

import com.mongenscave.mcScheduler.Scheduler;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.sauronsoftware.cron4j.Predictor;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class CronUtil {

    private static final Scheduler plugin = Scheduler.getInstance();
    private static final YamlDocument config = plugin.getConfigUtil().getConfig();

    public static String getNextRun(String event) {
        List<String> times = getEventTimes(event);

        if (times.isEmpty()) {
            return "---";
        }

        long value = Long.MAX_VALUE;

        for(String time : times) {
            Predictor predictor = new Predictor(time);
            predictor.setTimeZone(TimeZone.getTimeZone(config.getString("Settings.timezone")));
            value = Math.min(value, predictor.nextMatchingTime());
        }

        value = value - System.currentTimeMillis();

        if (value <= 0) {
            return plugin.getConfigUtil().getHooks().getString("hooks.settings.PlaceholderAPI.active");
        }

        return formatDuration(value);
    }

    public static List<Section> GetEvents() {
        List<Section> events = new ArrayList<>();

        Section eventsSection = config.getSection("Events");
        if (eventsSection == null) {
            return List.of();
        }

        for(Object key : eventsSection.getKeys())
        {
            String eventName = key.toString();
            Section eventSection = eventsSection.getSection(eventName);
            if (eventSection == null) continue;
            events.add(eventSection);
        }

        return events;
    }

    public static List<String> getEventTimes(String event) {
        List<String> times = new ArrayList<>();

        Section eventsSection = config.getSection("Events");
        if (eventsSection == null) {
            return List.of();
        }

        Section eventSection = eventsSection.getSection(event);
        if (eventSection == null) {
            return List.of();
        }

        List<?> timeList = eventSection.getList("times");
        if (timeList == null) {
            return List.of();
        }

        for (Object obj : timeList) {
            if (obj != null) {
                times.add(obj.toString());
            }
        }

        return times;
    }

    public static List<String> getEventCommands(String event) {
        List<String> commands = new ArrayList<>();

        Section eventsSection = config.getSection("Events");
        if (eventsSection == null) {
            return List.of();
        }

        Section eventSection = eventsSection.getSection(event);
        if (eventSection == null) {
            return List.of();
        }

        List<?> commandList = eventSection.getList("commands");
        if (commandList == null) {
            return List.of();
        }

        for (Object obj : commandList) {
            if (obj != null) {
                commands.add(obj.toString());
            }
        }

        return commands;
    }

    private static String formatDuration(long millis) {
        long seconds = millis / 1000;

        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        return plugin.getConfigUtil().getHooks().getString("hooks.settings.PlaceholderAPI.placeholder_format")
                .replace("%days%", String.valueOf(days))
                .replace("%hours%", String.valueOf(hours))
                .replace("%minutes%", String.valueOf(minutes))
                .replace("%seconds%", String.valueOf(seconds));
    }

    public static String getNextRunFromCron(String cronExpression) {
        try {
            Predictor predictor = new Predictor(cronExpression);
            predictor.setTimeZone(TimeZone.getTimeZone(config.getString("Settings.timezone")));
            long nextTime = predictor.nextMatchingTime();

            long diff = nextTime - System.currentTimeMillis();
            if (diff <= 0) {
                return plugin.getConfigUtil().getHooks().getString("hooks.settings.PlaceholderAPI.active");
            }

            return formatDuration(diff);
        } catch (Exception e) {
            e.printStackTrace();
            return "---!";
        }
    }

    public static List<String> getEvents() {
        List<Section> events = CronUtil.GetEvents();
        List<String> eventNames = new ArrayList<>();

        for(Section event : events) {
            eventNames.add(event.getNameAsString());
        }

        return eventNames;
    }
}
