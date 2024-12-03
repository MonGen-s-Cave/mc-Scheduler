package hu.leonidas.mcScheduler.manager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import hu.leonidas.mcScheduler.utils.DateUtil;

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
                return nextRunTime;
            } else {
                return "There are no upcoming events";
            }
        }

        return "Invalid placeholder";
    }
}
