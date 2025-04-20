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

        if (containsActionsTitle(actions) || containsActionsSubTitle(actions)) {
            executeTitleAndSubtitle(actions);
        }

        for (String action : actions) {
            if (containsActionTitle(action) || containsActionSubTitle(action)) {
                continue;
            }

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
        command = PlaceholderAPI.setPlaceholders(null, command.replace("[COMMAND]", ""));
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    private static void executeMessage(String message) {
        message = formatMessage(message.replace("[MESSAGE]", ""));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    private static void executeTitleAndSubtitle(List<String> actions) {
        String title = getActionTitle(actions);
        String subtitle = getActionSubTitle(actions);

        if (title == null && subtitle == null) return;

        title = formatMessage(title != null ? title : "");
        subtitle = formatMessage(subtitle != null ? subtitle : "");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, 0, 120, 0);
        }
    }

    private static void executeActionBar(String message) {
        message = formatMessage(message.replace("[ACTIONBAR]", ""));
        for (Player player : Bukkit.getOnlinePlayers()) {
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

    private static String formatMessage(String raw) {
        return ChatUtil.colorizeHex(PlaceholderAPI.setPlaceholders(null, raw.trim()));
    }

    private static boolean containsActionTitle(String action) {
        return action.startsWith("[TITLE]");
    }

    private static boolean containsActionSubTitle(String action) {
        return action.startsWith("[SUBTITLE]");
    }

    private static boolean containsActionsTitle(List<String> actions) {
        return actions.stream().anyMatch(a -> a.startsWith("[TITLE]"));
    }

    private static boolean containsActionsSubTitle(List<String> actions) {
        return actions.stream().anyMatch(a -> a.startsWith("[SUBTITLE]"));
    }

    private static String getActionTitle(List<String> actions) {
        return actions.stream()
                .filter(a -> a.startsWith("[TITLE]"))
                .findFirst()
                .map(a -> a.replace("[TITLE]", "").trim())
                .orElse(null);
    }

    private static String getActionSubTitle(List<String> actions) {
        return actions.stream()
                .filter(a -> a.startsWith("[SUBTITLE]"))
                .findFirst()
                .map(a -> a.replace("[SUBTITLE]", "").trim())
                .orElse(null);
    }
}
