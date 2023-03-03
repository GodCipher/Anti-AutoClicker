package dev.luzifer.antiac.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FlagEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final int clicks, violations;
    private final double averageCps;

    public FlagEvent(final Player player, final int clicks, final double averageCps, final int violations) {
        this.player = player;
        this.clicks = clicks;
        this.averageCps = averageCps;
        this.violations = violations;
    }

    public static HandlerList getHandlerList() {
        return FlagEvent.handlers;
    }

    public HandlerList getHandlers() {
        return FlagEvent.handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getClicks() {
        return this.clicks;
    }

    public double getAverageCps() {
        return this.averageCps;
    }

    public int getViolations() {
        return this.violations;
    }
}