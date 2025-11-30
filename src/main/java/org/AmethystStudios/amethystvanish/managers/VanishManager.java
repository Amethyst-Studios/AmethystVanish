package org.AmethystStudios.amethystvanish.managers;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.AmethystStudios.amethystvanish.utils.VanishPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class VanishManager {

    private final AmethystVanish plugin;
    private final Map<UUID, VanishPlayer> vanishedPlayers;
    private final Set<UUID> silentChestViewers;
    private final Set<UUID> noPickupPlayers;
    private final Set<UUID> noInteractPlayers;

    public VanishManager(AmethystVanish plugin) {
        this.plugin = plugin;
        this.vanishedPlayers = new HashMap<>();
        this.silentChestViewers = new HashSet<>();
        this.noPickupPlayers = new HashSet<>();
        this.noInteractPlayers = new HashSet<>();
    }

    public void enableVanish(Player player, boolean silent) {
        UUID uuid = player.getUniqueId();
        VanishPlayer vanishPlayer = new VanishPlayer(player);
        vanishedPlayers.put(uuid, vanishPlayer);

        // Apply vanish effects
        applyVanishEffects(player);

        // Apply custom effects based on configuration
        applyCustomEffects(player);

        // Send messages
        if (!silent) {
            String message = plugin.getMessageManager().getMessage("vanish.enabled");
            player.sendMessage(message);
        }

        // Log action
        plugin.getLogger().info(player.getName() + " has enabled vanish");
    }

    public void disableVanish(Player player, boolean silent) {
        UUID uuid = player.getUniqueId();
        VanishPlayer vanishPlayer = vanishedPlayers.remove(uuid);

        if (vanishPlayer != null) {
            // Remove vanish effects
            removeVanishEffects(player);

            // Remove from special sets
            silentChestViewers.remove(uuid);
            noPickupPlayers.remove(uuid);
            noInteractPlayers.remove(uuid);

            if (!silent) {
                String message = plugin.getMessageManager().getMessage("vanish.disabled");
                player.sendMessage(message);
            }

            plugin.getLogger().info(player.getName() + " has disabled vanish");
        }
    }

    private void applyVanishEffects(Player player) {
        // Hide player from other players
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.hasPermission("amethystvanish.see")) {
                online.hidePlayer(plugin, player);
            }
        }

        // Apply potion effects
        if (plugin.getConfigManager().getConfig().getBoolean("vanish-effects.night-vision", true)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        }

        if (plugin.getConfigManager().getConfig().getBoolean("vanish-effects.speed", false)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
        }
    }

    private void applyCustomEffects(Player player) {
        ConfigManager configManager = plugin.getConfigManager();

        // Apply custom particle effects
        if (configManager.getConfig().getBoolean("custom-effects.particles.enabled", false)) {
            plugin.getEffectManager().startParticleEffect(player);
        }

        // Apply sound effects
        if (configManager.getConfig().getBoolean("custom-effects.sounds.enabled", false)) {
            plugin.getEffectManager().playVanishSound(player);
        }
    }

    private void removeVanishEffects(Player player) {
        // Show player to all players
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(plugin, player);
        }

        // Remove potion effects
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.SPEED);

        // Stop custom effects
        plugin.getEffectManager().stopParticleEffect(player);
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.containsKey(player.getUniqueId());
    }

    public VanishPlayer getVanishPlayer(Player player) {
        return vanishedPlayers.get(player.getUniqueId());
    }

    public void toggleSilentChestView(Player player) {
        UUID uuid = player.getUniqueId();
        if (silentChestViewers.contains(uuid)) {
            silentChestViewers.remove(uuid);
        } else {
            silentChestViewers.add(uuid);
        }
    }

    public boolean hasSilentChestView(Player player) {
        return silentChestViewers.contains(player.getUniqueId());
    }

    public void toggleNoPickup(Player player) {
        UUID uuid = player.getUniqueId();
        if (noPickupPlayers.contains(uuid)) {
            noPickupPlayers.remove(uuid);
        } else {
            noPickupPlayers.add(uuid);
        }
    }

    public boolean hasNoPickup(Player player) {
        return noPickupPlayers.contains(player.getUniqueId());
    }

    public void toggleNoInteract(Player player) {
        UUID uuid = player.getUniqueId();
        if (noInteractPlayers.contains(uuid)) {
            noInteractPlayers.remove(uuid);
        } else {
            noInteractPlayers.add(uuid);
        }
    }

    public boolean hasNoInteract(Player player) {
        return noInteractPlayers.contains(player.getUniqueId());
    }

    public void disableAllVanish() {
        for (UUID uuid : new HashSet<>(vanishedPlayers.keySet())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                disableVanish(player, true);
            }
        }
    }

    public Collection<VanishPlayer> getVanishedPlayers() {
        return vanishedPlayers.values();
    }
}
