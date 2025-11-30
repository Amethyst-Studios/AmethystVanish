package org.AmethystStudios.amethystvanish.managers;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {

    private final AmethystVanish plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(AmethystVanish plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        try {
            // Create data folder if it doesn't exist
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            // Main config
            plugin.saveDefaultConfig();
            config = plugin.getConfig();
            configFile = new File(plugin.getDataFolder(), "config.yml");

            // Create other config files if they don't exist
            createFile("messages.yml");
            createFile("gui.yml");
            createFile("effects.yml");

            plugin.getLogger().info("Configurations loaded successfully");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load configurations", e);
            throw new RuntimeException("Configuration loading failed", e);
        }
    }

    private void createFile(String fileName) {
        try {
            File file = new File(plugin.getDataFolder(), fileName);
            if (!file.exists()) {
                plugin.saveResource(fileName, false);
                plugin.getLogger().info("Created default " + fileName);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to create " + fileName, e);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    public FileConfiguration getGUI() {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "gui.yml"));
    }

    public FileConfiguration getEffects() {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "effects.yml"));
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
        }
    }

    public void reloadConfigs() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
}
