package com.mongenscave.mcScheduler.api;

import com.mongenscave.mcScheduler.Scheduler;

public class SchedulerAPI {
    private static SchedulerAPI instance;

    public SchedulerAPI() {}

    public static SchedulerAPI getInstace() {
            if (instance == null) {
                throw new IllegalStateException("SchedulerAPI is not initialized!");
            }
            return instance;
    }

    public static void initialize(SchedulerAPI apiInstance) {
        if (instance != null) {
            throw new IllegalStateException("SchedulerAPI is already initialized!");
        }
        instance = apiInstance;
    }

    public Scheduler getPluginInstance() {
        return Scheduler.getInstance();
    }
}
