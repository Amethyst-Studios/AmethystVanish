package org.AmethystStudios.amethystvanish.listeners;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.AmethystStudios.amethystvanish.managers.DatabaseManager; // Hiányzó import
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinLeaveListener implements Listener {

    private final AmethystVanish plugin;

    public PlayerJoinLeaveListener(AmethystVanish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Load player data from database
        DatabaseManager.PlayerData playerData = plugin.getDatabaseManager().loadPlayerData(player.getUniqueId());

        if (playerData != null && playerData.isVanished()) {
            // Re-apply vanish with saved settings
            plugin.getVanishManager().enableVanish(player, true);

            // Apply saved feature toggles
            if (playerData.hasSilentChest()) {
                plugin.getVanishManager().toggleSilentChestView(player);
            }
            if (playerData.hasNoPickup()) {
                plugin.getVanishManager().toggleNoPickup(player);
            }
            if (playerData.hasNoInteract()) {
                plugin.getVanishManager().toggleNoInteract(player);
            }

            plugin.getLogger().info("Restored vanish state for " + player.getName());
        }

        // Hide vanished players from joining player
        plugin.getVanishManager().getVanishedPlayers().forEach(vanishPlayer -> {
            Player vanished = vanishPlayer.getPlayer();
            if (vanished != null && !player.hasPermission("amethystvanish.see")) {
                player.hidePlayer(plugin, vanished);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Save player data and disable vanish
        if (plugin.getVanishManager().isVanished(player)) {
            plugin.getDatabaseManager().savePlayerData(
                    player.getUniqueId(),
                    true,
                    plugin.getVanishManager().hasSilentChestView(player),
                    plugin.getVanishManager().hasNoPickup(player),
                    plugin.getVanishManager().hasNoInteract(player)
            );
            plugin.getVanishManager().disableVanish(player, true);
            plugin.getLogger().info("Saved vanish data for " + player.getName());
        }
    }
}
