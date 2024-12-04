package hu.leonidas.mcScheduler.util;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class UpdateChecker implements Listener {
    private final String currentVersion;
    private String latestVersion = null;
    private boolean isUpToDate = true;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.currentVersion = plugin.getDescription().getVersion();
        this.resourceId = resourceId;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

            this.latestVersion = fetchLatestVersion();
            this.isUpToDate = checkIfLatestVersion(currentVersion);

            if (latestVersion == null || isUpToDate) return;

            Bukkit.getConsoleSender().sendMessage(createUpdateMessage());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (latestVersion == null || isUpToDate) return;
        if (!event.getPlayer().hasPermission("mcscheduler.update-notify")) return;

        event.getPlayer().sendMessage(createUpdateMessage());
    }

    private String createUpdateMessage() {
        return ChatUtil.colorizeHex(ConfigUtil.updateCheckerMessage.replace("%current_version%", currentVersion).replace("%latest_version%", latestVersion));
    }

    @Nullable
    private String fetchLatestVersion() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.polymart.org/v1/getResourceInfoSimple/?resource_id=" + resourceId + "&key=version"))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean checkIfLatestVersion(String current) {
        return compareVersions(latestVersion, current) <= 0;
    }

    private int compareVersions(String version1, String version2) {
        if (version1 == null || version2 == null) return 0;

        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");

        int v1Major = Integer.parseInt(v1Parts[0]);
        int v1Minor = Integer.parseInt(v1Parts[1]);
        int v1Patch = Integer.parseInt(v1Parts[2]);

        int v2Major = Integer.parseInt(v2Parts[0]);
        int v2Minor = Integer.parseInt(v2Parts[1]);
        int v2Patch = Integer.parseInt(v2Parts[2]);

        if (v1Major != v2Major) return v1Major - v2Major;
        if (v1Minor != v2Minor) return v1Minor - v2Minor;
        return v1Patch - v2Patch;
    }
}