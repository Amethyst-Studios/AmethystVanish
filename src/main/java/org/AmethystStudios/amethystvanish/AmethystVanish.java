package org.AmethystStudios.amethystvanish;

import org.AmethystStudios.amethystvanish.commands.VanishCommand;
import org.AmethystStudios.amethystvanish.listeners.*;
import org.AmethystStudios.amethystvanish.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class AmethystVanish extends JavaPlugin {

    private static AmethystVanish instance;
    private VanishManager vanishManager;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private GUIManager guiManager;
    private EffectManager effectManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            // Initialize managers first
            initializeManagers();

            // Load configurations
            loadConfigurations();

            // Register commands
            registerCommands();

            // Register listeners
            registerListeners();

            // Initialize database
            initializeDatabase();

            getLogger().log(Level.INFO, "AmethystVanish has been enabled successfully!");

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable AmethystVanish! Plugin will be disabled.", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {
        // Save data and cleanup
        if (databaseManager != null) {
            try {
                databaseManager.saveAllPlayerData();
                databaseManager.closeConnection();
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Error during database cleanup", e);
            }
        }

        // Disable vanish for all players
        if (vanishManager != null) {
            try {
                vanishManager.disableAllVanish();
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Error disabling vanish for players", e);
            }
        }

        // Stop all effects
        if (effectManager != null) {
            try {
                effectManager.clearAllEffects();
            } catch (Exception e) {
                getLogger().log(Level.WARNING, "Error clearing effects", e);
            }
        }

        getLogger().log(Level.INFO, "AmethystVanish has been disabled!");
    }

    private void initializeManagers() {
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        guiManager = new GUIManager(this);
        effectManager = new EffectManager(this);
        vanishManager = new VanishManager(this);
        databaseManager = new DatabaseManager(this);
    }

    private void loadConfigurations() {
        configManager.loadConfigs();
        messageManager.loadMessages();
        guiManager.loadGUI();
    }

    private void registerCommands() {
        try {
            // Create command instances
            VanishCommand vanishCommand = new VanishCommand(this);

            // Register commands
            getCommand("vanish").setExecutor(vanishCommand);
            getCommand("vanish").setTabCompleter(vanishCommand);

            // Register alias
            getCommand("v").setExecutor(vanishCommand);
            getCommand("v").setTabCompleter(vanishCommand);

        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to register commands!", e);
            throw e;
        }
    }

    private void registerListeners() {
        try {
            Bukkit.getPluginManager().registerEvents(new PlayerJoinLeaveListener(this), this);
            Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
            Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
            Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);
            Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
            Bukkit.getPluginManager().registerEvents(new MoveListener(this), this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to register listeners!", e);
            throw e;
        }
    }

    private void initializeDatabase() {
        try {
            databaseManager.initializeDatabase();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to initialize database!", e);
            throw e;
        }
    }

    // Getters for managers
    public static AmethystVanish getInstance() {
        return instance;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
