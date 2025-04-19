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
import java.util.Objects;

public class ConfigUtil {

    private final JavaPlugin plugin;
    private YamlDocument config;
    private YamlDocument webhooks;
    private YamlDocument hooks;

    public ConfigUtil(JavaPlugin plugin) {
        this.plugin = plugin;
        setupConfig();
        setupWebhooks();
        setupHooks();
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

    public YamlDocument getConfig() {
        return config;
    }

    public YamlDocument getWebhooks() {
        return webhooks;
    }

    public YamlDocument getHooks() {
        return hooks;
    }


    public void reloadConfig() {
        try {
            config.reload();
            webhooks.reload();
            hooks.reload();
        } catch (IOException ex) {
            plugin.getLogger().severe("Error reloading configuration files: " + ex.getMessage());
        }
    }
}
