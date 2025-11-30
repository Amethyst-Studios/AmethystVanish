package org.AmethystStudios.amethystvanish.managers;

import org.AmethystStudios.amethystvanish.AmethystVanish;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EffectManager {

    private final AmethystVanish plugin;
    private final Map<UUID, BukkitRunnable> particleTasks;

    public EffectManager(AmethystVanish plugin) {
        this.plugin = plugin;
        this.particleTasks = new HashMap<>();
    }

    public void startParticleEffect(Player player) {
        stopParticleEffect(player);

        String particleType = plugin.getConfigManager().getEffects().getString("particles.type", "CLOUD");
        int interval = plugin.getConfigManager().getEffects().getInt("particles.interval", 5);

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getVanishManager().isVanished(player) || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                Location loc = player.getLocation();
                World world = player.getWorld();

                try {
                    Particle particle = Particle.valueOf(particleType);
                    world.spawnParticle(particle, loc.clone().add(0, 1, 0), 10, 0.3, 0.5, 0.3, 0.01);
                } catch (IllegalArgumentException e) {
                    // Use default particle if configured particle is invalid
                    world.spawnParticle(Particle.CLOUD, loc.clone().add(0, 1, 0), 10, 0.3, 0.5, 0.3, 0.01);
                }
            }
        };

        task.runTaskTimer(plugin, 0, interval);
        particleTasks.put(player.getUniqueId(), task);
    }

    public void stopParticleEffect(Player player) {
        BukkitRunnable task = particleTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public void playVanishSound(Player player) {
        String soundName = plugin.getConfigManager().getEffects().getString("sounds.vanish", "ENTITY_ENDERMAN_TELEPORT");
        float volume = (float) plugin.getConfigManager().getEffects().getDouble("sounds.volume", 1.0);
        float pitch = (float) plugin.getConfigManager().getEffects().getDouble("sounds.pitch", 1.0);

        try {
            Sound sound = Sound.valueOf(soundName);
            player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            // Use default sound if configured sound is invalid
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, volume, pitch);
        }
    }

    public void playReappearSound(Player player) {
        String soundName = plugin.getConfigManager().getEffects().getString("sounds.reappear", "BLOCK_PORTAL_TRAVEL");
        float volume = (float) plugin.getConfigManager().getEffects().getDouble("sounds.volume", 1.0);
        float pitch = (float) plugin.getConfigManager().getEffects().getDouble("sounds.pitch", 1.0);

        try {
            Sound sound = Sound.valueOf(soundName);
            player.getWorld().playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, volume, pitch);
        }
    }

    public void clearAllEffects() {
        for (BukkitRunnable task : particleTasks.values()) {
            task.cancel();
        }
        particleTasks.clear();
    }
}
