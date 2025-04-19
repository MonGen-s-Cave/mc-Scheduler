package com.mongenscave.mcScheduler.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class ActionsUtil {

    public static void executeActions(List<String> actions) {
        if (actions == null || actions.isEmpty()) {
            return;
        }

        for (String action : actions) {
            executeAction(action);
        }
    }

    private static void executeAction(String action) {
        String startAction = action.split(" ")[0];
        switch (startAction) {
            case "[COMMAND]":
                    executeCommand(action);
                break;
            case "[MESSAGE]":
                    executeMessage(action);
                break;
            case "[TITLE]":
                    executeTitle(action);
                break;
            case "[SUBTITLE]":
                    executeSubtitle(action);
                break;
            case "[ACTIONBAR]":
                    executeActionBar(action);
                break;
            case "[WEBHOOK]":
                    executeWebhook(action);
                break;
            case "[SOUND]":
                    executeSound(action);
                break;
            default:
                System.out.println("Unknown action: " + action);
        }
    }

    private static void executeCommand(String command) {
        command = command.replace("[COMMAND]", "").trim();
        command = PlaceholderAPI.setPlaceholders(null, command);

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    private static void executeMessage(String message) {
        message = message.replace("[MESSAGE]", "").trim();
        message = ChatUtil.colorizeHex(message);
        message = PlaceholderAPI.setPlaceholders(null, message);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
    private static void executeTitle(String title) {
        title = title.replace("[TITLE]", "").trim();
        title = ChatUtil.colorizeHex(title);
        title = PlaceholderAPI.setPlaceholders(null, title);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, "", 0, 120, 0);
        }
    }

    private static void executeSubtitle(String subtitle) {
        subtitle = subtitle.replace("[SUBTITLE]", "").trim();
        subtitle = ChatUtil.colorizeHex(subtitle);
        subtitle = PlaceholderAPI.setPlaceholders(null, subtitle);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle("", subtitle, 0, 120, 0);
        }
    }

    private static void executeActionBar(String message) {
        message = message.replace("[ACTIONBAR]", "").trim();
        message = ChatUtil.colorizeHex(message);
        message = PlaceholderAPI.setPlaceholders(null, message);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(message);
        }
    }

    private static void executeWebhook(String webhook) {
        webhook = webhook.replace("[WEBHOOK]", "").trim();

        WebhookUtil.sendWebhook(webhook);
    }

    private static void executeSound(String sound) {
        sound = sound.replace("[SOUND]", "").trim();

        Sound mcSound;

        try {
            mcSound = Sound.valueOf(sound.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid sound: " + sound);
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), mcSound, 1.0f, 1.0f);
        }
    }
}
