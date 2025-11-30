package org.AmethystStudios.amethystvanish.utils;

import org.bukkit.entity.Player;

import java.time.Instant;

public class VanishPlayer {

    private final Player player;
    private final Instant vanishTime;
    private boolean silentChest;
    private boolean noPickup;
    private boolean noInteract;

    public VanishPlayer(Player player) {
        this.player = player;
        this.vanishTime = Instant.now();
        this.silentChest = false;
        this.noPickup = false;
        this.noInteract = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Instant getVanishTime() {
        return vanishTime;
    }

    public long getVanishDuration() {
        return Instant.now().getEpochSecond() - vanishTime.getEpochSecond();
    }

    public boolean hasSilentChest() {
        return silentChest;
    }

    public void setSilentChest(boolean silentChest) {
        this.silentChest = silentChest;
    }

    public boolean hasNoPickup() {
        return noPickup;
    }

    public void setNoPickup(boolean noPickup) {
        this.noPickup = noPickup;
    }

    public boolean hasNoInteract() {
        return noInteract;
    }

    public void setNoInteract(boolean noInteract) {
        this.noInteract = noInteract;
    }
}
