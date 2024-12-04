package hu.leonidas.mcScheduler.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigUtil {

    private static FileConfiguration config;
    private static File configFile;

    public static ZoneId timeZone;
    public static Boolean updateChecker;
    public static String prefix;
    public static String updateCheckerMessage;
    public static String reloadMessage;
    public static String permissionMessage;
    public static String argsMessage;
    public static Map<String, EventConfig> events = new ConcurrentHashMap<>();

    private static void loadValues(Plugin plugin) {
        ConfigurationSection settings = config.getConfigurationSection("Settings");
        if (settings == null) {
            Console.warning("Missing 'Settings' section in the configuration file.");
            throw new IllegalStateException("Configuration file is missing required 'Settings' section.");
        }


        String timeZoneString = settings.getString("timezone", "Europe/London").trim();
        try {
            timeZone = ZoneId.of(timeZoneString);
        } catch (Exception e) {
            Console.warning("Invalid timezone in config: " + timeZoneString + ". Falling back to default: Europe/Budapest.");
            timeZone = ZoneId.of("Europe/London");
        }

        updateChecker = settings.getBoolean("enable_update_checker", true);
        updateCheckerMessage = ChatUtil.colorizeHexPrefix(settings.getString("update_notify", "%prefix% &aA new version of the plugin is available! &8(&c%current_version% &f→ &e%latest_version%&8)"));
        prefix = ChatUtil.colorizeHexPrefix(settings.getString("prefix", "&#3597FF[Scheduler]"));
        reloadMessage = settings.getString("reload_message", "%prefix% &aPlugin files have been successfully reloaded.");
        permissionMessage = settings.getString("no_permission_message", "%prefix% &cYou do not have permission to use this command.");
        argsMessage = settings.getString("usage_message", "%prefix% &fUsage: &7/mcscheduler reload");

        ConfigurationSection eventsSection = config.getConfigurationSection("Events");
        if (eventsSection != null) {
            for (String eventName : eventsSection.getKeys(false)) {
                ConfigurationSection eventSection = eventsSection.getConfigurationSection(eventName);
                if (eventSection == null) {
                    Console.warning("Event section '" + eventName + "' is missing or invalid.");
                    continue;
                }

                List<String> times = new ArrayList<>();
                List<String> commands = eventSection.getStringList("commands");

                for(String time : eventSection.getStringList("times"))
                {
                    DateUtil dateUtil = new DateUtil();
                    if(dateUtil.isDateValid(time))
                        times.add(time);
                    else
                        Console.warning("Invalid time format in " + eventName + " event config: " + time);

                }

                events.put(eventName, new EventConfig(eventName, times, commands));
            }
        } else {
            Console.warning("No 'Events' section found in configuration. Make sure to define at least one event.");
        }
    }

    public static void init(Plugin plugin) {
        createConfig(plugin);
        load(plugin);
    }

    public static void load(Plugin plugin) {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            loadValues(plugin);
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
