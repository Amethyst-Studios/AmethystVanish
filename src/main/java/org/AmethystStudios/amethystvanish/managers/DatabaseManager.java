package org.AmethystStudios.amethystvanish.managers;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final AmethystVanish plugin;
    private Connection connection;
    private boolean isConnected = false;

    public DatabaseManager(AmethystVanish plugin) {
        this.plugin = plugin;
    }

    public void initializeDatabase() {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        String storageType = config.getString("storage.type", "SQLITE").toUpperCase();

        try {
            // Load JDBC driver
            if (storageType.equals("MYSQL")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else {
                Class.forName("org.sqlite.JDBC");
            }

            if (storageType.equals("MYSQL")) {
                setupMySQL();
            } else {
                setupSQLite();
            }

            createTables();
            isConnected = true;
            plugin.getLogger().info("Database initialized successfully with " + storageType);

        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "JDBC driver not found for database type", e);
            throw new RuntimeException("JDBC driver not available", e);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize database", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private void setupSQLite() throws SQLException {
        // Ensure data folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        String url = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/data.db";
        connection = DriverManager.getConnection(url);

        // Enable foreign keys and better performance for SQLite
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("PRAGMA journal_mode = WAL");
            stmt.execute("PRAGMA synchronous = NORMAL");
            stmt.execute("PRAGMA cache_size = 10000");
        }
    }

    private void setupMySQL() throws SQLException {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        String host = config.getString("storage.mysql.host", "localhost");
        int port = config.getInt("storage.mysql.port", 3306);
        String database = config.getString("storage.mysql.database", "amethystvanish");
        String username = config.getString("storage.mysql.username", "root");
        String password = config.getString("storage.mysql.password", "");
        boolean useSSL = config.getBoolean("storage.mysql.useSSL", false);

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database +
                "?useSSL=" + useSSL +
                "&autoReconnect=true" +
                "&characterEncoding=utf8" +
                "&serverTimezone=UTC";

        connection = DriverManager.getConnection(url, username, password);

        // Test connection
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SELECT 1");
        }
    }

    private void createTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS vanish_data (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "vanished BOOLEAN DEFAULT FALSE, " +
                "silent_chest BOOLEAN DEFAULT FALSE, " +
                "no_pickup BOOLEAN DEFAULT FALSE, " +
                "no_interact BOOLEAN DEFAULT FALSE, " +
                "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void savePlayerData(UUID uuid, boolean vanished, boolean silentChest, boolean noPickup, boolean noInteract) {
        if (!isConnected || connection == null) {
            plugin.getLogger().warning("Database not connected, skipping save for " + uuid);
            return;
        }

        String sql = "INSERT OR REPLACE INTO vanish_data (uuid, vanished, silent_chest, no_pickup, no_interact) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setBoolean(2, vanished);
            stmt.setBoolean(3, silentChest);
            stmt.setBoolean(4, noPickup);
            stmt.setBoolean(5, noInteract);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data for " + uuid, e);
        }
    }

    // NEW: Load player data from database
    public PlayerData loadPlayerData(UUID uuid) {
        if (!isConnected || connection == null) {
            plugin.getLogger().warning("Database not connected, cannot load data for " + uuid);
            return null;
        }

        String sql = "SELECT vanished, silent_chest, no_pickup, no_interact FROM vanish_data WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new PlayerData(
                        rs.getBoolean("vanished"),
                        rs.getBoolean("silent_chest"),
                        rs.getBoolean("no_pickup"),
                        rs.getBoolean("no_interact")
                );
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load player data for " + uuid, e);
        }

        return null;
    }

    // NEW: Check if player has data in database
    public boolean hasPlayerData(UUID uuid) {
        if (!isConnected || connection == null) return false;

        String sql = "SELECT 1 FROM vanish_data WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to check player data for " + uuid, e);
        }

        return false;
    }

    // NEW: Delete player data
    public void deletePlayerData(UUID uuid) {
        if (!isConnected || connection == null) return;

        String sql = "DELETE FROM vanish_data WHERE uuid = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to delete player data for " + uuid, e);
        }
    }

    public void saveAllPlayerData() {
        if (!isConnected || connection == null) {
            plugin.getLogger().warning("Database not connected, skipping save all data");
            return;
        }

        // Save data for all vanished players
        plugin.getVanishManager().getVanishedPlayers().forEach(vanishPlayer -> {
            Player player = vanishPlayer.getPlayer();
            if (player != null) {
                savePlayerData(
                        player.getUniqueId(),
                        true,
                        plugin.getVanishManager().hasSilentChestView(player),
                        plugin.getVanishManager().hasNoPickup(player),
                        plugin.getVanishManager().hasNoInteract(player)
                );
            }
        });

        plugin.getLogger().info("Saved data for all vanished players");
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                isConnected = false;
                plugin.getLogger().info("Database connection closed");
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close database connection", e);
            }
        }
    }

    public boolean isConnected() {
        return isConnected && connection != null;
    }

    // NEW: Data class to hold player data
    public static class PlayerData {
        private final boolean vanished;
        private final boolean silentChest;
        private final boolean noPickup;
        private final boolean noInteract;

        public PlayerData(boolean vanished, boolean silentChest, boolean noPickup, boolean noInteract) {
            this.vanished = vanished;
            this.silentChest = silentChest;
            this.noPickup = noPickup;
            this.noInteract = noInteract;
        }

        public boolean isVanished() { return vanished; }
        public boolean hasSilentChest() { return silentChest; }
        public boolean hasNoPickup() { return noPickup; }
        public boolean hasNoInteract() { return noInteract; }
    }
}
