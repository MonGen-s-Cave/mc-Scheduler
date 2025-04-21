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

public class TimeCreateListener implements Listener {

    public static ConcurrentHashMap<Player, String> playerCreateTime = new ConcurrentHashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (playerCreateTime.isEmpty()) {
            return;
        }
        
        Player player = event.getPlayer();
        String eventName = playerCreateTime.get(player);
        
        if (!playerCreateTime.containsKey(player)) {
            return;
        }

        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            playerCreateTime.remove(player);

            SchedulerManager.run(() -> {
                TimesGUI timesGUI = new TimesGUI();
                timesGUI.openMenu(player, eventName);
            });

            event.setCancelled(true);
            return;
        }

        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");

        if(eventsSection == null) {
            playerCreateTime.remove(player);
            return;
        }

        if (eventsSection.getStringList("times." + eventName).contains(message)) {
            player.sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.time_already_exists")));
            playerCreateTime.remove(player);
            event.setCancelled(true);
            return;
        }

        addTime(message, eventName);
        Scheduler.getInstance().getConfigUtil().reloadConfig();
        Scheduler.getInstance().getSchedulerUtil().reload();

        SchedulerManager.run(() -> {
            TimesGUI timesGUI = new TimesGUI();
            player.sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.time_created"))
                    .replace("%time%", message));
            timesGUI.openMenu(player, eventName);
        });


        playerCreateTime.remove(player);
        event.setCancelled(true);
    }

    private void addTime(String time, String event) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");
        if (eventsSection == null) return;

        var eventSection = eventsSection.getSection(event);

        if(eventSection == null) return;

        List<String> times = eventSection.getStringList("times");

        if(times.contains(time)) return;

        times.add(time);

        eventSection.set("times", times);

        try {
            configUtil.getConfig().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
