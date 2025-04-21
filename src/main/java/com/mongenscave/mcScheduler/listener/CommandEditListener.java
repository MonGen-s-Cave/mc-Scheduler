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

public class CommandEditListener implements Listener {

    public static ConcurrentHashMap<Player, Time> playerEditCommand = new ConcurrentHashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!playerEditCommand.containsKey(player)) {
            return;
        }

        Time editTimeObj = playerEditCommand.get(player);
        if (editTimeObj == null) {
            playerEditCommand.remove(player);
            return;
        }

        String eventName = editTimeObj.getEvent();
        String oldCommand = editTimeObj.getTime();
        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            SchedulerManager.run(() -> {
                TimesGUI timesGUI = new TimesGUI();
                timesGUI.openMenu(player, eventName);
            });
            event.setCancelled(true);
            playerEditCommand.remove(player);
            return;
        }

        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");

        if (eventsSection == null) {
            playerEditCommand.remove(player);
            return;
        }

        editCommand(oldCommand, eventName, message);
        Scheduler.getInstance().getConfigUtil().reloadConfig();
        Scheduler.getInstance().getSchedulerUtil().reload();

        SchedulerManager.run(() -> {
            TimesGUI timesGUI = new TimesGUI();
            player.sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.command_renamed"))
                    .replace("%old_command%", oldCommand)
                    .replace("%new_command%", message));
            timesGUI.openMenu(player, eventName);
        });

        playerEditCommand.remove(player);
        event.setCancelled(true);
    }

    private void editCommand(String oldCommand, String event, String newCommand) {
        deleteCommand(event, oldCommand);
        addCommand(newCommand, event);
    }

    private void addCommand(String command, String event) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");
        if (eventsSection == null) return;

        var eventSection = eventsSection.getSection(event);
        if (eventSection == null) return;

        List<String> commands = eventSection.getStringList("commands");
        if (commands.contains(command)) return;

        commands.add(command);
        eventSection.set("commands", commands);

        try {
            configUtil.getConfig().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteCommand(String event, String command) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");
        if (eventsSection == null) return;

        var eventSection = eventsSection.getSection(event);
        if (eventSection == null) return;

        List<String> commands = eventSection.getStringList("commands");
        if (!commands.contains(command)) return;

        commands.remove(command);
        eventSection.set("commands", commands);

        try {
            configUtil.getConfig().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
