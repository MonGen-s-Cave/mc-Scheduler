package com.mongenscave.mcScheduler.listener;

public class Time {
    private String time;
    private String event;

    public Time(String time, String event) {
        this.time = time;
        this.event = event;
    }

    public String getTime() {
        return time;
    }

    public String getEvent() {
        return event;
    }
}
