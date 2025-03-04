package hu.leonidas.mcScheduler.manager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import hu.leonidas.mcScheduler.util.DateUtil;

import static hu.leonidas.mcScheduler.util.ConfigUtil.placeholder_format;

public class PlaceholderManager extends PlaceholderExpansion {

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Leonidas";
    }

    @Override
    public String getIdentifier() {
        return "scheduler";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        DateUtil dateUtil = new DateUtil();
        if (identifier.startsWith("nextrun_")) {
            String section = identifier.replace("nextrun_", "");
            String nextRunTime = dateUtil.nextRun(section);

            if (nextRunTime != null) {
                String[] split = nextRunTime.split(":");

                if(split.length == 4){
                    String days = split[0];
                    String hours = split[1];
                    String minutes = split[2];
                    String seconds = split[3];
                    return placeholder_format.replace("%days%", days).replace("%hours%", hours).replace("%minutes%", minutes).replace("%seconds%", seconds);
                }
                if(split.length == 3){
                    String hours = split[0];
                    String minutes = split[1];
                    String seconds = split[2];
                    return placeholder_format
//                            .replace("%days% d", "")
//                            .replace("%days%d", "")
//                            .replace("%days%d ", "")
//                            .replace("%days% days", "")
//                            .replace("%days% day", "")
//                            .replace("%days% days ", "")
//                            .replace("%days% day ", "")
//                            .replace("%days%days", "")
//                            .replace("%days%day", "")
//                            .replace("%days%days ", "")
//                            .replace("%days%day ", "")
//                            .replace("%days% ", "")
//                            .replace("%days%", "")
//                            .replace("%days%:", "")
//                            .replace("%days%d:", "")
//                            .replace("%days%day:", "")
//                            .replace("%days%days:", "")
                            .replace("%days%", "00")
                            .replace("%hours%", hours)
                            .replace("%minutes%", minutes)
                            .replace("%seconds%", seconds);
                }
                return nextRunTime;
            } else {
                return "There are no upcoming events";
            }
        }

        return "Invalid placeholder";
    }
}
