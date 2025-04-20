package com.mongenscave.mcScheduler.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RunSchedulerEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final String eventName;
    private boolean isCancelled;

    public RunSchedulerEvent(String eventName) {
        this.eventName = eventName;
        this.isCancelled = false;
    }

    public String getEventName() {
        return eventName;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
