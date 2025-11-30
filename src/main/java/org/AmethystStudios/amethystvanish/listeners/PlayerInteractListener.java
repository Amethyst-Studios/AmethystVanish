package org.AmethystStudios.amethystvanish.listeners;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final AmethystVanish plugin;

    public PlayerInteractListener(AmethystVanish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getVanishManager().isVanished(player)) {
            return;
        }

        // Check for no interact mode
        if (plugin.getVanishManager().hasNoInteract(player)) {
            event.setCancelled(true);
            return;
        }

        // Silent chest viewing
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && block.getState() instanceof Container) {
                if (plugin.getVanishManager().hasSilentChestView(player)) {
                    // Prevent the open sound
                    event.setCancelled(true);
                    // Open the container silently
                    player.openInventory(((Container) block.getState()).getInventory());
                }
            }
        }
    }
}
