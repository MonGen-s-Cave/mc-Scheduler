package hu.leonidas.mcScheduler.manager;

import hu.leonidas.mcScheduler.util.ConfigUtil;
import hu.leonidas.mcScheduler.util.Console;
import hu.leonidas.mcScheduler.util.DateUtil;
import hu.leonidas.mcScheduler.util.EventConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class SchedulerManager extends JavaPlugin {

    public static void scheduleCommand() {
        DateUtil dateUtil = new DateUtil();

        Bukkit.getScheduler().runTaskTimer(hu.leonidas.mcScheduler.Scheduler.getPlugin(hu.leonidas.mcScheduler.Scheduler.class), () -> {
            ZonedDateTime now = ZonedDateTime.now(ConfigUtil.timeZone);

            for (Map.Entry<String, EventConfig> entry : ConfigUtil.events.entrySet()) {
                EventConfig eventConfig = entry.getValue();
                List<String> times = eventConfig.getTimes().stream().distinct().toList();
                int requiredPlayers = eventConfig.getRequired_players();
                int onlinePlayersCount = Bukkit.getOnlinePlayers().size();

                for (String time : times) {
                        List<String> splitDate = dateUtil.splitDate(time);
                        List<String> actuallyDate = dateUtil.getNow();

                        if (actuallyDate.get(0).equalsIgnoreCase(splitDate.get(0)) &&
                                actuallyDate.get(1).equals(splitDate.get(1)) &&
                                actuallyDate.get(2).equals(splitDate.get(2)) &&
                                actuallyDate.get(3).equals(splitDate.get(3))) {
                            if (onlinePlayersCount >= requiredPlayers) {
                                Console.executeCommands(eventConfig.getCommands());
                            } else if (!eventConfig.getNo_enough_commands().isEmpty()) {
                                Console.executeCommands(eventConfig.getNo_enough_commands());
                            }
                        }
                }
            }
        }, 0L, 20L);
    }
}
