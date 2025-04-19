package com.mongenscave.mcScheduler.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongenscave.mcScheduler.Scheduler;
import com.mongenscave.mcScheduler.manager.SchedulerManager;
import me.clip.placeholderapi.PlaceholderAPI;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookUtil {

    public static void sendWebhook(String name) {
        String urlKey = "webhooks." + name + ".webhook-url";

        String url = Scheduler.getInstance().getConfigUtil().getWebhooks().getString(urlKey, null);

        JsonObject jsonMessage = formatWebhookMessage(name);
        sendMessage(url, jsonMessage.toString());
    }

    private static JsonObject formatWebhookMessage(String name) {

        JsonObject embed = new JsonObject();
        String title = PlaceholderAPI.setPlaceholders(null, Scheduler.getInstance().getConfigUtil().getWebhooks().getString("webhooks." + name + ".embed.title", ""));
        embed.addProperty("title", title);

        String description = PlaceholderAPI.setPlaceholders(null, Scheduler.getInstance().getConfigUtil().getWebhooks().getString("webhooks." + name + ".embed.description", ""));
        embed.addProperty("description", description);

        int color;
        try {
            color = Integer.decode(Scheduler.getInstance().getConfigUtil().getWebhooks().getString("webhook." + name + ".embed.color", "#36393F").replace("#", "0x"));
        } catch (NumberFormatException e) {
            System.out.println("[WARN] Invalid color format in configuration, using default.");
            color = 0x36393F;
        }
        embed.addProperty("color", color);

        JsonObject footer = new JsonObject();
        String footerText = PlaceholderAPI.setPlaceholders(null, Scheduler.getInstance().getConfigUtil().getWebhooks().getString("webhook." + name + ".embed.footer.text", ""));
        footer.addProperty("text", footerText);
        embed.add("footer", footer);

        if (Scheduler.getInstance().getConfigUtil().getWebhooks().getBoolean("webhook." + name + ".embed.timestamp", false)) {
            embed.addProperty("timestamp", java.time.Instant.now().toString());
        }

        JsonArray embeds = new JsonArray();
        embeds.add(embed);

        JsonObject jsonMessage = new JsonObject();
        jsonMessage.add("embeds", embeds);

        return jsonMessage;
    }

    private static void sendMessage(String urlString, String jsonMessage) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            SchedulerManager.runAsync(() -> {
                try {
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonMessage.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 429) {
                        System.out.println("[WARN] Rate limit reached. Retry-After: " + connection.getHeaderField("Retry-After"));
                    } else if (responseCode >= 400) {
                        System.out.println("[ERROR] Failed to send webhook. Response code: " + responseCode);
                    }
                } catch (Exception e) {
                    System.out.println("[WARNING] Exception occurred while sending webhook");
                }
            });
        } catch (Exception e) {
            System.out.println("[WARNING] Exception occurred while sending webhook");
        }
    }
}
