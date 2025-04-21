package com.mongenscave.mcScheduler.util;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigUtil {

    private final JavaPlugin plugin;
    private YamlDocument config;
    private YamlDocument webhooks;
    private YamlDocument hooks;
    private final Map<String, YamlDocument> guisMap = new HashMap<>();

    public ConfigUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        setupConfig();
        setupWebhooks();
        setupHooks();
        loadGuis();
    }

    public void setupConfig() {
        try {
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                plugin.saveResource("config.yml", false);
            }

            config = YamlDocument.create(configFile,
                    Objects.requireNonNull(plugin.getResource("config.yml")),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.DEFAULT, DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setKeepAll(true)
                            .setVersioning(new BasicVersioning("version")).build());

            config.update();
        } catch (IOException ex) {
            plugin.getLogger().severe("Error loading or creating config.yml: " + ex.getMessage());
        }
    }

    public void setupWebhooks() {
        try {
            File webhooksFile = new File(plugin.getDataFolder(), "webhooks.yml");
            if (!webhooksFile.exists()) {
                plugin.saveResource("webhooks.yml", false);
            }

            webhooks = YamlDocument.create(webhooksFile,
                    Objects.requireNonNull(plugin.getResource("webhooks.yml")),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.DEFAULT, DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setKeepAll(true)
                            .setVersioning(new BasicVersioning("version")).build());

            webhooks.update();
        } catch (IOException ex) {
            plugin.getLogger().severe("Error loading or creating webhooks.yml: " + ex.getMessage());
        }
    }

    public void setupHooks() {
        try {
            File hooksFile = new File(plugin.getDataFolder(), "hooks.yml");
            if (!hooksFile.exists()) {
                plugin.saveResource("hooks.yml", false);
            }

            hooks = YamlDocument.create(hooksFile,
                    Objects.requireNonNull(plugin.getResource("hooks.yml")),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.DEFAULT, DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setKeepAll(true)
                            .setVersioning(new BasicVersioning("version")).build());

            hooks.update();
        } catch (IOException ex) {
            plugin.getLogger().severe("Error loading or creating hooks.yml: " + ex.getMessage());
        }
    }

    public void loadGuis() {
        File guisFolder = new File(plugin.getDataFolder(), "guis");
        if (!guisFolder.exists()) {
            guisFolder.mkdirs();
        }

        guisMap.clear();

        String[] guiFiles = {"commands.yml", "editor.yml", "events.yml", "times.yml"};

        for (String guiFileName : guiFiles) {
            File effectFile = new File(guisFolder, guiFileName);
            if (!effectFile.exists()) {
                plugin.saveResource("guis/" + guiFileName, false);
            }
        }

        File[] loadedGuiFiles = guisFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (loadedGuiFiles != null && loadedGuiFiles.length > 0) {
            for (File guiFile : loadedGuiFiles) {
                try {
                    String guiName = guiFile.getName().replace(".yml", "");
                    YamlDocument guiDocument = YamlDocument.create(guiFile,
                            GeneralSettings.builder().setUseDefaults(false).build(),
                            LoaderSettings.DEFAULT, DumperSettings.DEFAULT,
                            UpdaterSettings.builder().setVersioning(new BasicVersioning("version")).build());

                    guisMap.put(guiName, guiDocument);
                } catch (IOException ex) {
                    plugin.getLogger().severe("Error loading gui file: " + guiFile.getName() + " - " + ex.getMessage());
                }
            }
        } else {
            plugin.getLogger().warning("No gui files found in the guis folder.");
        }
    }

    public YamlDocument getConfig() {
        return config;
    }

    public YamlDocument getWebhooks() {
        return webhooks;
    }

    public YamlDocument getHooks() {
        return hooks;
    }

    public YamlDocument getGui(String guiName) {
        return guisMap.get(guiName);
    }

    public void reloadConfig() {
        try {
            config.reload();
            webhooks.reload();
            hooks.reload();
            loadGuis();
        } catch (IOException ex) {
            plugin.getLogger().severe("Error reloading configuration files: " + ex.getMessage());
        }
    }
}
