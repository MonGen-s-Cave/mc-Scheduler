package hu.leonidas.mcScheduler.util;

import java.util.ArrayList;
import java.util.List;

public class EventConfig {
    private String name;
    private List<String> times;
    private List<String> commands;

    public EventConfig() {
        this.times = new ArrayList<>();
        this.commands = new ArrayList<>();
    }

    public EventConfig(String name, List<String> times, List<String> commands) {
        this();
        this.name = name;
        this.times.addAll(times);
        this.commands.addAll(commands);
    }

    public List<String> getTimes() {
        return times;
    }

    public List<String> getCommands() {
        return commands;
    }
}
