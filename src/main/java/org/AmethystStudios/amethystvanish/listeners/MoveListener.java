package org.AmethystStudios.amethystvanish.listeners;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

    private final AmethystVanish plugin;

    public MoveListener(AmethystVanish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Optional: Prevent movement while in no-interact mode
        if (plugin.getVanishManager().isVanished(player) &&
                plugin.getVanishManager().hasNoInteract(player) &&
                plugin.getConfigManager().getConfig().getBoolean("no-interact.prevent-movement", false)) {

            // Check if player actually moved to a different block
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                    event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                    event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                event.setTo(event.getFrom());
            }
        }
    }
}
