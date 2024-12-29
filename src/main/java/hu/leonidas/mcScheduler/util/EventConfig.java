package hu.leonidas.mcScheduler.util;

import java.util.ArrayList;
import java.util.List;

public class EventConfig {
    private String name;
    private int required_players;
    private List<String> times;
    private List<String> commands;
    private List<String> no_enough_commands;

    public EventConfig() {
        this.times = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.no_enough_commands = new ArrayList<>();
    }

    public EventConfig(String name, List<String> times, List<String> commands, List<String> no_enough_commands, int required_players) {
        this();
        this.name = name;
        this.required_players = required_players;
        this.times.addAll(times);
        this.times.addAll(times);
        this.commands.addAll(commands);
        this.no_enough_commands.addAll(no_enough_commands);
    }

    public List<String> getTimes() {
        return times;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getNo_enough_commands() {
        return no_enough_commands;
    }

    public int getRequired_players() {
        return required_players;
    }
}
