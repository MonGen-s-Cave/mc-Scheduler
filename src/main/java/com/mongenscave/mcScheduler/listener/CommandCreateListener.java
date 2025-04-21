package com.mongenscave.mcScheduler.listener;

import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.guis.CommandsGUI;
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

public class CommandCreateListener implements Listener {

    public static ConcurrentHashMap<Player, String> playerCreateCommand = new ConcurrentHashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (playerCreateCommand.isEmpty()) {
            return;
        }

        Player player = event.getPlayer();
        String eventName = playerCreateCommand.get(player);

        if (!playerCreateCommand.containsKey(player)) {
            return;
        }

        String message = event.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            playerCreateCommand.remove(player);

            SchedulerManager.run(() -> {
                CommandsGUI commandsGUI = new CommandsGUI();
                commandsGUI.openMenu(player, eventName);
            });

            event.setCancelled(true);
            return;
        }

        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");

        if(eventsSection == null) {
            playerCreateCommand.remove(player);
            return;
        }

        if (eventsSection.getStringList("commands." + eventName).contains(message)) {
            player.sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.command_already_exists")));
            playerCreateCommand.remove(player);
            event.setCancelled(true);
            return;
        }

        addTime(message, eventName);
        Scheduler.getInstance().getConfigUtil().reloadConfig();
        Scheduler.getInstance().getSchedulerUtil().reload();

        SchedulerManager.run(() -> {
            CommandsGUI commandsGUI = new CommandsGUI();
            player.sendMessage(ChatUtil.colorizeHex(configUtil.getConfig().getString("Settings.command_created"))
                    .replace("%command%", message));
            commandsGUI.openMenu(player, eventName);
        });


        playerCreateCommand.remove(player);
        event.setCancelled(true);
    }

    private void addTime(String command, String event) {
        ConfigUtil configUtil = Scheduler.getInstance().getConfigUtil();
        var eventsSection = configUtil.getConfig().getSection("Events");
        if (eventsSection == null) return;

        var eventSection = eventsSection.getSection(event);

        if(eventSection == null) return;

        List<String> commands = eventSection.getStringList("commands");

        if(commands.contains(command)) return;

        commands.add(command);

        eventSection.set("commands", commands);

        try {
            configUtil.getConfig().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
