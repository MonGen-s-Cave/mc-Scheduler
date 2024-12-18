package hu.leonidas.mcScheduler.manager;

import hu.leonidas.mcScheduler.util.ConfigUtil;
import hu.leonidas.mcScheduler.util.Console;
import hu.leonidas.mcScheduler.util.DateUtil;
import hu.leonidas.mcScheduler.util.EventConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class SchedulerManager extends JavaPlugin {

    public static void scheduleCommand() {
        DateUtil dateUtil = new DateUtil();

        Bukkit.getScheduler().runTaskTimer(hu.leonidas.mcScheduler.Scheduler.getPlugin(hu.leonidas.mcScheduler.Scheduler.class), new Runnable() {
            @Override
            public void run() {
                List<String> actuallyDate = dateUtil.getNow();
                for (Map.Entry<String, EventConfig> entry : ConfigUtil.events.entrySet()) {
                    List<String> times = entry.getValue().getTimes();

                    for(String time : times) {
                        List<String> list = dateUtil.splitDate(time);
                        if (actuallyDate.get(0).equalsIgnoreCase(list.get(0)) &&
                                actuallyDate.get(1).equals(list.get(1)) &&
                                actuallyDate.get(2).equals(list.get(2)) &&
                                actuallyDate.get(3).equals(list.get(3))) {

                            Console.executeCommands(entry.getValue().getCommands());
                        }
                    }
                }
            }
        }, 0L, 20L);
    }
}
