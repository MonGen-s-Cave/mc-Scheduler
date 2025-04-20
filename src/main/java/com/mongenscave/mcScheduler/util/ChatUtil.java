package com.mongenscave.mcScheduler.util;

import com.mongenscave.mcScheduler.Scheduler;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String colorizeHex(String message) {
        if (message == null) {
            return null;
        }

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String color = matcher.group(1);
            String replacement = String.format("\u00A7x\u00A7%c\u00A7%c\u00A7%c\u00A7%c\u00A7%c\u00A7%c",
                    color.charAt(0), color.charAt(1), color.charAt(2),
                    color.charAt(3), color.charAt(4), color.charAt(5));
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString()).replace("%prefix%", Scheduler.getInstance().getConfigUtil().getConfig().getString("Settings.prefix"));
    }

    public static String colorizeHexPrefix(String message) {
        if (message == null) {
            return null;
        }

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            String color = matcher.group(1);
            String replacement = String.format("\u00A7x\u00A7%c\u00A7%c\u00A7%c\u00A7%c\u00A7%c\u00A7%c",
                    color.charAt(0), color.charAt(1), color.charAt(2),
                    color.charAt(3), color.charAt(4), color.charAt(5));
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);

        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static List<String> colorizeHex(List<String> messages) {
        if (messages == null) {
            return null;
        }

        List<String> coloredMessages = new ArrayList<>();
        for (String message : messages) {
            coloredMessages.add(colorizeHex(message));
        }
        return coloredMessages;
    }

}