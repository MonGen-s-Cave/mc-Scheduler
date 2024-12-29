package hu.leonidas.mcScheduler.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Console {
    private static Server server;
    private static Logger logger;
    public static MiniMessage miniMessage;

    public static void init(Plugin plugin) {
        server = plugin.getServer();
        logger = plugin.getLogger();
        miniMessage = MiniMessage.miniMessage();
    }

    public static void executeCommands(List<String> commands) {
        if (commands == null || commands.isEmpty()) {
            return;
        }

        for (String command : commands) {
            if (server != null) {
                try {
                    if (command.startsWith("[COMMAND]")) {
                        String cmd = command.replace("[COMMAND]", "").trim();
                        server.dispatchCommand(server.getConsoleSender(), cmd);
                    } else if (command.startsWith("[MESSAGE]")) {
                        String msgContent = command.replace("[MESSAGE]", "").trim();
                        msgContent = PlaceholderAPI.setPlaceholders(null, msgContent);
                        msgContent = ChatUtil.colorizeHex(msgContent);
                        server.broadcastMessage(msgContent);
                    } else if (command.startsWith("[ACTIONBAR]")) {
                        String msgContent = command.replace("[ACTIONBAR]", "").trim();
                        msgContent = PlaceholderAPI.setPlaceholders(null, msgContent);
                        msgContent = ChatUtil.colorizeHex(msgContent);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendActionBar(msgContent);
                        }
                    } else if (command.startsWith("[TITLE]")) {
                        String msgContent = command.replace("[TITLE]", "").trim();
                        msgContent = PlaceholderAPI.setPlaceholders(null, msgContent);
                        msgContent = ChatUtil.colorizeHex(msgContent);
                        String subtitle = "";
                        for(String cmd : commands){
                            if(cmd.startsWith("[SUBTITLE]")){
                                subtitle = cmd.replace("[SUBTITLE]", "").trim();
                                subtitle = PlaceholderAPI.setPlaceholders(null, subtitle);
                                subtitle = ChatUtil.colorizeHex(subtitle);
                            }
                        }
                            for(Player player : Bukkit.getOnlinePlayers()){
                                player.sendTitle(msgContent, subtitle, 0, 120, 0);
                            }
                            for(Player player : Bukkit.getOnlinePlayers()){
                                player.sendTitle(msgContent, subtitle, 0, 120, 0);
                            }

                    }
                } catch (Exception e) {
                    error("Failed to execute command: " + command);
                }
            }
        }
    }

    public static void info(String msg) {
        if (logger != null) {
            logger.log(Level.INFO, msg);
        }
    }

    public static void warning(String msg) {
        if (logger != null) {
            logger.log(Level.WARNING, msg);
        }
    }

    public static void error(String msg) {
        if (logger != null) {
            logger.log(Level.SEVERE, msg);
        }
    }
}
