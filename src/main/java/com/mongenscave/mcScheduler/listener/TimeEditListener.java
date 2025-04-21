package com.mongenscave.mcScheduler.listener;

import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.guis.TimesGUI;
import com.mongenscave.mcScheduler.manager.SchedulerManager;
import com.mongenscave.mcScheduler.util.ChatUtil;
import com.mongenscave.mcScheduler.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TimeEditListener implements Listener {

    public static ConcurrentHashMap<Player, Time> playerEditTime = new ConcurrentHashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!playerEditTime.containsKey(player)) {
            return;
        }

        Time timeObj = playerEditTime.get(player);
        if (timeObj == null) {
            playerEditTime.remove(player);
            return;
        }

        String eventName = timeObj.getEvent();
        String oldTime = timeObj.getTime();
        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            SchedulerManager.run(() -> {
                TimesGUI timesGUI = new TimesGUI();
                timesGUI.openMenu(player, eventName);
            });
            event.setCancelled(true);
            playerEditTime.remove(player);
            return;
        }

        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");

        if (eventsSection == null) {
            playerEditTime.remove(player);
            return;
        }

        editTime(oldTime, eventName, message);
        Scheduler.getInstance().getConfigUtil().reloadConfig();
        Scheduler.getInstance().getSchedulerUtil().reload();

        SchedulerManager.run(() -> {
            TimesGUI timesGUI = new TimesGUI();
            player.sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.time_renamed"))
                    .replace("%old_time%", oldTime)
                    .replace("%new_time%", message));
            timesGUI.openMenu(player, eventName);
        });

        playerEditTime.remove(player);
        event.setCancelled(true);
    }

    private void editTime(String oldTime, String event, String newTime) {
        deleteTime(event, oldTime);
        addTime(newTime, event);
    }

    private void addTime(String time, String event) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");
        if (eventsSection == null) return;

        var eventSection = eventsSection.getSection(event);
        if (eventSection == null) return;

        List<String> times = eventSection.getStringList("times");
        if (times.contains(time)) return;

        times.add(time);
        eventSection.set("times", times);

        try {
            configUtil.getConfig().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteTime(String event, String time) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");
        if (eventsSection == null) return;

        var eventSection = eventsSection.getSection(event);
        if (eventSection == null) return;

        List<String> times = eventSection.getStringList("times");
        if (!times.contains(time)) return;

        times.remove(time);
        eventSection.set("times", times);

        try {
            configUtil.getConfig().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
