package hu.leonidas.mcScheduler.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigUtil {

    private static FileConfiguration config;
    private static File configFile;

    public static ZoneId timeZone;
    public static ZonedDateTime zonedDateTime;

    public static Boolean updateChecker;
    public static String prefix;
    public static String updateCheckerMessage;
    public static String reloadMessage;
    public static String permissionMessage;
    public static String argsMessage;
    public static String placeholder_format;
    public static Map<String, EventConfig> events = new ConcurrentHashMap<>();

    private static void loadValues() {
        ConfigurationSection settings = config.getConfigurationSection("Settings");
        if (settings == null) {
            Console.warning("Missing 'Settings' section in the configuration file.");
            throw new IllegalStateException("Configuration file is missing required 'Settings' section.");
        }

        String timeZoneString = settings.getString("timezone", "Europe/London").trim();
        try {
            timeZone = ZoneId.of(timeZoneString);
            zonedDateTime = ZonedDateTime.now(timeZone);
        } catch (Exception e) {
            Console.warning("Invalid timezone in config: " + timeZoneString + ". Falling back to default: Europe/London.");
            timeZone = ZoneId.of("Europe/London");
        }

        updateChecker = settings.getBoolean("enable_update_checker", true);
        updateCheckerMessage = ChatUtil.colorizeHexPrefix(settings.getString("update_notify", "%prefix% &aA new version of the plugin is available! &8(&c%current_version% &f→ &e%latest_version%&8)"));
        prefix = ChatUtil.colorizeHexPrefix(settings.getString("prefix", "&8[&#56C9CDScheduler&8] »"));
        reloadMessage = settings.getString("reload_message", "%prefix% &aPlugin files have been successfully reloaded.");
        permissionMessage = settings.getString("no_permission_message", "%prefix% &cYou do not have permission to use this command.");
        argsMessage = settings.getString("usage_message", "%prefix% &fUsage: &7/mcscheduler reload");
        placeholder_format = settings.getString("placeholder_format", "%days%:%hours%:%minutes%:%seconds%");

        ConfigurationSection eventsSection = config.getConfigurationSection("Events");
        if (eventsSection != null) {
            for (String eventName : eventsSection.getKeys(false)) {
                ConfigurationSection eventSection = eventsSection.getConfigurationSection(eventName);
                if (eventSection == null) {
                    Console.warning("Event section '" + eventName + "' is missing or invalid.");
                    continue;
                }

                int required_players = eventSection.getInt("required_players", 0);

                List<String> times = new ArrayList<>();
                List<String> no_enough_commands = new ArrayList<>();
                List<String> commands = eventSection.getStringList("commands");

                if (eventSection.contains("no_enough_player_commands")) {
                    no_enough_commands = eventSection.getStringList("no_enough_player_commands");
                }

                for (String time : eventSection.getStringList("times")) {
                    DateUtil dateUtil = new DateUtil();
                    if (dateUtil.isDateValid(time)) {
                        times.add(time);
                    } else {
                        Console.warning("Invalid time format in " + eventName + " event config: " + time);
                    }
                }

                events.put(eventName, new EventConfig(eventName, times, commands, no_enough_commands, required_players));
            }
        } else {
            Console.warning("No 'Events' section found in configuration. Make sure to define at least one event.");
        }
    }

    public static void init(Plugin plugin) {
        createConfig(plugin);
        load();
    }

    public static void load() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            loadValues();
        } catch (Exception e) {
            Console.warning("Failed to load configuration file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createConfig(Plugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
    }
}
