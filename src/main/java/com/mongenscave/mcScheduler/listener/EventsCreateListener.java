package com.mongenscave.mcScheduler.listener;

import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.guis.EventsGUI;
import com.mongenscave.mcScheduler.manager.SchedulerManager;
import com.mongenscave.mcScheduler.util.ChatUtil;
import com.mongenscave.mcScheduler.util.ConfigUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventsCreateListener implements Listener {

    public static List<Player> playerChat = new ArrayList<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (playerChat.isEmpty()) {
            return;
        }

        if (!playerChat.contains(event.getPlayer())) {
            return;
        }

        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            playerChat.remove(event.getPlayer());

            SchedulerManager.run(() -> {
                EventsGUI eventGUI = new EventsGUI();
                eventGUI.openMenu(event.getPlayer());
            });

            event.setCancelled(true);
            return;
        }

        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");

        if(eventsSection == null) {
            playerChat.remove(event.getPlayer());
            return;
        }

        if(eventsSection.getSection(message) != null) {
            event.getPlayer().sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.event_already_exists")));
            playerChat.remove(event.getPlayer());
            event.setCancelled(true);
            return;
        }

        createEvent(message);
        Scheduler.getInstance().getConfigUtil().reloadConfig();
        Scheduler.getInstance().getSchedulerUtil().reload();

        SchedulerManager.run(() -> {
            EventsGUI eventGUI = new EventsGUI();
            event.getPlayer().sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.event_created"))
                    .replace("%event_name%", message));
            eventGUI.openMenu(event.getPlayer());
        });


        playerChat.remove(event.getPlayer());
        event.setCancelled(true);
    }

    private void createEvent(String event) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");

        eventsSection.createSection(event);
        eventsSection.getSection(event).set("times", new ArrayList<String>());
        eventsSection.getSection(event).set("commands", new ArrayList<String>());

        try {
            configUtil.getConfig().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
